package tornaco.apps.shortx.services.rule.cond

import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.util.Logger

private val logger = Logger("ConditionCallable")

@DoNotStrip
class ConditionCallable(
    private val evaluateContext: EvaluateContext,
    private val eva: ConditionEvaluatorImpl,
    private val condition: ProtoAny,
    private val contextData: ContextData,
    private val localVars: LocalVars
) {
    fun call(): Boolean {
        val evaluateResult =
            runBlocking { eva.evaluate(evaluateContext, condition, contextData, localVars) }
        logger.d("call condition class: ${condition.javaClass}, evaluateResult: $evaluateResult")
        return evaluateResult.isMatch()
    }
}