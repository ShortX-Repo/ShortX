package tornaco.apps.shortx.services.xposed.hooks.hook

import android.annotation.SuppressLint
import android.view.InputEvent
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.platform.Classes.inputManagerService
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.android.ServiceHolder
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.ReflectionsExt
import util.Reflections


object InputManagerHook {
    private val logger = Logger("ShortXHook.InputManagerHook")

    @SuppressLint("PrivateApi")
    fun hookInputManager(lpparam: ISystemServerLoaded.Param) {
        kotlin.runCatching {
            afterMethod(lpparam.classLoader.inputManagerService(), Methods.start) { param ->
                logger.d("inputManagerService start.")
                ServiceHolder.inputManagerService = param.thisObject
            }
        }.onFailure {
            logger.e(it, "inputManagerService")
        }

        kotlin.runCatching {
            beforeMethod(lpparam.classLoader.inputManagerService(), Methods.filterInputEvent) { param ->
                val inputEvent = param.args[0] as InputEvent
                val res = ShortXService.shortXContext.gesture.filterInput(inputEvent)
                res?.let {
                    logger.logOnDebug { "Set filterInputEvent res: $it" }
                    param.result = it
                }
            }
        }.onFailure {
            logger.e(it, "inputManagerService")
        }

        kotlin.runCatching {
            afterMethod(
                Reflections.findClass(
                    "com.android.server.input.NativeInputManagerService\$NativeImpl",
                    lpparam.classLoader
                ),
                "setInputFilterEnabled"
            ) { param ->
                logger.w(
                    "setInputFilterEnabled: ${param.args.map { it }} ${
                        android.util.Log.getStackTraceString(
                            Throwable()
                        )
                    }"
                )
                val isEnabled = param.args[0] as Boolean
                ShortXService.shortXContext.input.onInputFilterStateChanged(isEnabled)
            }
        }.onFailure {
            logger.e(it, "setInputFilterEnabled")
        }
    }
}
