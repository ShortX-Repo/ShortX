package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.accManager
import tornaco.apps.shortx.core.util.Logger

object AccManagerHook {
    private val logger = Logger("ShortXHook.AccManagerHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookSuppressingAccessibilityServices(lpparam)
    }

    private fun hookSuppressingAccessibilityServices(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            afterMethod(
                lpparam.classLoader.accManager(),
                "suppressingAccessibilityServicesLocked"
            ) { param ->
                logger.d("suppressingAccessibilityServicesLocked ${param.result}")
                if (param.result.toString().toBoolean()) {
                    logger.i("Change suppressingAccessibilityServicesLocked result to false")
                    param.result = false
                }
            }
        }.onFailure {
            logger.e(it, "hookSuppressingAccessibilityServices")
        }
}