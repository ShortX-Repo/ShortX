package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowFloatWindow : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowFloatWindow::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowFloatWindow::class.java
            shortXContext.overlay.sideBarManager.postShowSideBar(
                evaluateContext,
                contextData.map.mapValues { it.value.toString() },
                localVars,
                parameterInputs,
                tornaco.apps.shortx.core.proto.action.ShowSideBar.newBuilder()
                    .apply {
                        if (d.tag.isBlank()) {
                            logger.i("Use action id as float window tag: ${d.id}")
                            tag = d.id
                        }
                    }.build()
            )
        }
        Actor.Res.EMPTY
    }
}