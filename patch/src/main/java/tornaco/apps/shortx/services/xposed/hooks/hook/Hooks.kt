package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

private val logger = Logger("Hooks")

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
    }).apply {
        require(this.size > 0) {
            "beforeMethod, unable to hook this method: $clazz#$methodName"
        }
        logger.w("beforeMethod, unhooks: $this for method: $clazz#$methodName")
    }
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
    }).apply {
        require(this.size > 0) {
            "afterMethod, unable to hook this method: $clazz#$methodName"
        }
        logger.w("afterMethod, unhooks: $this for method: $clazz#$methodName")
    }
}


@Suppress("UNCHECKED_CAST")
fun <T> Any.field(fieldName: String): T {
    return Reflections.getObjectField(this, fieldName) as T
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.fieldOrNull(fieldName: String): T? {
    return kotlin.runCatching { Reflections.getObjectField(this, fieldName) as T }
        .getOrElse { null }
}
