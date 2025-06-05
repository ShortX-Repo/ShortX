package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import java.lang.reflect.Method

private val utilLogger = Logger("ShortXHookUtil")

fun beforeConstruct(
    clazz: Class<*>,
    beforeConstruct: (param: MethodHookParam) -> Unit,
) {
    XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            super.beforeHookedMethod(param)
            param?.let {
                kotlin.runCatching {
                    beforeConstruct(it)
                }.onFailure {
                    utilLogger.e(it, "beforeConstruct-$clazz")
                }
            }
        }
    })
}

fun afterConstruct(
    clazz: Class<*>,
    afterConstruct: (param: MethodHookParam) -> Unit,
) {
    XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            super.afterHookedMethod(param)
            param?.let {
                kotlin.runCatching {
                    afterConstruct(it)
                }.onFailure {
                    utilLogger.e(it, "afterConstruct-$clazz")
                }
            }
        }
    }).apply {
        require(this.size > 0) {
            "afterConstruct, unable to hook this Constructor: $clazz"
        }
        utilLogger.d("afterConstruct, unhooks: $this for Constructor: $clazz")
    }
}

fun beforeMethod(
    clazz: Class<*>,
    methodName: String,
    logger: Logger = utilLogger,
    beforeMethod: (param: MethodHookParam) -> Unit,
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
        require(this.isNotEmpty()) {
            "beforeMethod, unable to hook this method: $clazz#$methodName"
        }
        logger.d("beforeMethod, unhooks: $this for method: $clazz#$methodName")
    }
}

fun afterMethod(
    clazz: Class<*>,
    methodName: String,
    logger: Logger = utilLogger,
    afterMethod: (param: MethodHookParam) -> Unit,
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
        require(this.isNotEmpty()) {
            "afterMethod, unable to hook this method: $clazz#$methodName"
        }
        logger.d("afterMethod, unhooks: $this for method: $clazz#$methodName")
    }
}

fun afterMethod(
    method: Method,
    afterMethod: (param: MethodHookParam) -> Unit,
) {
    val unHook: XC_MethodHook.Unhook? = XposedBridge.hookMethod(method, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            super.afterHookedMethod(param)
            param?.let {
                kotlin.runCatching {
                    afterMethod(it)
                }.onFailure {
                    utilLogger.e(it, "afterHookedMethod-$method")
                }
            }
        }
    })
    require(unHook != null) {
        "afterMethod, unable to hook this method: $method"
    }
    utilLogger.d("afterMethod, unhook: $unHook for method: $method")
}


fun beforeMethod(
    method: Method,
    beforeMethod: (param: MethodHookParam) -> Unit,
) {
    val unHook: XC_MethodHook.Unhook? = XposedBridge.hookMethod(method, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            super.beforeHookedMethod(param)
            param?.let {
                kotlin.runCatching {
                    beforeMethod(it)
                }.onFailure {
                    utilLogger.e(it, "beforeMethod-$method")
                }
            }
        }
    })
    require(unHook != null) {
        "beforeMethod, unable to hook this method: $method"
    }
    utilLogger.d("beforeMethod, unhook: $unHook for method: $method")
}


fun MethodHookParam.logParamsOnDebug(logger: Logger = utilLogger) {
    logger.logOnDebug { "${method.name} - ${this.args.map { it }.joinToString()}" }
}