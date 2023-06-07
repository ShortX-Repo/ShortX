package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.util.Logger

private val logger = Logger("ConditionCallable")

@DoNotStrip
class ConditionCallable(
    private val eva: ConditionEvaluatorImpl,
    private val condition: ProtoAny,
    private val contextData: ContextData
) {
    fun call(): Boolean {
        val evaluateResult = eva.evaluate(condition, contextData)
        logger.d("call condition class: ${condition.javaClass}, evaluateResult: $evaluateResult")
        return evaluateResult.isMatch()
    }
}