package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object AudioRecording : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.AudioRecording::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        runCatching {
            val d = action unpack_ tornaco.apps.shortx.core.proto.action.AudioRecording::class.java
            if (shortXContext.audio.isRecording) {
                shortXContext.audio.stopRecording()
                return@runCatching
            }

            shortXContext.audio.startRecording(d.src, d.fileNamePrefix)

            while (shortXContext.audio.isRecording) {
                delay(500)
            }

            logger.i("DONE")
        }.onFailure {
            logger.d("onFailure: $it")
            if (it is CancellationException) {
                logger.i("Stop recording since this action is canceled.")
                shortXContext.audio.stopRecording()
            } else {
                throw it
            }
        }

        Actor.Res.EMPTY
    }
}