package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object EnableViewIdViewer : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.EnableViewIdViewer::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        shortXContext.wm.withUiAutomation("enableIdViewer") {
            it.enableIdViewer()
        }
        Actor.Res.EMPTY
    }
}