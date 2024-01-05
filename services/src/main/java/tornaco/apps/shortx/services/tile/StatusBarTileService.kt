package tornaco.apps.shortx.services.tile

import android.annotation.SuppressLint
import android.content.Context
import android.os.RemoteCallbackList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.proto.fact.OnQSTileClick
import tornaco.apps.shortx.core.rule.ToggleObserver
import tornaco.apps.shortx.core.settings.defaultStatusBarTileSetting
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.statusBarManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.StatusBarTileSettingsStore
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety


class StatusBarTileService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "StatusBarTileService") {

    private val tileSettingsStore by lazy {
        StatusBarTileSettingsStore(systemServiceScope)
    }
    private val settingsChangeListeners = RemoteCallbackList<IStatusBarTileSettingsChangeListener>()

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
}
