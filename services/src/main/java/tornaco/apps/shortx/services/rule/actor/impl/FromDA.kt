package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.FromDA
import tornaco.apps.shortx.core.rule.asDefaultInput
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object FromDA : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FromDA::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ FromDA::class.java
        val da = requireNotNull(shortXContext.rule.getDirectActionById(d.daId)) {
            "Direct action not found: ${d.daId}"
        }

        val allParameters = d.funcParameterInputsList.toMutableList().apply {
            if (da.parametersList.isNotEmpty()) {
                logger.i("Inflate default parameters.")
                val inflatingParameters = da.parametersList
                    .filterNot { funcParamF ->
                        // Contains in FromDA.input or FromDA.DA.input
                        parameterInputs.any { funcParamI ->
                            funcParamI.name == funcParamF.name
                        } || d.funcParameterInputsList.any { funcParamI ->
                            funcParamI.name == funcParamF.name
                        }
                    }
                    .map { it.asDefaultInput() }

                logger.logOnDebug("inflatingParameters: ${inflatingParameters.map { it.toLogString() }}")

                addAll(inflatingParameters)
            }
        } + parameterInputs
        logger.logOnDebug("allParameters: ${allParameters.map { it.toLogString() }}")

        run loop@{
            if (da.actionsList.isNullOrEmpty().not()) {
                da.actionsList.forEach { actionProto ->
                    val evaRes =
                        recursiveExecute(
                            evaluateContext,
                            actionProto,
                            contextData,
                            localVars,
                            allParameters
                        )
                    if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                        logger.e(evaRes.error, "evaluate fail")
                        if (evaRes.shouldContinue) {
                            logger.d("Action on error is continue execute.")
                        } else {
                            return@loop
                        }
                    } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                        logger.w("evaluate break!")
                        return@loop
                    }
                }
            }
        }

    }
}