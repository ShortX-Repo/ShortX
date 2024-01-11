package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.ActionEvaluatorImpl
import tornaco.apps.shortx.services.rule.cond.ConditionEvaluator
import tornaco.apps.shortx.services.rule.cond.ConditionEvaluatorImpl
import tornaco.apps.shortx.services.rule.fact.FactMatcher
import tornaco.apps.shortx.services.rule.fact.FactMatcherImpl

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
        factMatcher: FactMatcher,
        actionEvaluateStartListener: (actionId: String) -> Unit,
        actionEvaluateResultListener: (ActionEvaluator.EvaluateResult) -> Unit,
    ) = ActionEvaluatorImpl(
        context,
        shortXContext,
        handler,
        actionEvaluateStartListener,
        actionEvaluateResultListener,
        conditionEvaluator,
        factMatcher
    )

    private fun newFactMatcher(shortXContext: ShortXContext) = FactMatcherImpl { shortXContext }

    fun newRuleEngine(
        shortXContext: ShortXContext,
        androidContext: Context,
        handler: Handler,
        actionEvaluateStartListener: (actionId: String) -> Unit,
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
            actionEvaluateStartListener = actionEvaluateStartListener,
            actionEvaluateResultListener = actionEvaluateResultListener,
            factMatcher = factMatcher
        ),
        onJobStarted: (String) -> Unit,
        onJobCompleted: (String) -> Unit,
        onJobCancelled: (String) -> Unit,
        scope: CoroutineScope = CoroutineScope(SupervisorJob()),
        isRuleFeatureEnabled: () -> Boolean = { shortXContext.rule.isRuleFeatureEnabled() },
        isSystemReadyPublished: () -> Boolean = { shortXContext.user.isSystemReadyTriggered },
    ) = DefaultRuleEngine(
        androidContext = androidContext,
        shortXContext = shortXContext,
        factMatcher = factMatcher,
        conditionEvaluator = conditionEvaluator,
        actionEvaluator = actionEvaluator,
        coroutineScope = scope,
        onJobStarted = onJobStarted,
        onJobCompleted = onJobCompleted,
        onJobCancelled = onJobCancelled,
        isRuleFeatureEnabled = isRuleFeatureEnabled,
        isSystemReadyPublished = isSystemReadyPublished,
    )
}

