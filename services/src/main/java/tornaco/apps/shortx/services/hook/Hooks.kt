package tornaco.apps.shortx.services.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

fun beforeConstruct(
    clazz: Class<*>,
    beforeConstruct: (param: XC_MethodHook.MethodHookParam) -> Unit,
) {
    XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            super.beforeHookedMethod(param)
            param?.let { beforeConstruct(it) }
        }
    })
}

fun afterConstruct(
    clazz: Class<*>,
    afterConstruct: (param: XC_MethodHook.MethodHookParam) -> Unit,
) {
    XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            super.afterHookedMethod(param)
            param?.let { afterConstruct(it) }
        }
    })
}

fun beforeMethod(
    clazz: Class<*>,
    methodName: String,
    beforeMethod: (param: XC_MethodHook.MethodHookParam) -> Unit,
) {
    XposedBridge.hookAllMethods(clazz, methodName, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            super.beforeHookedMethod(param)
            param?.let { beforeMethod(it) }
        }
    })
}

fun afterMethod(
    clazz: Class<*>,
    methodName: String,
    afterMethod: (param: XC_MethodHook.MethodHookParam) -> Unit,
) {
    XposedBridge.hookAllMethods(clazz, methodName, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            super.afterHookedMethod(param)
            param?.let { afterMethod(it) }
        }
    })
}


@Suppress("UNCHECKED_CAST")
fun <T> Any.field(fieldName: String): T {
    return XposedHelpers.getObjectField(this, fieldName) as T
}
