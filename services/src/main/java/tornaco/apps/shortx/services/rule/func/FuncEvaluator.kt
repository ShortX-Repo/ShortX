package tornaco.apps.shortx.services.rule.func

import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.func.Func
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator

interface FuncEvaluator {
    suspend fun evaluate(evaluateContext: EvaluateContext, func: Func, params: List<StringPair>)
}

class FuncEvaluatorImpl(private val actionEvaluator: ActionEvaluator) : FuncEvaluator {
    override suspend fun evaluate(
        evaluateContext: EvaluateContext,
        func: Func,
        params: List<StringPair>
    ) {
        val contextData = ContextData.obtain()
        func.actionsList.forEach {
            actionEvaluator.evaluate(evaluateContext, it, contextData, LocalVars.obtain())
        }
    }

}