package tornaco.apps.shortx.services.rule

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.prebuiltActorFor

class ActionEvaluatorImpl(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val handler: Handler,
    private val conditionEvaluator: ConditionEvaluator
) : ActionEvaluator {
    private val logger = Logger("ActionEvaluatorImpl")
    private val recursiveExecute: suspend (logTag: String, action: ProtoAny, contextData: ContextData) -> Unit =
        { logTagR, actionR, contextDataR ->
            evaluate(logTagR, actionR, contextDataR)
        }

    @SuppressLint("MissingPermission")
    override suspend fun evaluate(logTag: String, action: ProtoAny, contextData: ContextData) {
        val actor = prebuiltActorFor(action)
        logger.d("evaluate: $logTag - ${action.typeUrl}, actor: $actor")
        actor?.handle(
            HandleParams(
                context,
                shortXContext,
                action,
                contextData,
                recursiveExecute,
                conditionEvaluator
            )
        )
    }
}