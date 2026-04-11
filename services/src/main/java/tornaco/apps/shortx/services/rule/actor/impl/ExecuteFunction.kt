package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.GenericContextData
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.func.FuncReturnValue
import tornaco.apps.shortx.core.rule.asDefaultInput
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions

private const val CONTEXT_ACTIVE_FUNCTION_IDS = "__activeFunctionIds"

object ExecuteFunction : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ExecuteFunction::class.java
    }

    override suspend fun handle(params: HandleParams) = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.ExecuteFunction::class.java
        val function = requireNotNull(shortXContext.rule.getFunctionById(data.functionId)) {
            "Function not found: ${data.functionId}"
        }

        val activeFunctionIds = (contextData.get(CONTEXT_ACTIVE_FUNCTION_IDS) as? List<*>)
            ?.filterIsInstance<String>()
            ?.toMutableList()
            ?: mutableListOf()

        require(function.id !in activeFunctionIds) {
            "Recursive function call detected: ${function.id}"
        }
        activeFunctionIds.add(function.id)

        val allParameters = data.funcParameterInputsList.toMutableList().apply {
            if (function.parametersList.isNotEmpty()) {
                logger.i("Inflate default function parameters.")
                val inflatingParameters = function.parametersList
                    .filterNot { funcParamF ->
                        parameterInputs.any { funcParamI ->
                            funcParamI.name == funcParamF.name
                        } || data.funcParameterInputsList.any { funcParamI ->
                            funcParamI.name == funcParamF.name
                        }
                    }
                    .map { it.asDefaultInput() }

                logger.logOnDebug(
                    "inflatingParameters: ${inflatingParameters.map { it.toLogString() }}"
                )

                addAll(inflatingParameters)
            }
        } + parameterInputs
        logger.logOnDebug("allParameters: ${allParameters.map { it.toLogString() }}")

        val functionContext = ContextData.obtain(
            contextData.map.mapValues { it.value ?: "" } + mapOf(
                GenericContextData.CONTEXT_FUNC_RETURN_VALUE to FuncReturnValue.newBuilder()
                    .setValue("")
                    .build(),
                CONTEXT_ACTIVE_FUNCTION_IDS to activeFunctionIds.toList()
            )
        )

        try {
            copy(
                contextData = functionContext,
                parameterInputs = allParameters
            ).runSubActions(function.actionsList)

            val returnValue = when (val value = functionContext.get(GenericContextData.CONTEXT_FUNC_RETURN_VALUE)) {
                is FuncReturnValue -> value
                is String -> FuncReturnValue.newBuilder().setValue(value).build()
                else -> FuncReturnValue.newBuilder().setValue("").build()
            }
            contextData.override(GenericContextData.CONTEXT_FUNC_RETURN_VALUE, returnValue)
            Actor.Res(returnValue.value)
        } finally {
            functionContext.recycle()
        }
    }
}