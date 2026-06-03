package tornaco.apps.shortx.services.xposed.hooks.hook

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

class Api100XposedAdapter(
    private val xposed: XposedInterface,
) : XposedAdapter {
    override fun log(message: String) {
        xposed.log(message)
    }

    override fun log(message: String, throwable: Throwable) {
        xposed.log(message, throwable)
    }

    override fun hookBefore(member: Member, callback: (ShortXHookParam) -> Unit) {
        Api100HookRegistry.registerBefore(member, callback)
        hookIfNeeded(member)
    }

    override fun hookAfter(member: Member, callback: (ShortXHookParam) -> Unit) {
        Api100HookRegistry.registerAfter(member, callback)
        hookIfNeeded(member)
    }

    @Suppress("UNCHECKED_CAST")
    private fun hookIfNeeded(member: Member) {
        if (!Api100HookRegistry.markInstalled(member)) {
            return
        }
        when (member) {
            is Method -> xposed.hook(member, Api100DispatchHooker::class.java)
            is Constructor<*> -> xposed.hook(member as Constructor<Any>, Api100DispatchHooker::class.java)
            else -> error("Unsupported hook member: $member")
        }
    }
}

private object Api100HookRegistry {
    private val beforeCallbacks = ConcurrentHashMap<Member, MutableList<(ShortXHookParam) -> Unit>>()
    private val afterCallbacks = ConcurrentHashMap<Member, MutableList<(ShortXHookParam) -> Unit>>()
    private val installedMembers = ConcurrentHashMap.newKeySet<Member>()

    fun registerBefore(member: Member, callback: (ShortXHookParam) -> Unit) {
        beforeCallbacks.compute(member) { _, callbacks ->
            (callbacks ?: mutableListOf()).also { it.add(callback) }
        }
    }

    fun registerAfter(member: Member, callback: (ShortXHookParam) -> Unit) {
        afterCallbacks.compute(member) { _, callbacks ->
            (callbacks ?: mutableListOf()).also { it.add(callback) }
        }
    }

    fun markInstalled(member: Member): Boolean = installedMembers.add(member)

    fun dispatchBefore(callback: XposedInterface.BeforeHookCallback) {
        beforeCallbacks[callback.member]?.forEach { hook ->
            val shortXParam = ShortXHookParam(
                method = callback.member,
                thisObject = callback.thisObject,
                args = callback.args,
                isBefore = true,
            )
            hook(shortXParam)
            if (shortXParam.skipOriginal) {
                if (shortXParam.throwableChanged) {
                    callback.throwAndSkip(shortXParam.throwable ?: IllegalStateException("Skip requested without throwable"))
                } else {
                    callback.returnAndSkip(shortXParam.result)
                }
            }
        }
    }

    fun dispatchAfter(callback: XposedInterface.AfterHookCallback) {
        afterCallbacks[callback.member]?.forEach { hook ->
            val shortXParam = ShortXHookParam(
                method = callback.member,
                thisObject = callback.thisObject,
                args = callback.args,
                result = callback.result,
                throwable = callback.throwable,
            )
            hook(shortXParam)
            if (shortXParam.throwableChanged) {
                callback.setThrowable(shortXParam.throwable)
            }
            if (shortXParam.resultChanged) {
                callback.setResult(shortXParam.result)
            }
        }
    }
}

@Suppress("unused")
@XposedHooker
private class Api100DispatchHooker : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        @BeforeInvocation
        fun before(callback: XposedInterface.BeforeHookCallback) {
            Api100HookRegistry.dispatchBefore(callback)
        }

        @JvmStatic
        @AfterInvocation
        fun after(callback: XposedInterface.AfterHookCallback) {
            Api100HookRegistry.dispatchAfter(callback)
        }
    }
}