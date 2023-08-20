package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.proto.action.ExecuteMVEL
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.Interceptors
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ExecuteMVEL : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ExecuteMVEL::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        require(Interceptors.checkExecuteMVEL(params.shortXContext, params.androidContext))
        val d = params.action unpack_ ExecuteMVEL::class.java
        MVELExecutor(
            context = params.androidContext,
            shortXContext = params.shortXContext,
            expression = d.expression.inflateContextMap(params.contextData.map.mapValues { entry ->
                // Wrap all data with ""
                entry.value?.let {
                    "\"$it\""
                }
            }).apply {
                logger.d("inflated expression: $this")
            }
        ).invoke(vars = params.contextData.map, localVars = params.localVars).apply {
            params.contextData.add(
                d.customContextDataKey.forKey(ContextDataMapping.ExecuteMVEL.mvelRet.name)
                    .fallbackOnEmpty(ContextDataMapping.ExecuteMVEL.mvelRet.name),
                this
            )
        }
    }
}