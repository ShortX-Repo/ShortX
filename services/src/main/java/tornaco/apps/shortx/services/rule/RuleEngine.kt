package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import tornaco.apps.shortx.core.rule.engine.*
import tornaco.apps.shortx.services.ShortXContext

object RuleEngineFactory {
    private fun newConditionEvaluator(shortXContext: ShortXContext) =
        ConditionEvaluatorImpl(shortXContext)

    private fun newActionEvaluator(context: Context, handler: Handler) =
        ActionEvaluatorImpl(context, handler)

    private fun newFactMatcher() = FactMatcherImpl()

    fun newRuleEngine(
        shortXContext: ShortXContext,
        context: Context,
        handler: Handler,
        factMatcher: FactMatcher = newFactMatcher(),
        conditionEvaluator: ConditionEvaluator = newConditionEvaluator(shortXContext),
        actionEvaluator: ActionEvaluator = newActionEvaluator(context, handler),
        listener: EngineListener = LogEngineListener("RuleEngine"),
    ) = DefaultRuleEngine(
        factMatcher = factMatcher,
        conditionEvaluator = conditionEvaluator,
        actionEvaluator = actionEvaluator,
        listener = listener
    )
}

