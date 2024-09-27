package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.statusBarManagerServiceClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.android.ServiceHolder

object StatusBarManagerServiceHook {
    private val logger = Logger("ShortXHook.StatusBarManagerServiceHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookInit(lpparam)
    }

    private fun hookInit(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterConstruct(lpparam.classLoader.statusBarManagerServiceClass()) { param ->
            logger.d("statusBarManagerService ${param.thisObject}")
            ServiceHolder.statusBarManagerService = param.thisObject
        }
        afterMethod(lpparam.classLoader.statusBarManagerServiceClass(), "setIcon") { param ->
            logger.d("setIcon ${param.args.map { it }}")
        }
    }.onFailure {
        logger.e(it, "hookInit")
    }
}