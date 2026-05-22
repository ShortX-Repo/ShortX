package tornaco.apps.shortx.services.xposed.hooks.hook

import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

class Api101HookAdapter : HookAdapter {
    @Suppress("UNCHECKED_CAST")
    override fun hookBefore(member: Member, callback: (ShortXHookParam) -> Unit) {
        when (member) {
            is Method -> Api101Runtime.xposed.hook(member).intercept(Api101Hooker.before(callback))
            is Constructor<*> -> Api101Runtime.xposed.hook(
                member as Constructor<Any>
            ).intercept(Api101Hooker.before(callback))
            else -> error("Unsupported hook member: $member")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun hookAfter(member: Member, callback: (ShortXHookParam) -> Unit) {
        when (member) {
            is Method -> Api101Runtime.xposed.hook(member).intercept(Api101Hooker.after(callback))
            is Constructor<*> -> Api101Runtime.xposed.hook(
                member as Constructor<Any>
            ).intercept(Api101Hooker.after(callback))
            else -> error("Unsupported hook member: $member")
        }
    }
}
