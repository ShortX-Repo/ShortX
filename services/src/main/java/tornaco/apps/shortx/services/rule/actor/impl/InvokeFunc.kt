package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.func.Func
import tornaco.apps.shortx.core.proto.func.FuncReturnValue
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object InvokeFunc : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.InvokeFunc::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.InvokeFunc::class.java
        val func = requireNotNull(Func.newBuilder().build()) {
            "Func with id: ${d.funcId} not exists."
        }

        val requiredParamNames = func.parametersList.filter { it.isRequired }.map { it.name }
        requiredParamNames.forEach { name ->
            require(parameterInputs.map { it.name }.contains(name)) {
                "Parameter: $name is required."
            }
        }

        with(params) {
            run loop@{
                func.actionsList.forEach {
                    val evaRes = recursiveExecute(
                        evaluateContext,
                        it,
                        contextData,
                        localVars,
                        parameterInputs
                    )
                    val returnValue =
                        contextData.get(tornaco.apps.shortx.core.context.GenericContextData.CONTEXT_FUNC_RETURN_VALUE) as FuncReturnValue?
                    if (returnValue != null) {
                        logger.d("Func returnValue: $returnValue")
                        return@loop
                    }

                    if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                        logger.e(evaRes.error, "evaluate fail")
                        if (evaRes.shouldContinue) {
                            logger.d("Action on error is continue execute.")
                        } else {
                            return@loop
                        }
                    } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                        val scope = evaRes.scope
                        logger.w("evaluate break! $scope")
                        scope.rethrowOrReturnLoop {
                            return@loop
                        }
                    }
                }
            }
        }
    }
}