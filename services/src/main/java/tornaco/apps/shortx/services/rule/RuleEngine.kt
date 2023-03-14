package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import tornaco.apps.shortx.services.ShortXContext

object RuleEngineFactory {
    private fun newConditionEvaluator(context: Context, shortXContext: ShortXContext) =
        ConditionEvaluatorImpl(context, shortXContext)

    private fun newActionEvaluator(
        context: Context,
        shortXContext: ShortXContext,
        handler: Handler,
        conditionEvaluator: ConditionEvaluator
    ) = ActionEvaluatorImpl(context, shortXContext, handler, conditionEvaluator)

    private fun newFactMatcher(shortXContext: ShortXContext) = FactMatcherImpl(shortXContext)

    fun newRuleEngine(
        shortXContext: ShortXContext,
        androidContext: Context,
        handler: Handler,
        factMatcher: FactMatcher = newFactMatcher(shortXContext),
        conditionEvaluator: ConditionEvaluator = newConditionEvaluator(
            androidContext,
            shortXContext
        ),
        actionEvaluator: ActionEvaluator = newActionEvaluator(
            androidContext,
            shortXContext,
            handler,
            conditionEvaluator
        ),
        onJobStarted: (String) -> Unit,
        onJobCompleted: (String) -> Unit,
        onJobCancelled: (String) -> Unit,
        scope: CoroutineScope = CoroutineScope(SupervisorJob())
    ) = DefaultRuleEngine(
        factMatcher = factMatcher,
        conditionEvaluator = conditionEvaluator,
        actionEvaluator = actionEvaluator,
        scope = scope,
        onJobStarted = onJobStarted,
        onJobCompleted = onJobCompleted,
        onJobCancelled = onJobCancelled,
    )
}

