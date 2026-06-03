package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Member

object LegacyXposedAdapter : XposedAdapter {
    override fun log(message: String) {
        XposedBridge.log("ShortX: $message")
    }

    override fun log(message: String, throwable: Throwable) {
        XposedBridge.log("ShortX: $message")
        XposedBridge.log(throwable)
    }

    override fun hookBefore(member: Member, callback: (ShortXHookParam) -> Unit) {
        hook(member, before = callback, after = null)
    }

    override fun hookAfter(member: Member, callback: (ShortXHookParam) -> Unit) {
        hook(member, before = null, after = callback)
    }

    private fun hook(
        member: Member,
        before: ((ShortXHookParam) -> Unit)?,
        after: ((ShortXHookParam) -> Unit)?
    ) {
        val unhook = XposedBridge.hookMethod(member, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)
                before?.let { callback ->
                    val shortXParam = param.toShortXHookParam(isBefore = true)
                    callback(shortXParam)
                    param.applyBeforeChanges(shortXParam)
                }
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                after?.let { callback ->
                    val shortXParam = param.toShortXHookParam(isBefore = false)
                    callback(shortXParam)
                    param.applyAfterChanges(shortXParam)
                }
            }
        })
        require(unhook != null) { "Unable to hook member: $member" }
    }

    private fun XC_MethodHook.MethodHookParam.toShortXHookParam(isBefore: Boolean): ShortXHookParam {
        return ShortXHookParam(
            method = method,
            thisObject = thisObject,
            args = args,
            result = result,
            throwable = throwable,
            isBefore = isBefore,
        )
    }

    private fun XC_MethodHook.MethodHookParam.applyBeforeChanges(param: ShortXHookParam) {
        if (param.skipOriginal) {
            if (param.throwableChanged) {
                throwable = param.throwable
            } else {
                result = param.result
            }
        }
    }

    private fun XC_MethodHook.MethodHookParam.applyAfterChanges(param: ShortXHookParam) {
        if (param.throwableChanged) {
            throwable = param.throwable
        }
        if (param.resultChanged) {
            result = param.result
        }
    }
}