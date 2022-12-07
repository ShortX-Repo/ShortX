package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import tornaco.apps.shortx.core.rule.engine.*
import tornaco.apps.shortx.services.ShortXContext

object RuleEngineFactory {
    private fun newConditionEvaluator(context: Context, shortXContext: ShortXContext) =
        ConditionEvaluatorImpl(context, shortXContext)

    private fun newActionEvaluator(
        context: Context,
        shortXContext: ShortXContext,
        handler: Handler
    ) = ActionEvaluatorImpl(context, shortXContext, handler)

    private fun newFactMatcher() = FactMatcherImpl()

    fun newRuleEngine(
        shortXContext: ShortXContext,
        androidContext: Context,
        handler: Handler,
        factMatcher: FactMatcher = newFactMatcher(),
        conditionEvaluator: ConditionEvaluator = newConditionEvaluator(
            androidContext,
            shortXContext
        ),
        actionEvaluator: ActionEvaluator = newActionEvaluator(
            androidContext,
            shortXContext,
            handler
        ),
        listener: EngineListener = LogEngineListener("RuleEngine"),
    ) = DefaultRuleEngine(
        factMatcher = factMatcher,
        conditionEvaluator = conditionEvaluator,
        actionEvaluator = actionEvaluator,
        listener = listener
    )
}

