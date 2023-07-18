package tornaco.apps.shortx.services.rule.actor

import android.content.Context
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.ConditionEvaluator

fun dummyHandlerParamsForAction(context: Context, shortXContext: ShortXContext, action: ProtoAny) =
    HandleParams(
        context = context,
        shortXContext = shortXContext,
        action = action,
        contextData = ContextData.obtain(),
        recursiveExecute = { _, _, _ ->
            throw java.lang.IllegalStateException("Dummy")
        },
        conditionEvaluator = object : ConditionEvaluator {
            override suspend fun evaluate(
                condition: ProtoAny,
                contextData: ContextData
            ): ConditionEvaluator.EvaluateResult {
                throw IllegalStateException("Dummy")
            }

            override suspend fun evaluate(
                logTag: String,
                condOp: ConditionOperator,
                condOpPayload: ConditionOperatorPayload,
                conditions: List<ProtoAny>,
                contextData: ContextData
            ): Boolean {
                throw IllegalStateException("Dummy")
            }
        }
    )