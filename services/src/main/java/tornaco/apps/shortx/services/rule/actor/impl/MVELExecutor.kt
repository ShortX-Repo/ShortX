package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.mvel2.MVEL
import org.mvel2.ParserContext
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.OuterActions
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.FUNC_PARAM_REF_PREFIX
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.LOCAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.impl.js.ScriptShortXApi
import tornaco.apps.shortx.services.rule.actor.mvel.handle.Handle
import java.util.concurrent.atomic.AtomicInteger

class MVELExecutor(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val expression: String,
    private val evaluateContext: EvaluateContext? = null,
) {
    companion object {
        private val logger = Logger("MVELExecutor")

        val parserContext by lazy {
            ParserContext().apply {
                val allActionNames = OuterActions.getDescriptor().messageTypes.map { it.name }
                allActionNames.forEach {
                    val clazz = Class.forName("tornaco.apps.shortx.core.proto.action.$it")
                    addImport(it, clazz)
                    logger.logOnDebug { "addImport: $it - $clazz" }
                }
            }
        }
    }

    operator fun invoke(
        vars: Map<String, Any?> = emptyMap(),
        initialContextData: Map<String, Any> = emptyMap(),
        localVars: LocalVars?,
        parameterInputs: List<FuncParameterInput>
    ): Any? {
        val varsMap: Map<String, Any?> = mutableMapOf<String, Any?>().apply {
            put("context", context)
            put(
                "shortx",
                ScriptShortXApi(
                    shortXContext = shortXContext,
                    context = context,
                    evaluateContext = evaluateContext ?: EvaluateContext.getDefaultInstance()
                )
            )
            put(
                "actionEvaluator", ScriptActionEvaluator(
                    shortXContext = shortXContext,
                    evaluateContext = evaluateContext ?: EvaluateContext.getDefaultInstance(),
                    initialContextData = initialContextData,
                    localVars = localVars ?: LocalVars.obtain(),
                    parameterInputs = parameterInputs
                )
            )

            putAll(vars)
            Handle.inject(context, shortXContext, this)

            shortXContext.rule.getAllGlobalVars().forEach {
                put(
                    GLOBAL_VAR_REF_PREFIX + it.name,
                    if (it.isList()) {
                        it.valueList()
                    } else {
                        it.valueString()
                    }
                )
            }

            localVars?.all?.forEach {
                logger.logOnDebug("localVar: $it")
                put(
                    LOCAL_VAR_REF_PREFIX + it.name,
                    if (it.isList()) {
                        it.valueList()
                    } else {
                        it.valueString()
                    }
                )
            }

            parameterInputs.forEach {
                logger.logOnDebug("parameterInput: $it")
                put(
                    FUNC_PARAM_REF_PREFIX + it.name,
                    it.value
                )
            }
        }
        logger.logOnDebug("varsMap: $varsMap")
        logger.d("Execute: $expression")

        return MVEL.executeExpression(MVEL.compileExpression(expression, parserContext), varsMap)
    }
}

class ScriptActionEvaluator(
    private val shortXContext: ShortXContext,
    private val evaluateContext: EvaluateContext,
    private val initialContextData: Map<String, Any> = emptyMap(),
    private val localVars: LocalVars = LocalVars.obtain(),
    private val parameterInputs: List<FuncParameterInput> = emptyList(),
) {
    companion object {
        private val _evaId = AtomicInteger(0)
        val evaId get() = _evaId.getAndIncrement()
    }

    private val logger = Logger("MVEL_ActionEvaluator")

    fun evaluate(action: ProtoMessage) {
        evaluate(listOf(action))
    }

    fun evaluate(actions: List<ProtoMessage>) {
        val evaId = evaId
        logger.w("evaluate $evaId ${actions.joinToString { it.toString() }}")
        runBlocking {
            suspendCancellableCoroutine<Unit> { con ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = actions.map { it.pack_() },
                    actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync,
                    initialContextData = initialContextData,
                    localVars = localVars,
                    parameterInputs = parameterInputs,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    onComplete = {
                        logger.w("Action@ $evaId complete.")
                        con.resume(Unit) { cause, _, _ ->
                            logger.e(cause, "resume error.")
                        }
                    }
                )
            }
            logger.w("evaluate $evaId complete.")
        }
    }
}