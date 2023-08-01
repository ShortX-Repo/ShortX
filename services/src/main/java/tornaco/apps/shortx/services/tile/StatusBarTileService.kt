package tornaco.apps.shortx.services.tile

import android.os.RemoteCallbackList
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.common.StatusBarTileSetting
import tornaco.apps.shortx.core.settings.defaultStatusBarTileSetting
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.StatusBarTileSettingsStore
import tornaco.apps.shortx.services.systemServiceScope


class StatusBarTileService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "StatusBarTileService") {

    private val tileSettingsStore by lazy {
        StatusBarTileSettingsStore(systemServiceScope)
    }
    private val settingsChangeListeners = RemoteCallbackList<IStatusBarTileSettingsChangeListener>()

    override fun systemReady() {
        super.systemReady()
        tileSettingsStore.readBlocking()
        // Init default settings.
        StatusBarTile.values().filterNot { it == StatusBarTile.UNRECOGNIZED }.forEach {
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

    private fun notifySettingChanged(tileSetting: StatusBarTileSetting) {
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
