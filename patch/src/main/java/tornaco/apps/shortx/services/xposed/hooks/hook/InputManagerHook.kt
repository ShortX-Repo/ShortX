package tornaco.apps.shortx.services.xposed.hooks.hook

import android.os.Build
import android.util.Log
import android.view.IInputFilter
import android.view.IInputFilterHost
import android.view.InputEvent
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.onDebugBuild
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
        // Meizu android 9 volumn not work after active ShortX.
        if (Build.MANUFACTURER == "meizu" && Build.ID == "PKQ1.190616.001") {
            logger.w("Drop InputManagerHook.")
            return@runCatching
        }

        afterMethod(lpparam.classLoader.inputManagerService(), Methods.start) { param ->
            logger.d("inputManagerService start.")
            ServiceHolder.inputManagerService = param.thisObject

            val inputFilter = Reflections.getObjectField(param.thisObject, "mInputFilter")
            logger.d("inputFilter: $inputFilter")

            // Not null means input filter is enabled.
            if (inputFilter == null) {
                Reflections.callMethod(
                    ServiceHolder.inputManagerService,
                    "setInputFilter",
                    InputFilterImpl
                )
                logger.i("InputFilter installed.")
                Reflections.setObjectField(ServiceHolder.inputManagerService, "mInputFilter", null)
                logger.i("InputFilter removed.")
            }
            logger.d("handle start complete.")
        }

        beforeMethod(lpparam.classLoader.inputManagerService(), Methods.filterInputEvent) { param ->
            // logger.d("filterInputEvent, event: ${param.args[0]}")
            val res =
                ShortXService.shortXContext.gesture.filterInput(param.args[0] as InputEvent)
            res?.let {
                logger.d("filterInputEvent, setResult: $it")
                param.result = it
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

object InputFilterImpl : IInputFilter.Stub() {
    private val logger = Logger("InputFilterImpl")

    override fun install(host: IInputFilterHost) {
        logger.d("install: $host")
    }

    override fun uninstall() {
        logger.d("uninstall")
    }

    override fun filterInputEvent(event: InputEvent, policyFlags: Int) {
        throw IllegalStateException("filterInputEvent should not be called")
    }
}