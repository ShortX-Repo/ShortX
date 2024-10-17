package tornaco.apps.shortx.services.statusbar

import android.annotation.SuppressLint
import android.content.Context
import android.os.RemoteCallbackList
import android.view.WindowInsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.StatusBarClipState
import tornaco.apps.shortx.core.proto.common.StatusBarClipUiPlacement
import tornaco.apps.shortx.core.proto.common.StatusBarClipUiSettings
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.proto.fact.OnQSTileClick
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.res.remixResId
import tornaco.apps.shortx.core.rule.ToggleObserver
import tornaco.apps.shortx.core.rule.action.wrap
import tornaco.apps.shortx.core.settings.defaultStatusBarTileSetting
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.core.util.statusBarManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.StatusBarTileSettingsStore
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety

sealed interface StatusBarChipEvent {
    val id: String

    data class Click(override val id: String) : StatusBarChipEvent
    data class LongClick(override val id: String) : StatusBarChipEvent
}

class StatusBarService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "StatusBarService") {

    private val tileSettingsStore by lazy {
        StatusBarTileSettingsStore(systemServiceScope)
    }
    private val settingsChangeListeners = RemoteCallbackList<IStatusBarTileSettingsChangeListener>()

    private val statusBarClipStateListeners = RemoteCallbackList<ICallback>()
    private val statusBarClipSettingsListeners = RemoteCallbackList<ICallback>()

    private val statusBarClipState = MutableStateFlow(StatusBarClipState.getDefaultInstance())
    private val statusBarClipSettings =
        MutableStateFlow(StatusBarClipUiSettings.getDefaultInstance())

    private val _statusBarChipEvent = MutableSharedFlow<StatusBarChipEvent>()
    val statusBarChipEvent = _statusBarChipEvent.asSharedFlow()

    private val appContext by lazy {
        AppResources(
            context,
            BuildPropInternal.APP_PACKAGE_NAME
        ).appContext
    }

    override fun start(context: Context) {
        super.start(context)

        shortXContext.rule.registerToggleObs(object : ToggleObserver() {
            override fun onToggleChanged(id: String) {
                super.onToggleChanged(id)
                val toggle = shortXContext.rule.getToggleById(id)
                if (toggle != null && toggle.hasTileSetting()) {
                    val tileSetting = toggle.tileSetting
                    logger.d("onToggleChanged: $id, update tile state for number: ${tileSetting.bondToTileNumber}")
                    systemServiceScope.launchSafety {
                        withContext(Dispatchers.IO) {
                            val isEnabled = shortXContext.rule.isToggleEnabled(id)
                            logger.d("onToggleChanged, updated isEnabled: $isEnabled")
                            val currentTileSetting =
                                getStatusBarTileSettingsByNumber(tileSetting.bondToTileNumber)
                            val updateSetting = StatusBarTileSetting.newBuilder(currentTileSetting)
                                .setIsActive(isEnabled).build()
                            logger.d("updateSetting: $updateSetting")
                            updateStatusBarTileSetting(updateSetting)
                        }
                    }
                }
            }
        })
    }

    override fun systemReady() {
        super.systemReady()
        tileSettingsStore.readBlocking()
        // Init default settings.
        StatusBarTile.entries.filterNot { it == StatusBarTile.UNRECOGNIZED }.forEach {
            val setting = getStatusBarTileSettingsByNumber(it.number)
            logger.d("Tile: $it, setting: $setting")
            if (setting == null) {
                logger.i("Init default settings for tile: $it")
                updateStatusBarTileSetting(defaultStatusBarTileSetting(it))
            }
        }

        systemServiceScope.launchSafety {
            statusBarClipState.collectLatest {
                notifyStatusBarClipStateChanged(it)
            }
        }
        systemServiceScope.launchSafety {
            statusBarClipSettings.collectLatest {
                notifyStatusBarClipSettingsChanged(it)
            }
        }

        setStatusbarClipSettings(
            StatusBarClipUiSettings.newBuilder()
                .setPaddingStart(store.read(Settings.IntSettings.StatusBarChipPaddingStart))
                .setPaddingEnd(store.read(Settings.IntSettings.StatusBarChipPaddingEnd))
                .setPaddingTop(store.read(Settings.IntSettings.StatusBarChipPaddingTop))
                .setPaddingBottom(store.read(Settings.IntSettings.StatusBarChipPaddingBottom))
                .setFontSize(store.read(Settings.IntSettings.StatusBarChipFontSize))
                .setIconSize(store.read(Settings.IntSettings.StatusBarChipIconSize))
                .setPlacement(StatusBarClipUiPlacement.forNumber(store.read(Settings.IntSettings.StatusBarChipUiPlacement)))
                .setBackgroundColor(store.read(Settings.IntSettings.StatusBarChipBgColor))
                .setTextColor(store.read(Settings.IntSettings.StatusBarChipTextColor))
                .setMaxLength(store.read(Settings.IntSettings.StatusBarChipMaxLength))
                .build()
        )

        if (BuildPropInternal.APP_IS_DEBUG) {
            systemServiceScope.launchSafety {
                setStatusBarIcon("ShortX", Remix.Animals.cat)
            }
        }
    }

