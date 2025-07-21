package tornaco.apps.shortx.services.xposed.hooks.hook

import android.view.WindowManager
import tornaco.apps.shortx.core.platform.Classes.wmDisplayPolicyClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.android.ServiceHolder
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

object DisplayPolicyHook {
    private val logger = Logger("ShortXHook.DisplayPolicyHook")

    fun hook(lpparam: ISystemServerLoaded.Param) {
        hookDisplayPolicy(lpparam)
        hookAddWindow(lpparam)
    }

    private fun hookDisplayPolicy(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            afterConstruct(
                lpparam.classLoader.wmDisplayPolicyClass(),
            ) { param ->
                logger.d("init: ${param.thisObject}")
                ServiceHolder.displayPolicy = param.thisObject
            }
        }.onFailure {
            logger.e(it, "hookDisplayPolicy")
        }

    private fun hookAddWindow(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            afterMethod(
                lpparam.classLoader.wmDisplayPolicyClass(),
                "addWindowLw"
            ) { param ->
                // void addWindowLw(WindowState win, WindowManager.LayoutParams attrs)
                param.logParamsOnDebug(logger)
                val attr = param.args[1] as WindowManager.LayoutParams
                logger.d("addWindowLw: ${attr.title?.toString()} ${attr.type} ${attr.packageName}")
            }
        }.onFailure {
            logger.e(it, "hookDisplayPolicy")
        }
}