package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ToggleGestureRecording : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ToggleGestureRecording::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val isRecording = shortXContext.gesture.isGestureRecording()
        if (isRecording) {
            shortXContext.gesture.stopGestureRecording(true)
        } else {
            shortXContext.gesture.startGestureRecording()
        }
        Actor.Res.EMPTY
    }
}