    fun getAllStatusBarTileSettings(): List<StatusBarTileSetting> {
        return tileSettingsStore.getAllSettings()
    }

    fun getStatusBarTileSettingsByNumber(tileNumber: Int): StatusBarTileSetting? {
        return tileSettingsStore.getSettingByTile(StatusBarTile.forNumber(tileNumber))
    }

    fun updateStatusBarTileSetting(tileSetting: StatusBarTileSetting) {
        tileSettingsStore.addSetting(tileSetting)
        notifySettingChanged(tileSetting)
    }

    fun registerStatusBarTileSettingsChangeListener(listener: IStatusBarTileSettingsChangeListener) {
        settingsChangeListeners.register(listener)
    }

    fun unregisterStatusBarTileSettingsChangeListener(listener: IStatusBarTileSettingsChangeListener) {
        settingsChangeListeners.unregister(listener)
    }

    fun isQSTileOptEnabled(): Boolean {
        return store.read(Settings.BoolSettings.EnableQSTileOpt)
    }

    fun setQSTileOptEnabled(enable: Boolean) {
        store.write(Settings.BoolSettings.EnableQSTileOpt, enable)
    }

    private fun notifySettingChanged(tileSetting: StatusBarTileSetting) {
        synchronized(settingsChangeListeners) {
            settingsChangeListeners.broadcast {
                runCatching { it.onTileSettingsChange(tileSetting.tile.number) }.onFailure {
                    logger.e(
                        it,
                        "call onTileSettingsChange"
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun onTileClick(tileNumber: Int) {
        // find toggles bond to a tile.
        val bondToggle = shortXContext.rule.getAllToggles().firstOrNull {
            it.hasTileSetting() && it.tileSetting.bondToTileNumber == tileNumber
        }
        logger.d("onTileClick, bondToggle: $bondToggle")

        if (bondToggle != null) {
            val isCurrentEnabled = shortXContext.rule.isToggleEnabled(bondToggle.id)
            logger.d("setToggleEnabled since tile click: ${bondToggle.title}, current enabled: $isCurrentEnabled")
            shortXContext.rule.setToggleEnabled(bondToggle.id, !isCurrentEnabled)
        } else {
            shortXContext.rule.publishFact(
                reason = "onTileClick-$tileNumber",
                fact = OnQSTileClick.newBuilder().setTileNumber(tileNumber).build()
            ) { emptyMap() }
            if (OsUtils.isQOrAbove()) {
                systemServiceScope.launchSafety {
                    context.statusBarManager.collapsePanels()
                }
            }
        }
    }

    fun updateQSTileActiveState(tileNumber: Int) {
        val bondToggle = shortXContext.rule.getAllToggles().firstOrNull {
            it.hasTileSetting() && it.tileSetting.bondToTileNumber == tileNumber
        }
        logger.d("updateQSTileActiveState, bondToggle: $bondToggle")
        bondToggle?.let {
            systemServiceScope.launchSafety {
                withContext(Dispatchers.IO) {
                    val isEnabled = shortXContext.rule.isToggleEnabled(it.id)
                    val currentTileSetting =
                        getStatusBarTileSettingsByNumber(tileNumber)
                    val updateSetting = StatusBarTileSetting.newBuilder(currentTileSetting)
                        .setIsActive(isEnabled).build()
                    logger.d("updateSetting: $updateSetting")
                    updateStatusBarTileSetting(updateSetting)
                }
            }
        }
    }

    fun registerStatusbarClipStateListener(callback: ICallback) {
        statusBarClipStateListeners.register(callback)
    }

    fun unregisterStatusbarClipStateListener(callback: ICallback) {
        statusBarClipStateListeners.unregister(callback)
    }

    fun registerStatusbarClipSettingsListener(callback: ICallback) {
        statusBarClipSettingsListeners.register(callback)
    }

    fun unregisterStatusbarClipSettingsListener(callback: ICallback) {
        statusBarClipSettingsListeners.unregister(callback)
    }

    fun getCurrentStatusbarClipState(): StatusBarClipState {
        return statusBarClipState.value
    }

    fun getStatusbarClipSettings(): StatusBarClipUiSettings {
        return statusBarClipSettings.value
    }

    fun setStatusbarClipSettings(data: StatusBarClipUiSettings) {
        logger.d("setStatusbarClipSettings- $data")
        statusBarClipSettings.update { data }
        store.write(Settings.IntSettings.StatusBarChipPaddingStart, data.paddingStart)
        store.write(Settings.IntSettings.StatusBarChipPaddingEnd, data.paddingEnd)
        store.write(Settings.IntSettings.StatusBarChipPaddingTop, data.paddingTop)
        store.write(Settings.IntSettings.StatusBarChipPaddingBottom, data.paddingBottom)
        store.write(Settings.IntSettings.StatusBarChipFontSize, data.fontSize)
        store.write(Settings.IntSettings.StatusBarChipIconSize, data.iconSize)
        store.write(Settings.IntSettings.StatusBarChipUiPlacement, data.placement.number)
        store.write(Settings.IntSettings.StatusBarChipBgColor, data.backgroundColor)
        store.write(Settings.IntSettings.StatusBarChipTextColor, data.textColor)
        store.write(
            Settings.IntSettings.StatusBarChipMaxLength,
            data.maxLength.takeIf { it > 0 } ?: 100
        )
    }

    fun onStatusBarClipClick(id: String) {
        logger.d("onStatusBarClipClick: $id")
        systemServiceScope.launchSafety {
            _statusBarChipEvent.emit(StatusBarChipEvent.Click(id))
        }
    }

    fun onStatusBarClipLongClick(id: String) {
        logger.d("onStatusBarClipLongClick: $id")
        systemServiceScope.launchSafety {
            _statusBarChipEvent.emit(StatusBarChipEvent.LongClick(id))
        }
    }

    fun updateStatusBarClip(clipState: StatusBarClipState) {
        logger.logOnDebug("updateStatusBarClip: $clipState")
        statusBarClipState.update {
            clipState
        }
    }

    fun hideStatusBarClip() {
        logger.logOnDebug("hideStatusBarClip")
        statusBarClipState.update {
            StatusBarClipState.getDefaultInstance()
        }
    }

    fun setStatusBarIcon(slot: String, icon: String) {
        // try 16dp.
        val iconId = appContext.remixResId(icon, 16)
        logger.logOnDebug("setStatusBarIcon: $slot $icon $iconId")
        StatusBarManagerServiceFuncs.setIcon(slot, iconId)
    }

    fun removeStatusBarIcon(slot: String) {
        logger.logOnDebug("removeStatusBarIcon: $slot")
        StatusBarManagerServiceFuncs.removeIcon(slot)
    }

    fun showRecentApps(show: Boolean) {
        logger.d("showRecentApps: $show")
        show.select(
            { StatusBarManagerServiceFuncs.showRecentApps() },
            { StatusBarManagerServiceFuncs.hideRecentApps() }
        )
    }

    fun showGlobalActionsMenu() {
        logger.d("showGlobalActionsMenu")
        StatusBarManagerServiceFuncs.showGlobalActionsMenu()
    }

    fun hideOrShowInsets(@WindowInsets.Type.InsetsType types: Int, isHide: Boolean) {
        logger.d("hideOrShowInsets: $types $isHide")
        StatusBarManagerServiceFuncs.hideOrShowInsets(types, isHide)
    }

    private fun notifyStatusBarClipStateChanged(clipState: StatusBarClipState) {
        logger.logOnDebug("notifyStatusBarClipStateChanged: $clipState")
        synchronized(statusBarClipStateListeners) {
            statusBarClipStateListeners.broadcast {
                runCatching { it.receive(clipState.toByteArray().wrap()) }.onFailure {
                    logger.e(it, "notifyStatusBarClipStateChanged")
                }
            }
        }
    }

    private fun notifyStatusBarClipSettingsChanged(settings: StatusBarClipUiSettings) {
        logger.logOnDebug("notifyStatusBarClipSettingsChanged: $settings")
        synchronized(statusBarClipSettingsListeners) {
            statusBarClipSettingsListeners.broadcast {
                runCatching { it.receive(settings.toByteArray().wrap()) }.onFailure {
                    logger.e(it, "notifyStatusBarClipSettingsChanged")
                }
            }
        }
    }
}
