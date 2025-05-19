package tornaco.apps.shortx.services.xposed.hooks.hook.systemui


import android.content.ComponentName
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_CUSTOM_TILE
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import util.Reflections

var sAutoHideControllerImpl: Any? = null

fun SystemUIHook.hookTile(lpparam: IPackageLoaded.Param) = kotlin.runCatching {
    if (PackageNames.systemUI == lpparam.packageName) {
        afterMethod(
            Reflections.findClass(
                SYSTEM_UI_CUSTOM_TILE,
                lpparam.classLoader
            ),
            "handleClick"
        ) { param ->
            logger.d("handleClick: ${param.thisObject}")
            ShortXManagerNative.shortX?.let {
                if (it.isQSTileOptEnabled) {
                    val name: ComponentName =
                        Reflections.getObjectField(
                            param.thisObject,
                            "mComponent"
                        ) as ComponentName
                    if (name.packageName.contains(BuildPropInternal.APP_PACKAGE_NAME)) {
                        // tornaco.apps.shortx/tornaco.apps.shortx.ui.service.tile.Tile7
                        val tileName = name.className.substringAfterLast(".")
                        val tile = StatusBarTile.valueOf(tileName)
                        logger.d("handleClick: $name, tile: $tile")
                        it.onTileClick(tile.number)
                        param.result = null

                        Reflections.callMethod(sAutoHideControllerImpl, "touchAutoHide")
                        logger.d("touchAutoHide done")
                    }
                }
            }
        }
    }
}.onFailure {
    logger.e(it, "hookTile")
}