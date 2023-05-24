package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object RemoteExecuteMVEL : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.RemoteExecuteMVEL::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        val d =
            params.action unpack_ tornaco.apps.shortx.core.proto.action.RemoteExecuteMVEL::class.java
        MVELExecutor(params.context, params.shortXContext, d.expression).invoke().apply {
            params.contextData.add(ContextDataMapping.ExecuteMVEL.mvelRet.name, this)
        }
    }
}