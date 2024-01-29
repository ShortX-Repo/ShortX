package tornaco.apps.shortx.services.rule.cond

import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny

interface ConditionEvaluator {
    suspend fun evaluate(
        evaluateContext: EvaluateContext,
        condition: ProtoAny,
        contextData: ContextData,
        localVars: LocalVars
    ): EvaluateResult

    suspend fun evaluate(
        evaluateContext: EvaluateContext,
        condOp: ConditionOperator,
        condOpPayload: ConditionOperatorPayload,
        conditions: List<ProtoAny>,
        contextData: ContextData,
        localVars: LocalVars
    ): Boolean

    sealed interface EvaluateResult {
        val evaluateContext: EvaluateContext

        data class Success(
            val condition: ProtoAny,
            val match: Boolean,
            val reason: String,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Fail(
            val condition: ProtoAny,
            val error: Throwable,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Break(override val evaluateContext: EvaluateContext) : EvaluateResult
        data class Disabled(override val evaluateContext: EvaluateContext) : EvaluateResult
    }
}

fun ConditionEvaluator.EvaluateResult.isMatch() =
    (this is ConditionEvaluator.EvaluateResult.Success && this.match) || this is ConditionEvaluator.EvaluateResult.Disabled