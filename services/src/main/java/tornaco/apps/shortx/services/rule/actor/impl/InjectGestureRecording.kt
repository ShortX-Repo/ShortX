package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.rule.action.emptyByteArrayWrapper
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.time.Duration


object InjectGestureRecording : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.InjectGestureRecording::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val isRecording = shortXContext.gesture.isGestureRecording()
        require(!isRecording) {
            "WTF? Trying to inject gesture while recording!"
        }

        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.InjectGestureRecording::class.java
        logger.d("InjectGestureRecording start.")
        val receiver = SynchronousResultReceiver.get()
        shortXContext.gesture.injectGestureRecord(
            data.gestureId,
            data.speed,
            data.showGesturePathView,
            receiver
        )

        withContext(Dispatchers.IO) {
            receiver.awaitResultNoInterrupt(Duration.ofDays(1)).getValue(emptyByteArrayWrapper)
        }

        logger.d("InjectGestureRecording complete.")
        Actor.Res.EMPTY
    }
}