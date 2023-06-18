package tornaco.apps.shortx.services.xposed.hooks.hook

import android.view.InputEvent
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.inputManagerService
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.services.ShortXService
import util.Reflections

fun XposedEntry.hookInputManager(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
    afterMethod(lpparam.classLoader.inputManagerService(), Methods.start) { param ->
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
        val res =
            ShortXService.shortXContext.gesture.filterInput(param.args[0] as InputEvent)
        res?.let {
            logger.d("filterInputEvent, setResult: $it")
            param.result = it
        }
    }
}.onFailure {
    logger.e(it, "hookInputManager")
}