package tornaco.apps.shortx.services.xposed.hooks.hook

import android.util.Log
import android.view.InputEvent
import android.view.KeyEvent
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.platform.Classes.inputManagerService
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.android.ServiceHolder
import util.Reflections

object InputManagerHook {
    private val logger = Logger("ShortXHook.InputManagerHook")

    fun hookInputManager(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(lpparam.classLoader.inputManagerService(), Methods.start) { param ->
            logger.d("inputManagerService start.")
            ServiceHolder.inputManagerService = param.thisObject
        }

        beforeMethod(lpparam.classLoader.inputManagerService(), Methods.filterInputEvent) { param ->
            // logger.d("filterInputEvent, event: ${param.args[0]}")

            val inputEvent = param.args[0] as InputEvent
            ShortXService.shortXContext.input.setHasReceivedInputFilterCallback()
            if (inputEvent is KeyEvent) {
                val res =
                    ShortXService.shortXContext.gesture.filterInput(inputEvent)
                res?.let {
                    logger.d("filterInputEvent, setResult: $it")
                    param.result = it
                }
            }
        }

        onDebugBuild {
            if (OsUtils.isTOrAbove()) {
                // debugHooks(lpparam)
            }
        }
    }.onFailure {
        logger.e(it, "hookInputManager")
    }

    private fun debugHooks(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(lpparam.classLoader.inputManagerService(), "setInputFilter") { param ->
            logger.d(
                "setInputFilter: ${param.args.map { it }} ${
                    Log.getStackTraceString(
                        Throwable()
                    )
                }"
            )
        }

        afterMethod(
            Reflections.findClass(
                "com.android.server.input.InputManagerService\$InputFilterHost",
                lpparam.classLoader
            ), "sendInputEvent"
        ) { param ->
            logger.d("sendInputEvent: ${param.args.map { it }} ${Log.getStackTraceString(Throwable())}")
        }

        afterMethod(
            Reflections.findClass(
                "com.android.server.input.NativeInputManagerService\$NativeImpl",
                lpparam.classLoader
            ), "injectInputEvent"
        ) { param ->
            logger.d("injectInputEvent: ${param.args.map { it }} ${Log.getStackTraceString(Throwable())}")
        }

        afterMethod(
            Reflections.findClass(
                "com.android.server.input.NativeInputManagerService\$NativeImpl",
                lpparam.classLoader
            ),
            "setInputFilterEnabled"
        ) { param ->
            logger.d(
                "setInputFilterEnabled: ${param.args.map { it }} ${
                    Log.getStackTraceString(
                        Throwable()
                    )
                }"
            )
        }

    }.onFailure {
        logger.e(it, "debugHooks")
    }
}
