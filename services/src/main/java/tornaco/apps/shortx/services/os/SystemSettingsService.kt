package tornaco.apps.shortx.services.os

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.provider.Settings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.AppComponent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.CustomContextMenu
import tornaco.apps.shortx.core.proto.common.CustomContextMenuSetting
import tornaco.apps.shortx.core.proto.common.SystemSettingsAccessRecord
import tornaco.apps.shortx.core.proto.fact.OnMenuActionTrigger
import tornaco.apps.shortx.core.rule.RuleObserver
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.settings.defaultCustomContextMenuSetting
import tornaco.apps.shortx.core.toActivityClassName
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.ContextMenuSettingsStore
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.runClearCallingIdentity
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety

class SystemSettingsService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "SystemSettingsService") {
    private val recorder by lazy {
        SystemSettingsAccessRecorder()
    }
    private val contextMenuSettingsStore by lazy {
        ContextMenuSettingsStore(systemServiceScope)
    }

    private val configurationChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Intent.ACTION_CONFIGURATION_CHANGED != intent.action) {
                return
            }
            systemServiceScope.launchSafety("configurationChangeReceiver") {
                val newConfig = context.resources.configuration
                logger.i("configuration change received, new config: $newConfig")
                _currentConfig.emit(newConfig)
            }
        }
    }

    private val _currentConfig = MutableSharedFlow<Configuration>()
    val currentConfig = _currentConfig.asSharedFlow()

    override fun systemReady() {
        super.systemReady()
        contextMenuSettingsStore.readBlocking()
        // Init default settings.
        CustomContextMenu.entries.filterNot { it == CustomContextMenu.UNRECOGNIZED }.forEach {
            val setting = getContextMenuSettingsByMenuNumber(it.number)
            logger.d("CustomContextMenu: $it, setting: $setting")
            if (setting == null) {
                logger.i("Init default settings for CustomContextMenu: $it")
                updateContextMenuSetting(defaultCustomContextMenuSetting(it))
            }
        }


        // For better performance.
        setSettingsCatcherEnabled(false)

        getMinScreenOffTimeout()
        registerReceivers()

        updateContextMenuActivitySettings()

        shortXContext.rule.registerRuleObs(object : RuleObserver() {
            override fun onRuleChanged(id: String) {
                super.onRuleChanged(id)
                updateContextMenuActivitySettings()
            }
        })
    }

    private fun registerReceivers() {
        val configFilter = IntentFilter()
        configFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED)
        ContextCompat.registerReceiver(
            context,
            configurationChangeReceiver,
            configFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun setPointerLocationEnabled(enable: Boolean) {
        runBlocking {
            systemServiceScope.launchSafety("setPointerLocationEnabled") {
                Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.POINTER_LOCATION,
                    if (enable) 1 else 0
                )
            }.join()
        }
    }

    fun isPointerLocationEnabled(): Boolean {
        return runClearCallingIdentity {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.POINTER_LOCATION,
                0
            ) != 0
        } ?: false
    }

    fun isSettingsCatcherEnabled(): Boolean {
        return store.read(tornaco.apps.shortx.services.data.Settings.BoolSettings.EnableSettingsCatcher)
    }

    fun setSettingsCatcherEnabled(enabled: Boolean) {
        store.write(
            tornaco.apps.shortx.services.data.Settings.BoolSettings.EnableSettingsCatcher,
            enabled
        )
    }

    fun onRead(url: String, value: String) {
        recorder.onRead(url, value)
    }

    fun onWrite(url: String, value: String) {
        recorder.onWrite(url, value)
    }

    fun getReadSettingsRecord(): List<SystemSettingsAccessRecord> {
        return recorder.getReadRecords()
    }

    fun getWriteSettingsRecord(): List<SystemSettingsAccessRecord> {
        return recorder.getWriteRecords()
    }

    fun clearWriteSettingsRecord() {
        recorder.clearWriteRecords()
    }

    fun clearReadSettingsRecord() {
        recorder.clearReadRecords()
    }

    fun getMinScreenOffTimeout(): Long {
        return runCatching {
            context.resources.getInteger(
                com.android.internal.R.integer.config_minimumScreenOffTimeout
            ).toLong().also {
                logger.i("getMinScreenOffTimeout: $it")
            }
        }.getOrElse {
            10000L
        }
    }

    fun setScreenOffTimeout(timeout: Long) {
        val checkedOut = maxOf(getMinScreenOffTimeout(), timeout)
        logger.d("setScreenOffTimeout: $timeout")
        Settings.System.putLong(
            context.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            checkedOut
        )
    }

    fun getAllContextMenuSettings(): List<CustomContextMenuSetting> {
        return contextMenuSettingsStore.getAllSettings()
    }

    fun getContextMenuSettingsByMenuNumber(number: Int): CustomContextMenuSetting? {
        return contextMenuSettingsStore.getSettingByMenu(CustomContextMenu.forNumber(number))
    }

    fun updateContextMenuSetting(setting: CustomContextMenuSetting) {
        contextMenuSettingsStore.addSetting(setting)
        updateContextMenuActivitySettings()
    }

    private fun updateContextMenuActivitySettings() {
        systemServiceScope.launchSafety("updateContextMenuActivitySettings") {
            contextMenuSettingsStore.getAllSettings().forEach {
                it.menu.enableComponent(false)
            }
            getMenuActionActivitiesShouldEnable().forEach {
                it.enableComponent(true)
            }
        }
    }

    fun getMenuActionActivitiesShouldEnable(): Set<CustomContextMenu> {
        val res = mutableSetOf<CustomContextMenu>()
        shortXContext.rule.getAllEnabledRules().forEach { rule ->
            val menuFacts =
                rule.factsList.filter { it is_ OnMenuActionTrigger::class.java }
            menuFacts.forEach { fact ->
                val factModel = fact unpack_ OnMenuActionTrigger::class.java
                if (!factModel.isDisabled) {
                    logger.d("getMenuActionActivitiesShouldEnable, add for rule: ${rule.title} ${factModel.menuNumber}")
                    CustomContextMenu.forNumber(factModel.menuNumber)?.let {
                        res.add(it)
                    }
                }
            }
        }
        return res
    }

    private fun CustomContextMenu.enableComponent(enabled: Boolean) {
        val className = toActivityClassName()
        logger.w("Enable: $enabled $this $className")

        shortXContext.appState.setComponentEnabledSetting(
            AppComponent(
                AppPkg.newBuilder().setUserId(myUserId)
                    .setPkgName(BuildPropInternal.APP_PACKAGE_NAME).build(),
                className
            ),
            enabled.select(
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            ),
            PackageManager.DONT_KILL_APP
        )
    }

    fun onMenuActionTrigger(number: Int, text: String) {
        logger.w("onMenuActionTrigger: $number ${text.map { "*" }.take(4)}")
        shortXContext.rule.publishFactChecked(
            OnMenuActionTrigger.getDefaultInstance()
        ) {
            FactPublishData(
                "onMenuActionTrigger",
                OnMenuActionTrigger.newBuilder()
                    .setText(text)
                    .setMenuNumber(number)
                    .build(),
            ) {
                mapOf(ContextDataMapping.CustomContextMenu.selectedText.name to text)
            }
        }
    }
}