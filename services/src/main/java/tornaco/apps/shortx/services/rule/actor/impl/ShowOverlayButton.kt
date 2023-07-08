package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowOverlayButton : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowOverlayButton::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d = action unpack_ ShowOverlayButton::class.java
            shortXContext.overlay.postShowOverlay(
                ShowOverlayButton.newBuilder(d).apply {
                    if (d.tag.isBlank()) {
                        logger.i("Use action id as overlay tag: ${d.id}")
                        tag = d.id
                    }
                }.build()
            )
        }
    }
}