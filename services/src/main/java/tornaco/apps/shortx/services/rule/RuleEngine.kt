package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import tornaco.apps.shortx.core.proto.action.StartActivity
import tornaco.apps.shortx.core.proto.action.TakeScreenshot
import tornaco.apps.shortx.core.proto.condition.CurrentPkg
import tornaco.apps.shortx.core.proto.condition.False
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.engine.*
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

object RuleEngineFactory {
    private fun newConditionEvaluator() = object : ConditionEvaluator {
        override suspend fun evaluate(condition: ProtoAny): Boolean {
            return when {
                condition is_ CurrentPkg::class.java -> {
                    val currentPkg = condition unpack_ CurrentPkg::class.java
                    currentPkg.pkgName == "p"
                }
                condition is_ True::class.java -> {
                    true
                }
                condition is_ False::class.java -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun newActionEvaluator(context: Context, handler: Handler) = object : ActionEvaluator {
        override suspend fun evaluate(action: ProtoAny) {
            if (action is_ StartActivity::class.java) {
                val startActivity = action unpack_ StartActivity::class.java
            } else if (action is_ TakeScreenshot::class.java) {
                tornaco.apps.shortx.services.actor.TakeScreenshot(context, handler).invoke()
            }
        }
    }

    fun newRuleEngine(
        context: Context,
        handler: Handler,
        factMatcher: FactMatcher = FactMatcherFactory.factMatcher(),
        conditionEvaluator: ConditionEvaluator = newConditionEvaluator(),
        actionEvaluator: ActionEvaluator = newActionEvaluator(context, handler),
        listener: EngineListener = LogEngineListener("RuleEngine"),
    ) = DefaultRuleEngine(factMatcher = factMatcher,
        conditionEvaluator = conditionEvaluator,
        actionEvaluator = actionEvaluator,
        listener = listener)
}

