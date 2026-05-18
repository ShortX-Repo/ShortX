package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.ShowActionSidebar
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ShowActionSidebar : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return ShowActionSidebar::class.java
    }

    override suspend fun handle(params: HandleParams) = with(params) {
        withContext(Dispatchers.Main) {
            val data = action unpack_ ShowActionSidebar::class.java
            shortXContext.overlay.actionSidebarManager.postShowActionSidebar(
                evaluateContext = evaluateContext,
                localVars = localVars,
                parameters = parameterInputs,
                action = ShowActionSidebar.newBuilder(data).apply {
                    if (data.tag.isBlank()) {
                        tag = data.id
                    }
                }.build(),
            )
        }
        Actor.Res.EMPTY
    }
}
