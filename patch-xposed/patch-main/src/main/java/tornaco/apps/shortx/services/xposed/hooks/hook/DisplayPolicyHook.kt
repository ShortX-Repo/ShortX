package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.android.ServiceHolder
import util.Reflections

object DisplayPolicyHook {
    private val logger = Logger("ShortXHook.DisplayPolicyHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        hookDisplayPolicy(lpparam)
    }.onFailure {
        logger.e(it, "hookDisplayPolicy")
    }

    private fun hookDisplayPolicy(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            afterConstruct(
                Reflections.findClass("com.android.server.wm.DisplayPolicy", lpparam.classLoader),
            ) { param ->
                logger.d("init: ${param.thisObject}")
                ServiceHolder.displayPolicy = param.thisObject
            }
        }.onFailure {
            logger.e(it, "hookDisplayPolicy")
        }
}