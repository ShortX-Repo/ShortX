package tornaco.apps.shortx.services.rule.actor

import android.content.Context
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.cond.ConditionEvaluator
import tornaco.apps.shortx.services.rule.fact.FactMatcher

fun dummyHandlerParamsForAction(
    androidContext: Context,
    shortXContext: ShortXContext,
    evaluateContext: EvaluateContext,
    action: ProtoAny
) =
    HandleParams(
        androidContext = androidContext,
        androidUiContext = androidContext,
        shortXContext = shortXContext,
        evaluateContext = evaluateContext,
        action = action,
        contextData = ContextData.obtain(),
        localVars = LocalVars.obtain(),
        recursiveExecute = { _, _, _, _, _ ->
            throw java.lang.IllegalStateException("Dummy")
        },
        conditionEvaluator = object : ConditionEvaluator {
            override suspend fun evaluate(
                evaluateContext: EvaluateContext,
                condition: ProtoAny,
                contextData: ContextData,
                localVars: LocalVars,
                parameterInputs: List<FuncParameterInput>
            ): ConditionEvaluator.EvaluateResult {
                TODO("Dummy")
            }

            override suspend fun evaluate(
                evaluateContext: EvaluateContext,
                condOp: ConditionOperator,
                condOpPayload: ConditionOperatorPayload,
                conditions: List<ProtoAny>,
                contextData: ContextData,
                localVars: LocalVars,
                parameterInputs: List<FuncParameterInput>
            ): Boolean {
                TODO("Dummy")
            }

        },
        factMatcher = object : FactMatcher {
            override fun match(
                actualFact: ProtoMessage,
                ruleFacts: List<ProtoAny>
            ): FactMatcher.MatcherResult {
                TODO("Dummy")
            }
        }
    )