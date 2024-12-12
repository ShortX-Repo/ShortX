package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowClipboardView : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowClipboardView::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        shortXContext.wm.showClipboard()
        Actor.Res.EMPTY
    }
}