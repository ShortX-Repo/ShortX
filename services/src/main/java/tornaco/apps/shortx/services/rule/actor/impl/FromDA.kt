package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.FromDA
import tornaco.apps.shortx.core.rule.asDefaultInput
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions


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

        runSubActions(da.actionsList)
        Actor.Res.EMPTY
    }
}