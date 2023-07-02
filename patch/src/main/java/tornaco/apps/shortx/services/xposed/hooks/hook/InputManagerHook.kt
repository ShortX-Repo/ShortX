package tornaco.apps.shortx.services.xposed.hooks.hook

import android.util.Log
import android.view.InputEvent
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.platform.Classes.inputManagerService
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.android.ServiceHolder
import util.Reflections
import util.fieldOrNull

fun XposedEntry.hookInputManager(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
    afterMethod(lpparam.classLoader.inputManagerService(), Methods.start) { param ->
        ServiceHolder.inputManagerService = param.thisObject
        val nat = param.thisObject.fieldOrNull<Any>("mNative")
        if (nat != null) {
            Reflections.callMethod(nat, "setInputFilterEnabled", true)
        } else {
            val ptr = Reflections.getObjectField(param.thisObject, "mPtr") as Long
            Reflections.callMethod(
                param.thisObject,
                "nativeSetInputFilterEnabled",
                ptr,
                true
            )
        }
    }

    beforeMethod(lpparam.classLoader.inputManagerService(), Methods.filterInputEvent) { param ->
        //  logger.d("filterInputEvent, event: ${param.args[0]}")
        val res =
            ShortXService.shortXContext.gesture.filterInput(param.args[0] as InputEvent)
        res?.let {
            logger.d("filterInputEvent, setResult: $it")
            param.result = it
        }
    }

    onDebugBuild {
        if (OsUtils.isTOrAbove()) {
            debugHooks(lpparam)
        }
    }
}.onFailure {
    logger.e(it, "hookInputManager")
}

private fun XposedEntry.debugHooks(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
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