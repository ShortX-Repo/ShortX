package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.CurrentPkg
import tornaco.apps.shortx.core.proto.False
import tornaco.apps.shortx.core.proto.StartActivity
import tornaco.apps.shortx.core.proto.True
import tornaco.apps.shortx.core.rule.*

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

    private fun newActionEvaluator() = object : ActionEvaluator {
        override suspend fun evaluate(action: ProtoAny) {
            if (action is_ StartActivity::class.java) {
                val startActivity = action unpack_ StartActivity::class.java
            }
        }
    }

    fun newRuleEngine(
        factMatcher: FactMatcher = FactMatcherFactory.factMatcher(),
        conditionEvaluator: ConditionEvaluator = newConditionEvaluator(),
        actionEvaluator: ActionEvaluator = newActionEvaluator(),
        listener: EngineListener = LogEngineListener("RuleEngine"),
    ) = DefaultRuleEngine(factMatcher = factMatcher,
        conditionEvaluator = conditionEvaluator,
        actionEvaluator = actionEvaluator,
        listener = listener)
}

