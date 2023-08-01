package tornaco.apps.shortx.ui.service.tile

import android.os.Handler
import android.os.Looper
import android.service.quicksettings.TileService
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger

abstract class GenericTileService : TileService() {
    abstract val tileIndex: StatusBarTile

    open val logger by lazy { Logger("TileService-$tileIndex") }
    open val handler by lazy { Handler(Looper.getMainLooper()) }
    open val appRes by lazy { AppResources(applicationContext, applicationContext.packageName) }

    private val onTileSettingsChangeListener =
        object : IStatusBarTileSettingsChangeListener.Stub() {
            override fun onTileSettingsChange(tileNumber: Int) {
                handler.runCatching {
                    if (tileNumber == tileIndex.number) {
                        updateTile()
                    }
                }
            }
        }

    private fun updateTile() {
        shortXManager.getStatusBarTileSettingsByNumber(tileIndex.number)?.let {
            logger.d("updateTile: $it")

            qsTile.label = it.label
            qsTile.icon = appRes.getIcon(it.iconName)
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        logger.d("onStartListening")
        shortXManager.registerStatusBarTileSettingsChangeListener(onTileSettingsChangeListener)
    }

    override fun onStopListening() {
        super.onStopListening()
        logger.d("onStopListening")
        shortXManager.unregisterStatusBarTileSettingsChangeListener(onTileSettingsChangeListener)
    }

    override fun onClick() {
        super.onClick()
        logger.d("onClick: $tileIndex")
    }

}