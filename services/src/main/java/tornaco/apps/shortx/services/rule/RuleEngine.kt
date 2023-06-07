package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import tornaco.apps.shortx.services.ShortXContext

object RuleEngineFactory {
    private fun newConditionEvaluator(
        context: Context,
        shortXContext: ShortXContext,
        evaluateResultListener: (ConditionEvaluator.EvaluateResult) -> Unit,
    ) =
        ConditionEvaluatorImpl(context, shortXContext, evaluateResultListener)

    private fun newActionEvaluator(
        context: Context,
        shortXContext: ShortXContext,
        handler: Handler,
        conditionEvaluator: ConditionEvaluator,
        actionEvaluateResultListener: (ActionEvaluator.EvaluateResult) -> Unit,
    ) = ActionEvaluatorImpl(
        context,
        shortXContext,
        handler,
        actionEvaluateResultListener,
        conditionEvaluator
    )

    private fun newFactMatcher(shortXContext: ShortXContext) = FactMatcherImpl(shortXContext)

    fun newRuleEngine(
        shortXContext: ShortXContext,
        androidContext: Context,
        handler: Handler,
        actionEvaluateResultListener: (ActionEvaluator.EvaluateResult) -> Unit,
        conditionEvaluateResultListener: (ConditionEvaluator.EvaluateResult) -> Unit,
        factMatcher: FactMatcher = newFactMatcher(shortXContext),
        conditionEvaluator: ConditionEvaluator = newConditionEvaluator(
            context = androidContext,
            shortXContext = shortXContext,
            evaluateResultListener = conditionEvaluateResultListener
        ),
        actionEvaluator: ActionEvaluator = newActionEvaluator(
            context = androidContext,
            shortXContext = shortXContext,
            handler = handler,
            conditionEvaluator = conditionEvaluator,
            actionEvaluateResultListener = actionEvaluateResultListener
        ),
        onJobStarted: (String) -> Unit,
        onJobCompleted: (String) -> Unit,
        onJobCancelled: (String) -> Unit,
        scope: CoroutineScope = CoroutineScope(SupervisorJob()),
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

