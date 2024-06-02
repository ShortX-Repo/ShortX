package tornaco.apps.shortx.services.xposed.hooks.hook

import com.android.internal.annotations.VisibleForTesting
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.mvel2.MVEL
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.fact.MethodHook
import tornaco.apps.shortx.core.proto.fact.MethodHookConstraintParamsCount
import tornaco.apps.shortx.core.proto.fact.MethodHookConstraintParamsType
import tornaco.apps.shortx.core.proto.fact.MethodHookConstraintReturnType
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.services.ShortXService
import util.ReflectionUtils
import util.Reflections
import java.lang.reflect.Method

object CustomHook {
    private const val IS_FEATURE_ENABLED = true
    private const val LOG_REDIRECT = "method_hook"

    fun hookAndroidSystem(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!IS_FEATURE_ENABLED) return
        ShortXService.registerLifecycleSystemReadyCallback {
            val log: (String) -> Unit = {
                ShortXService.serviceBinder.log(it, LOG_REDIRECT)
            }
            log("System ready.")
            installHookForRules(
                rules = ShortXService.shortXContext.rule.getAllEnabledRules(),
                classLoader = lpparam.classLoader,
                currentProcessPkgName = "android",
                onMethodCall = { param, hook ->
                    val contextDataList = hook.compileExpressions(param, log)
                    ShortXService.shortXContext.rule.publishFact(
                        "Custom hook",
                        MethodHook.newBuilder(hook).build()
                    ) {
                        contextDataList.associate {
                            it.first to it.second
                        }
                    }
                },
                lifecycle = MethodHookLifecycle.LoadedPackage,
                log = log
            )
        }
    }

    fun hookApp(
        classLoader: ClassLoader?,
        currentProcessPkgName: String?,
        lifecycle: MethodHookLifecycle
    ) {
        if (!IS_FEATURE_ENABLED) return
        val log: (String) -> Unit = {
            ShortXManagerNative.shortX?.log(it, LOG_REDIRECT)
        }
        installHookForRules(
            rules = shortXManager.getAllRules(),
            classLoader = classLoader,
            currentProcessPkgName = currentProcessPkgName,
            lifecycle = lifecycle,
            onMethodCall = { param, hook ->
                val contextDataList = hook.compileExpressions(param, log)
                shortXManager.onHookedMethodCalled(hook, contextDataList)
            },
            log = log
        )
    }

    private fun installHookForRules(
        rules: List<Rule>,
        classLoader: ClassLoader?,
        currentProcessPkgName: String?,
        lifecycle: MethodHookLifecycle,
        onMethodCall: (MethodHookParam, MethodHook) -> Unit,
        log: (String) -> Unit
    ) {
        log("Checking rules for app: $currentProcessPkgName - $lifecycle")
        rules.filter { it.isEnabled }.forEach { rule ->
            rule.factsAndQuitFacts.forEach { proto ->
                if (proto is_ MethodHook::class.java) {
                    val mh = proto unpack_ MethodHook::class.java
                    if ((currentProcessPkgName == null || mh.packageName == currentProcessPkgName)
                        && mh.lifecycle == lifecycle
                    ) {
                        log("Installing method hook: ${mh.className}-${mh.methodName}")
                        mh.install(classLoader, onMethodCall, log)
                    }
                }
            }
        }
    }


    private fun MethodHook.install(
        classLoader: ClassLoader?,
        onMethodCall: (MethodHookParam, MethodHook) -> Unit,
        log: (String) -> Unit
    ) {
        kotlin.runCatching {
            val clazz = Reflections.findClass(className, classLoader)
            log("Class instance: $clazz")
            val constraints = constraintList
            val methodsNameFiltered = ReflectionUtils.getDeclaredMethods(clazz)
                .filter { it.name == methodName }
            log("Methods match name: ${methodsNameFiltered.joinToString()}")
            val methodConstraintsFiltered =
                methodsNameFiltered.filter { matchConstraints(it, constraints, log) }
            require(methodConstraintsFiltered.isNotEmpty()) {
                "No method match your constraints"
            }
            val firstMethod = methodConstraintsFiltered.first()
            log("Pick first methods match constraints: $firstMethod")
            afterMethod(firstMethod) {
                log("----> onMethodCall $className-$methodName")
                onMethodCall(it, this)
            }
        }.onFailure {
            log(it.stackTraceToString())
        }
    }

    private fun matchConstraints(
        method: Method,
        constraints: List<ProtoAny>,
        log: (String) -> Unit
    ): Boolean {
        if (constraints.isEmpty()) return true
        return constraints.all {
            matchConstraint(method, it, log)
        }
    }

    @VisibleForTesting
    fun matchConstraint(method: Method, constraint: ProtoAny, log: (String) -> Unit): Boolean {
        return if (constraint is_ MethodHookConstraintParamsCount::class.java) {
            val count = constraint unpack_ MethodHookConstraintParamsCount::class.java
            log("MethodHookConstraintParamsCount: $count")
            method.parameters.size == count.count
        } else if (constraint is_ MethodHookConstraintParamsType::class.java) {
            val type = constraint unpack_ MethodHookConstraintParamsType::class.java
            log("MethodHookConstraintParamsType: ${type.typeList.joinToString(",")}")
            method.parameters.map {
                it.type.name
            } == type.typeList
        } else if (constraint is_ MethodHookConstraintReturnType::class.java) {
            val type = constraint unpack_ MethodHookConstraintReturnType::class.java
            log("MethodHookConstraintReturnType: ${type.type}")
            method.returnType.name == type.type
        } else {
            false
        }
    }

    private fun MethodHook.compileExpressions(
        methodHookParam: MethodHookParam,
        log: (String) -> Unit
    ): List<StringPair> {
        val expressions = expressionsList
        return expressions.map {
            val value = executeMVEL(it.expressionMVEL, methodHookParam, log)
            log("Execute expression: ${it.expressionMVEL}")
            log("Execute expression, result: $value")
            StringPair.newBuilder().setFirst(it.contextKey).setSecond(value).build()
        }
    }

    private fun executeMVEL(
        expression: String,
        methodHookParam: MethodHookParam,
        log: (String) -> Unit
    ): String {
        val varsMap: Map<String, Any?> = mapOf("param" to methodHookParam)
        return kotlin.runCatching {
            MVEL.executeExpression(MVEL.compileExpression(expression), varsMap)?.toString()
                .orEmpty()
        }.getOrElse {
            log(it.stackTraceToString())
            ""
        }
    }
}