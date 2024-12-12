package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object HideFloatWindow : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.HideFloatWindow::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        val d =
            params.action unpack_ tornaco.apps.shortx.core.proto.action.HideFloatWindow::class.java
        withContext(Dispatchers.Main) {
            if (d.overlayTagsList.isNullOrEmpty()) {
                params.shortXContext.overlay.postHideAllFloatWindow()
            } else {
                params.shortXContext.overlay.postHideFloatWindow(d.overlayTagsList)
            }
        }
        return Actor.Res.EMPTY
    }
}