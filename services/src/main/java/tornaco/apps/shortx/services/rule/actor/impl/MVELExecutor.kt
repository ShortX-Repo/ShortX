package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import org.mvel2.MVEL
import org.mvel2.ParserContext
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.OuterActions
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.FUNC_PARAM_REF_PREFIX
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.LOCAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.impl.js.ScriptShortXApiImpl
import tornaco.apps.shortx.services.rule.actor.mvel.handle.Handle

class MVELExecutor(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val expression: String,
    private val evaluateContext: EvaluateContext? = null,
) {
    companion object {
        private val logger = Logger("MVELExecutor")
    }

    operator fun invoke(
        vars: Map<String, Any?> = emptyMap(),
        initialContextData: Map<String, Any> = emptyMap(),
        localVars: LocalVars?,
        parameterInputs: List<FuncParameterInput>
    ): Any? {
        val varsMap: Map<String, Any?> = mutableMapOf<String, Any?>().apply {
            put("context", context)

            ScriptShortXApiImpl(
                shortXContext = shortXContext,
                context = context,
                evaluateContext = evaluateContext ?: EvaluateContext.getDefaultInstance(),
                initialContextData = initialContextData,
                localVars = localVars ?: LocalVars.obtain(),
                parameterInputs = parameterInputs
            ).apply {
                put("shortx", this)
            }

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
        val parserContext = ParserContext().apply {
            val allActionNames = OuterActions.getDescriptor().messageTypes.map { it.name }
            allActionNames.forEach {
                val clazz = Class.forName("tornaco.apps.shortx.core.proto.action.$it")
                addImport(it, clazz)
                logger.logOnDebug { "addImport: $it - $clazz" }
            }
        }
        return MVEL.executeExpression(MVEL.compileExpression(expression, parserContext), varsMap)
    }
}