package tornaco.apps.shortx.services.rule.func

import tornaco.apps.shortx.core.context.GenericContextData
import tornaco.apps.shortx.core.proto.func.Func
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.func.FuncReturnValue
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.HandleParams

interface FuncEvaluator {
    suspend fun evaluate(
        handleParams: HandleParams,
        func: Func,
        parameterInputs: List<FuncParameterInput>
    )
}

class FuncEvaluatorImpl(private val actionEvaluator: ActionEvaluator) : FuncEvaluator {
    private val logger = Logger("FuncEvaluator")

    override suspend fun evaluate(
        handleParams: HandleParams,
        func: Func,
        parameterInputs: List<FuncParameterInput>
    ) {
        val requiredParamNames = func.parametersList.filter { it.isRequired }.map { it.name }
        requiredParamNames.forEach { name ->
            require(parameterInputs.map { it.name }.contains(name)) {
                "Parameter: $name is required."
            }
        }

        with(handleParams) {
            func.actionsList.forEach loop@{
                actionEvaluator.evaluate(
                    evaluateContext = evaluateContext,
                    action = it,
                    contextData = contextData,
                    localVars = localVars,
                    parameterInputs = parameterInputs
                )
                val returnValue =
                    contextData.get(GenericContextData.CONTEXT_FUNC_RETURN_VALUE) as FuncReturnValue?
                if (returnValue != null) {
                    logger.d("Func returnValue: $returnValue")
                    return@loop
                }
            }
        }
    }

}