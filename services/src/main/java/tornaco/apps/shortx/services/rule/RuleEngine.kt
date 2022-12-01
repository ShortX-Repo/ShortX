package tornaco.apps.shortx.services.rule

import android.content.Context
import android.os.Handler
import android.widget.Toast
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.proto.action.*
import tornaco.apps.shortx.core.proto.condition.CurrentPkg
import tornaco.apps.shortx.core.proto.condition.False
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.engine.*
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.postSafety
import kotlin.math.min

object RuleEngineFactory {
    private fun newConditionEvaluator(shortXContext: ShortXContext) = object : ConditionEvaluator {
        override fun evaluate(condition: ProtoAny): Boolean {
            return when {
                condition is_ CurrentPkg::class.java -> {
                    val currentPkg = condition unpack_ CurrentPkg::class.java
                    val state = shortXContext.appState.currentFgComponentName.value
                    currentPkg.pkgName == state.pkg.pkgName
                }
                condition is_ True::class.java -> {
                    true
                }
                condition is_ False::class.java -> {
                    false
                }
                else -> false
            }
        }
    }

    private fun newActionEvaluator(context: Context, handler: Handler) = object : ActionEvaluator {
        override suspend fun evaluate(action: ProtoAny) {
            if (action is_ StartActivity::class.java) {
                val startActivity = action unpack_ StartActivity::class.java
                tornaco.apps.shortx.services.actor.StartActivity(
                    context,
                    startActivity.componentNameAsString
                ).invoke()
            } else if (action is_ TakeScreenshot::class.java) {
                tornaco.apps.shortx.services.actor.TakeScreenshot(context, handler).invoke()
            } else if (action is_ ShellCommand::class.java) {
                val sc = action unpack_ ShellCommand::class.java
                tornaco.apps.shortx.services.actor.ShellCommand(context, sc.command).invoke()
            } else if (action is_ ShowToast::class.java) {
                val t = action unpack_ ShowToast::class.java
                handler.postSafety {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            } else if (action is_ Delay::class.java) {
                val d = action unpack_ Delay::class.java
                delay(min(0, d.time))
            }
        }
    }

    fun newRuleEngine(
        shortXContext: ShortXContext,
        context: Context,
        handler: Handler,
        factMatcher: FactMatcher = FactMatcherFactory.factMatcher(),
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

