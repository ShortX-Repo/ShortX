package tornaco.apps.shortx.services.xposed.hooks.hook

import com.android.internal.annotations.VisibleForTesting
import org.mvel2.MVEL
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.fact.MethodHook
import tornaco.apps.shortx.core.proto.fact.MethodHookConstraintParamsCount
import tornaco.apps.shortx.core.proto.fact.MethodHookConstraintParamsType
import tornaco.apps.shortx.core.proto.fact.MethodHookConstraintReturnType
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.quitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import util.ReflectionUtils
import util.Reflections
import java.lang.reflect.Method

object CustomHookUtil {
    fun installHookForRules(
        rules: List<Rule>,
        das: List<DirectAction>,
        classLoader: ClassLoader?,
        currentProcessPkgName: String?,
        lifecycle: MethodHookLifecycle,
        onMethodCall: (ShortXHookParam, MethodHook) -> Unit,
        log: (String) -> Unit
    ) {
        log("Checking rules for app: $currentProcessPkgName - $lifecycle")
        (rules.filter { it.isEnabled }.map { it.factsAndQuitFacts } + das.map { it.quitFacts })
            .flatten()
            .forEach { proto ->
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


    private fun MethodHook.install(
        classLoader: ClassLoader?,
        onMethodCall: (ShortXHookParam, MethodHook) -> Unit,
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
            log("beforeMethod?: $beforeMethod")
            if (beforeMethod) {
                beforeMethod(firstMethod) {
                    log("----> beforeMethod.onMethodCall $className-$methodName")
                    onMethodCall(it, this)
                }
            } else {
                afterMethod(firstMethod) {
                    log("----> afterMethod.onMethodCall $className-$methodName")
                    onMethodCall(it, this)
                }
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

    fun MethodHook.compileExpressions(
        methodHookParam: ShortXHookParam,
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
        methodHookParam: ShortXHookParam,
        log: (String) -> Unit
    ): String {
        val varsMap: Map<String, Any?> =
            mapOf(
                "param" to methodHookParam,
                "helper" to InjectedAndroidAppHelper()
            )
        return kotlin.runCatching {
            MVEL.executeExpression(MVEL.compileExpression(expression), varsMap)?.toString()
                .orEmpty()
        }.getOrElse {
            log(it.stackTraceToString())
            ""
        }
    }
}
