package tornaco.apps.shortx.services.xposed.hooks.hook

import android.view.KeyEvent
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.inputManagerService
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.android.ServiceHolder

object InputManagerHook {
    private val logger = Logger("ShortXHook.InputManagerHook")

    fun hookInputManager(lpparam: XC_LoadPackage.LoadPackageParam) {
        kotlin.runCatching {
            afterMethod(lpparam.classLoader.inputManagerService(), Methods.start) { param ->
                logger.d("inputManagerService start.")
                ServiceHolder.inputManagerService = param.thisObject
            }
        }.onFailure {
            logger.e(it, "inputManagerService")
        }

        kotlin.runCatching {
            // public int interceptKeyBeforeQueueing(KeyEvent event, int policyFlags)
            beforeMethod(
                lpparam.classLoader.inputManagerService(),
                Methods.interceptKeyBeforeQueueing
            ) { param ->
                param.logParamsOnDebug()
                val keyEvent = param.args[0] as KeyEvent
                val policyFlags = param.args[1] as Int
                val res =
                    ShortXService.shortXContext.input.interceptKeyBeforeQueueing(
                        keyEvent,
                        policyFlags
                    )
                if (res != null) {
                    param.result = res
                }
            }
        }.onFailure {
            logger.e(it, "inputManagerService")
        }

        // public int interceptKeyBeforeDispatching((IBinder focus, KeyEvent event, int policyFlags)
        beforeMethod(
            lpparam.classLoader.inputManagerService(),
            Methods.interceptKeyBeforeDispatching
        ) { param ->
            param.logParamsOnDebug()
        }
    }
}
