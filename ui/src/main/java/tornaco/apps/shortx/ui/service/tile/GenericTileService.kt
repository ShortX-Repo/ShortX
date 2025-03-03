package tornaco.apps.shortx.ui.service.tile

import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import tornaco.apps.shortx.core.IStatusBarTileSettingsChangeListener
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.select

abstract class GenericTileService : TileService() {
    abstract val tileIndex: StatusBarTile

    open val logger by lazy { Logger("TileService-$tileIndex") }
    open val handler by lazy { Handler(Looper.getMainLooper()) }
    open val appRes by lazy { AppResources(applicationContext, applicationContext.packageName) }

    private val onTileSettingsChangeListener =
        object : IStatusBarTileSettingsChangeListener.Stub() {
            override fun onTileSettingsChange(tileNumber: Int) {
                logger.d("onTileSettingsChange: $tileNumber")
                handler.runCatching {
                    if (tileNumber == tileIndex.number) {
                        updateTile()
                    }
                }
            }
        }

    override fun onTileAdded() {
        super.onTileAdded()
        logger.d("onTileAdded")
        updateTile()
    }

    private fun updateTile() {
        shortXManager.getStatusBarTileSettingsByNumber(tileIndex.number)?.let {
            logger.d("updateTile: $it")

            qsTile.label = it.label
            val resName = Remix.allFlat[it.iconName]
            qsTile.icon = appRes.getIcon(resName)
            qsTile.state = it.isActive.select(Tile.STATE_ACTIVE, Tile.STATE_INACTIVE)
            qsTile.updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        logger.d("onStartListening- ${Log.getStackTraceString(Throwable())}")
        updateTile()

        shortXManager.updateQSTileActiveState(tileIndex.number)
        shortXManager.unregisterStatusBarTileSettingsChangeListener(onTileSettingsChangeListener)
        shortXManager.registerStatusBarTileSettingsChangeListener(onTileSettingsChangeListener)
    }

    override fun onStopListening() {
        super.onStopListening()
        logger.d("onStopListening")
    }

    override fun onClick() {
        super.onClick()
        if (shortXManager.isQSTileOptEnabled()) {
            logger.i("Tile opt is enabled, ignore click")
            return
        }
        logger.d("onClick: $tileIndex")
        ShortXManagerNative.shortX?.onTileClick(tileIndex.number)
    }

}