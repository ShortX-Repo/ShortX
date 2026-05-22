package tornaco.apps.shortx.services.xposed.hooks.hook

import io.github.libxposed.api.XposedInterface
import tornaco.apps.shortx.core.BuildPropInternal

class Api101Hooker(
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
