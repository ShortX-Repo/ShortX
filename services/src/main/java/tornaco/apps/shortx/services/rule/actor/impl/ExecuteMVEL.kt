package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.ExecuteMVEL
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ExecuteMVEL : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ExecuteMVEL::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        val d = params.action unpack_ ExecuteMVEL::class.java
        MVELExecutor(
            context = params.context,
            shortXContext = params.shortXContext,
            expression = d.expression.inflateContextMap(params.contextData.map.mapValues { entry ->
                // Wrap all data with ""
                entry.value?.let {
                    "\"$it\""
                }
            }).apply {
                logger.d("inflated expression: $this")
            }
        ).invoke(params.contextData.map).apply {
            params.contextData.add(ContextDataMapping.ExecuteMVEL.mvelRet.name, this)
        }
    }
}