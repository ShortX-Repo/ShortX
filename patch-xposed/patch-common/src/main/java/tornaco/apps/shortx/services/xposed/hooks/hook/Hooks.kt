package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

private val utilLogger = Logger("ShortXHookUtil")

fun beforeConstruct(
    clazz: Class<*>,
    beforeConstruct: (param: ShortXHookParam) -> Unit,
) {
    val constructors = clazz.declaredConstructors.onEach { it.isAccessible = true }
    require(constructors.isNotEmpty()) {
        "beforeConstruct, unable to hook this Constructor: $clazz"
    }
    constructors.forEach { constructor ->
        hookBefore(constructor, beforeConstruct)
    }
    utilLogger.d("beforeConstruct, hooked: ${constructors.toList()} for Constructor: $clazz")
}

fun afterConstruct(
    clazz: Class<*>,
    afterConstruct: (param: ShortXHookParam) -> Unit,
) {
    val constructors = clazz.declaredConstructors.onEach { it.isAccessible = true }
    require(constructors.isNotEmpty()) {
        "afterConstruct, unable to hook this Constructor: $clazz"
    }
    constructors.forEach { constructor ->
        hookAfter(constructor, afterConstruct)
    }
    utilLogger.d("afterConstruct, hooked: ${constructors.toList()} for Constructor: $clazz")
}

fun beforeMethod(
    clazz: Class<*>,
    methodName: String,
    logger: Logger = utilLogger,
    beforeMethod: (param: ShortXHookParam) -> Unit,
) {
    val methods = clazz.declaredMethods
        .filter { it.name == methodName }
        .onEach { it.isAccessible = true }
    require(methods.isNotEmpty()) {
        "beforeMethod, unable to hook this method: $clazz#$methodName"
    }
    methods.forEach { method -> hookBefore(method, beforeMethod) }
    logger.d("beforeMethod, hooked: $methods for method: $clazz#$methodName")
}

fun afterMethod(
    clazz: Class<*>,
    methodName: String,
    logger: Logger = utilLogger,
    afterMethod: (param: ShortXHookParam) -> Unit,
) {
    val methods = clazz.declaredMethods
        .filter { it.name == methodName }
        .onEach { it.isAccessible = true }
    require(methods.isNotEmpty()) {
        "afterMethod, unable to hook this method: $clazz#$methodName"
    }
    methods.forEach { method -> hookAfter(method, afterMethod) }
    logger.d("afterMethod, hooked: $methods for method: $clazz#$methodName")
}

fun afterMethod(
    method: Method,
    afterMethod: (param: ShortXHookParam) -> Unit,
) {
    method.isAccessible = true
    hookAfter(method, afterMethod)
    utilLogger.d("afterMethod, hooked: $method")
}

fun beforeMethod(
    method: Method,
    beforeMethod: (param: ShortXHookParam) -> Unit,
) {
    method.isAccessible = true
    hookBefore(method, beforeMethod)
    utilLogger.d("beforeMethod, hooked: $method")
}

private fun hookBefore(member: Member, callback: (ShortXHookParam) -> Unit) {
    hookMember(member, Api101Hooker.before(callback))
}

private fun hookAfter(member: Member, callback: (ShortXHookParam) -> Unit) {
    hookMember(member, Api101Hooker.after(callback))
}

@Suppress("UNCHECKED_CAST")
private fun hookMember(member: Member, hooker: Api101Hooker) {
    when (member) {
        is Method -> Api101Runtime.xposed.hook(member).intercept(hooker)
        is Constructor<*> -> Api101Runtime.xposed.hook(
            member as Constructor<Any>
        ).intercept(hooker)
        else -> error("Unsupported hook member: $member")
    }
}

fun ShortXHookParam.logParamsOnDebug(logger: Logger = utilLogger, printStack: Boolean = false) {
    logger.logOnDebug {
        "${method.name} - ${args.map { it }.joinToString()} ${if (printStack) Throwable().stackTraceToString() else ""}"
    }
}

fun ShortXHookParam.logParamsAndResOnDebug(
    logger: Logger = utilLogger,
    printStack: Boolean = false
) {
    logger.logOnDebug {
        "${method.name} - ${args.map { it }.joinToString()} ---> $result ${if (printStack) Throwable().stackTraceToString() else ""}"
    }
}
