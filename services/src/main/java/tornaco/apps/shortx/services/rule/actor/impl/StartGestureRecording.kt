package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StartGestureRecording : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StartGestureRecording::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        if (!shortXContext.gesture.isGestureRecording()) {
            logger.w("startGestureRecording ignored while it is already started")
            shortXContext.gesture.startGestureRecording()
        }
    }
}