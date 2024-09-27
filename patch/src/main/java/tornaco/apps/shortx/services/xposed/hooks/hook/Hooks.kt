package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import java.lang.reflect.Method

private val logger = Logger("ShortXHookUtil")

fun beforeConstruct(
    clazz: Class<*>,
    beforeConstruct: (param: XC_MethodHook.MethodHookParam) -> Unit,
) {
    XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            super.beforeHookedMethod(param)
            param?.let {
                kotlin.runCatching {
                    beforeConstruct(it)
                }.onFailure {
                    logger.e(it, "beforeConstruct-$clazz")
                }
            }
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
            param?.let {
                kotlin.runCatching {
                    afterConstruct(it)
                }.onFailure {
                    logger.e(it, "afterConstruct-$clazz")
                }
            }
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
            param?.let {
                kotlin.runCatching {
                    beforeMethod(it)
                }.onFailure {
                    logger.e(it, "beforeHookedMethod-$clazz-$methodName")
                }
            }
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
            param?.let {
                kotlin.runCatching {
                    afterMethod(it)
                }.onFailure {
                    logger.e(it, "afterHookedMethod-$clazz-$methodName")
                }
            }
        }
    }).apply {
        require(this.size > 0) {
            "afterMethod, unable to hook this method: $clazz#$methodName"
        }
        logger.w("afterMethod, unhooks: $this for method: $clazz#$methodName")
    }
}

fun afterMethod(
    method: Method,
    afterMethod: (param: XC_MethodHook.MethodHookParam) -> Unit,
) {
    val unHook: XC_MethodHook.Unhook? = XposedBridge.hookMethod(method, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            super.afterHookedMethod(param)
            param?.let {
                kotlin.runCatching {
                    afterMethod(it)
                }.onFailure {
                    logger.e(it, "afterHookedMethod-$method")
                }
            }
        }
    })
    require(unHook != null) {
        "afterMethod, unable to hook this method: $method"
    }
    logger.w("afterMethod, unhook: $unHook for method: $method")
}
