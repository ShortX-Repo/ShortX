package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.platform.Classes.statusBarManagerServiceClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.android.ServiceHolder
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

object StatusBarManagerServiceHook {
    private val logger = Logger("ShortXHook.StatusBarManagerServiceHook")

    fun hook(lpparam: ISystemServerLoaded.Param) {
        hookInit(lpparam)
    }

    private fun hookInit(lpparam: ISystemServerLoaded.Param) = kotlin.runCatching {
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