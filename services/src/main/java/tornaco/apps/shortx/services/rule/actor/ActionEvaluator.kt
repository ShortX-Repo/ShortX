package tornaco.apps.shortx.services.rule.actor


import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.BreakActionExecuteScope
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny

interface ActionEvaluator {
    suspend fun evaluate(
        evaluateContext: EvaluateContext,
        action: ProtoAny,
        contextData: ContextData,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput> = emptyList()
    ): EvaluateResult

    sealed interface EvaluateResult {
        val evaluateContext: EvaluateContext
        val actionId: String

        data class Success(
            override val actionId: String,
            val action: ProtoAny,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Fail(
            val shouldContinue: Boolean,
            override val actionId: String,
            val action: ProtoAny,
            val error: Throwable,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Break(
            override val actionId: String,
            val action: ProtoAny,
            val scope: BreakActionExecuteScope,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult

        data class Disabled(
            override val actionId: String,
            val action: ProtoAny,
            override val evaluateContext: EvaluateContext
        ) : EvaluateResult
    }
}
