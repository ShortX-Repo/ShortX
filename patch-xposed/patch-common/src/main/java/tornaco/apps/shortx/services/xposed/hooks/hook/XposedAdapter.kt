package tornaco.apps.shortx.services.xposed.hooks.hook

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

interface XposedAdapter {

    fun log(message: String)

    fun log(message: String, throwable: Throwable)

    fun hookBefore(member: Member, callback: (ShortXHookParam) -> Unit)

    fun hookAfter(member: Member, callback: (ShortXHookParam) -> Unit)
}

object XposedRuntime {
    private var adapter: XposedAdapter? = null

    var isSystemServer: Boolean = false
        private set

    val current: XposedAdapter
        get() = checkNotNull(adapter) { "XposedAdapter is not initialized" }

    fun initApi101(base: XposedInterface, systemServer: Boolean) {
        adapter = Api101XposedAdapter(base)
        isSystemServer = systemServer
    }

    fun initLegacy(systemServer: Boolean = false) {
        adapter = LegacyXposedAdapter
        isSystemServer = systemServer
    }
}

private class Api101XposedAdapter(
    private val xposed: XposedInterface,
) : XposedAdapter {
    override fun log(message: String) {
        xposed.log(Log.INFO, "ShortX", message)
    }

    override fun log(message: String, throwable: Throwable) {
        xposed.log(Log.ERROR, "ShortX", message, throwable)
    }

    @Suppress("UNCHECKED_CAST")
    override fun hookBefore(member: Member, callback: (ShortXHookParam) -> Unit) {
        when (member) {
            is Method -> xposed.hook(member).intercept(Api101Hooker.before(callback))
            is Constructor<*> -> xposed.hook(member as Constructor<Any>)
                .intercept(Api101Hooker.before(callback))

            else -> error("Unsupported hook member: $member")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun hookAfter(member: Member, callback: (ShortXHookParam) -> Unit) {
        when (member) {
            is Method -> xposed.hook(member).intercept(Api101Hooker.after(callback))
            is Constructor<*> -> xposed.hook(member as Constructor<Any>)
                .intercept(Api101Hooker.after(callback))

            else -> error("Unsupported hook member: $member")
        }
    }
}

private object LegacyXposedAdapter : XposedAdapter {
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