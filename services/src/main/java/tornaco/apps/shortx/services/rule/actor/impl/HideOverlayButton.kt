package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.HideOverlayButton
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object HideOverlayButton : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.HideOverlayButton::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        val d = params.action unpack_ HideOverlayButton::class.java
        withContext(Dispatchers.Main) {
            if (d.overlayTagsList.isNullOrEmpty()) {
                params.shortXContext.overlay.postHideAllOverlay()
            } else {
                params.shortXContext.overlay.postHideOverlay(d.overlayTagsList)
            }
        }
    }
}