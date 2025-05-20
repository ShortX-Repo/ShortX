package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object PlayRingtone : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PlayRingtone::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.PlayRingtone::class.java
        shortXContext.audio.playRingtone(d.ringtone)?.apply {
            while (isPlaying) {
                logger.d("Ringtone playing, delay 500.")
                delay(500)
            }
        }
        Actor.Res.EMPTY
    }
}