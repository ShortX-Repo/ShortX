package tornaco.apps.shortx.services.os

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.provider.Settings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.common.SystemSettingsAccessRecord
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.runClearCallingIdentity
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety

class SystemSettingsService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "SystemSettingsService") {
    private val recorder by lazy {
        SystemSettingsAccessRecorder()
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
        // For better performance.
        setSettingsCatcherEnabled(false)

        getMinScreenOffTimeout()
        registerReceivers()
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
}