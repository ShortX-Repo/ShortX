package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Member

class LegacyHookAdapter : HookAdapter {
    override fun hookBefore(member: Member, callback: (ShortXHookParam) -> Unit) {
        XposedBridge.hookMethod(member, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                param ?: return
                val shortXParam = param.toShortXHookParam(isBefore = true)
                callback(shortXParam)
                shortXParam.applyToLegacy(param)
            }
        }) ?: error("before hook failed: $member")
    }

    override fun hookAfter(member: Member, callback: (ShortXHookParam) -> Unit) {
        XposedBridge.hookMethod(member, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                param ?: return
                val shortXParam = param.toShortXHookParam(isBefore = false)
                callback(shortXParam)
                shortXParam.applyToLegacy(param)
            }
        }) ?: error("after hook failed: $member")
    }

    private fun MethodHookParam.toShortXHookParam(isBefore: Boolean): ShortXHookParam {
        return ShortXHookParam(
            method = method,
            thisObject = thisObject,
            args = args,
            result = result,
            throwable = throwable,
            isBefore = isBefore,
        )
    }

    private fun ShortXHookParam.applyToLegacy(param: MethodHookParam) {
        if (throwableChanged) {
            param.throwable = throwable
        }
        if (resultChanged || skipOriginal) {
            param.result = result
        }
    }
}
