package tornaco.apps.shortx.services.xposed.hooks.hook

import android.util.Log
import io.github.libxposed.api.XposedInterface
import tornaco.apps.shortx.core.BuildPropInternal
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

class Api101XposedAdapter(
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

private class Api101Hooker(
    private val beforeCallback: ((ShortXHookParam) -> Unit)? = null,
    private val afterCallback: ((ShortXHookParam) -> Unit)? = null,
) : XposedInterface.Hooker {
    override fun intercept(chain: XposedInterface.Chain): Any? {
        if (BuildPropInternal.APP_IS_DEBUG) {
            XposedLog.log("Api101Hooker intercept - $chain")
        }
        val member = chain.executable
        val args = chain.args.toTypedArray()
        val beforeParam = ShortXHookParam(
            method = member,
            thisObject = chain.thisObject,
            args = args,
            isBefore = true,
        )
        beforeCallback?.let { hook ->
            runCatching {
                if (BuildPropInternal.APP_IS_DEBUG) {
                    XposedLog.log("Api101Hooker invoke beforeCallback - $hook $member")
                }
                hook(beforeParam)
            }.onFailure { XposedLog.log("before hook failed: $member", it) }
        }

        var result: Any? = null
        var throwable: Throwable? = null
        if (beforeParam.skipOriginal) {
            throwable = beforeParam.throwable
            result = beforeParam.result
        } else {
            try {
                result = chain.proceed(beforeParam.args)
            } catch (e: Throwable) {
                throwable = e
            }
        }

        val afterParam = ShortXHookParam(
            method = member,
            thisObject = chain.thisObject,
            args = beforeParam.args,
            result = result,
            throwable = throwable,
        )
        afterCallback?.let { hook ->
            runCatching {
                if (BuildPropInternal.APP_IS_DEBUG) {
                    XposedLog.log("Api101Hooker invoke afterCallback - $hook $member")
                }
                hook(afterParam)
            }.onFailure { XposedLog.log("after hook failed: $member", it) }
        }
        afterParam.throwable?.takeIf { afterParam.throwableChanged }?.let { throw it }
        if (!afterParam.throwableChanged) {
            throwable?.let { throw it }
        }
        return afterParam.result
    }

    companion object {
        fun before(callback: (ShortXHookParam) -> Unit): Api101Hooker {
            return Api101Hooker(beforeCallback = callback)
        }

        fun after(callback: (ShortXHookParam) -> Unit): Api101Hooker {
            return Api101Hooker(afterCallback = callback)
        }
    }
}