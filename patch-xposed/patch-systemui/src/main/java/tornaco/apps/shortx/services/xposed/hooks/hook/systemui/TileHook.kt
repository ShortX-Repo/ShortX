package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import tornaco.apps.shortx.core.AppGlobals
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.res.remixResId
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod

private val shortx by lazy { shortXManager }

fun SystemUIHook.hookTileIcon() {
    afterMethod(
        Tile::class.java,
        "getIcon"
    ) { param ->
        runCatching {
            if (AppGlobals.appContext == null) {
                logger.w("shortxAppContext is null.")
                return@afterMethod
            }
            val result = param.result
            val tile = param.thisObject as Tile
            logger.d("hookTileIcon Tile: ${tile.label} ${tile.contentDescription} $result")
            val label: CharSequence = tile.label ?: return@afterMethod
            val contentDescription: CharSequence = tile.contentDescription ?: return@afterMethod
            // Tile 1
            // Tile 2..
            val number = contentDescription.toString().toIntOrNull() ?: return@afterMethod
            logger.d("hookTileIcon number: $number")
            val sbtOrNull = StatusBarTile.forNumber(number)
            logger.d("hookTileIcon sbtOrNull: $sbtOrNull")
            if (sbtOrNull != null) {
                val iconName =
                    shortx.getStatusBarTileSettingsByNumber(sbtOrNull.number)?.iconName
                logger.d("hookTileIcon getIcon: $label, iconName?: $iconName")
                if (iconName != null) {
                    val remixResId =
                        requireNotNull(AppGlobals.appContext) { "shortxAppContext is null" }.remixResId(
                            iconName
                        )
                    logger.d("hookTileIcon getIcon: $label, remixResId?: $remixResId")
                    val iconFixed = Icon.createWithResource(AppGlobals.appContext, remixResId)
                    logger.d("hookTileIcon getIcon: $label, iconFixed?: $iconFixed")
                    param.returnAndSkip(iconFixed)
                }
            }

        }.onFailure {
            logger.e(it, "Handle Tile.getIcon")
        }
    }
}


fun SystemUIHook.hookTileLabel() {
    afterMethod(
        Tile::class.java,
        "getLabel"
    ) { param ->
        runCatching {
            if (AppGlobals.appContext == null) {
                logger.w("shortxAppContext is null.")
                return@afterMethod
            }
            val result = param.result
            val tile = param.thisObject as Tile
            logger.d("hookTileLabel Tile: ${tile.contentDescription} $result")
            val contentDescription: CharSequence = tile.contentDescription ?: return@afterMethod
            // Tile 1
            // Tile 2..
            val number = contentDescription.toString().toIntOrNull() ?: return@afterMethod
            logger.d("hookTileLabel number: $number")
            val sbtOrNull = StatusBarTile.forNumber(number)
            logger.d("hookTileLabel sbtOrNull: $sbtOrNull")
            if (sbtOrNull != null) {
                val overlayLabel =
                    shortx.getStatusBarTileSettingsByNumber(sbtOrNull.number).label
                logger.d("hookTileLabel getLabel for number: $number, overlayLabel?: $overlayLabel")
                if (overlayLabel != null) {
                    param.returnAndSkip(overlayLabel)
                }
            }

        }.onFailure {
            logger.e(it, "Handle Tile.getLabel")
        }
    }
}
