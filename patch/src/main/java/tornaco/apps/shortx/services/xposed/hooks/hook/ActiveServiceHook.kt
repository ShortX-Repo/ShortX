package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.activeServicesClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService

object ActiveServiceHook {
    private val logger = Logger("ShortXHook.ActiveServiceHook")

    fun hookUpdateScreenState(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        // Only for >= android 31, the screen off fact slow issue is reported on android 31.
        if (OsUtils.isSOrAbove()) {
            afterMethod(
                lpparam.classLoader.activeServicesClass(),
                Methods.updateScreenStateLocked
            ) { param ->
                // void updateScreenStateLocked(boolean screenOn)
                val isScreenOn = param.args[0] as Boolean
                logger.d("updateScreenStateLocked: ${param.args.map { it }}")
                if (isScreenOn) {
                    ShortXService.shortXContext.serviceLifecycle.screenOn()

                } else {
                    ShortXService.shortXContext.serviceLifecycle.screenOff()
                }
            }
        }

    }.onFailure {
        logger.e(it, "hookUpdateScreenState")
    }
}
