package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
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
            if (shortxAppContext == null) {
                logger.w("shortxAppContext is null.")
                return@afterMethod
            }
            val result = param.result
            val tile = param.thisObject as Tile
            logger.d("Tile: ${tile.label} ${tile.contentDescription} $result")
            val label: CharSequence = tile.label ?: return@afterMethod
            val contentDescription: CharSequence = tile.contentDescription ?: return@afterMethod
            // Tile 1
            // Tile 2..
            val number = contentDescription.toString().toIntOrNull() ?: return@afterMethod
            logger.d("number: $number")
            val sbtOrNull = StatusBarTile.forNumber(number)
            logger.d("sbtOrNull: $sbtOrNull")
            if (sbtOrNull != null) {
                val iconName =
                    shortx.getStatusBarTileSettingsByNumber(sbtOrNull.number)?.iconName
                logger.d("getIcon: $label, iconName?: $iconName")
                if (iconName != null) {
                    val remixResId =
                        requireNotNull(shortxAppContext) { "shortxAppContext is null" }.remixResId(
                            iconName
                        )
                    logger.d("getIcon: $label, remixResId?: $remixResId")
                    val iconFixed = Icon.createWithResource(shortxAppContext, remixResId)
                    logger.d("getIcon: $label, iconFixed?: $iconFixed")
                    param.result = iconFixed
                }
            }

        }.onFailure {
            logger.e(it, "Handle Tile.getIcon")
        }
    }
}