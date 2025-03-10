package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ComponentName
import android.content.pm.ServiceInfo
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.res.remixResId
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService

object ComputerEngineHook {
    private val logger = Logger("ShortXHook.ComputerEngineHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Replaced by TileHook.
        // hookGetServiceInfo(lpparam)
    }

    private fun hookGetServiceInfo(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(
            XposedHelpers.findClass("com.android.server.pm.ComputerEngine", lpparam.classLoader),
            "getServiceInfo"
        ) { param ->
            logger.d("getServiceInfo ${param.args.map { it }}")
            if (param.throwable != null) return@afterMethod
            if (param.result == null) return@afterMethod

            val name = param.args[0] as ComponentName?
            if (name != null && name.packageName == BuildPropInternal.APP_PACKAGE_NAME) {
                val tileName = name.className.substringAfterLast(".")
                val tileOrNull = StatusBarTile.entries.firstOrNull { it.name == tileName }
                logger.d("getServiceInfo: $name, tileOrNull?: $tileOrNull")
                if (tileOrNull != null) {
                    val iconName =
                        shortXManager.getStatusBarTileSettingsByNumber(tileOrNull.number)?.iconName
                    logger.d("getServiceInfo: $name, iconName?: $iconName")
                    if (iconName != null) {
                        val remixResId =
                            ShortXService.shortXContext.statusbar.appContext.remixResId(iconName)
                        logger.d("getServiceInfo: $name, remixResId?: $remixResId")
                        val resultServiceInfo = param.result as ServiceInfo
                        logger.d("getServiceInfo: $name, resultServiceInfo?: $resultServiceInfo")
                        resultServiceInfo.icon = remixResId
                        param.result = resultServiceInfo
                    }
                }
            }
        }
    }.onFailure {
        logger.e(it, "hookGetServiceInfo")
    }
}