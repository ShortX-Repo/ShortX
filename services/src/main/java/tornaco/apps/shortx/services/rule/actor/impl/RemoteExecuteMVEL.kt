package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.Interceptors
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object RemoteExecuteMVEL : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.RemoteExecuteMVEL::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        require(Interceptors.checkExecuteMVEL(params.shortXContext, params.androidContext))
        val d =
            params.action unpack_ tornaco.apps.shortx.core.proto.action.RemoteExecuteMVEL::class.java
        MVELExecutor(
            params.androidContext,
            params.shortXContext,
            d.expression
        ).invoke(
            vars = params.contextData.map,
            localVars = params.localVars,
            parameterInputs = params.parameterInputs
        )
            .apply {
                params.contextData.add(
                    d.customContextDataKey.forKey(ContextDataMapping.ExecuteMVEL.mvelRet.name)
                        .fallbackOnEmpty { ContextDataMapping.ExecuteMVEL.mvelRet.name },
                    this
                )
            }
    }
}