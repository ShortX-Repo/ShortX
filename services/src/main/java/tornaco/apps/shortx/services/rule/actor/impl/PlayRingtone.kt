package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.CancellationException
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
        val player = shortXContext.audio.playRingtone(d.ringtone)
        requireNotNull(player) { "PlayRingtone, player is null. Maybe is in call now???" }
        kotlin.runCatching {
            while (player.isPlaying) {
                logger.d("Ringtone playing, delay 500.")
                delay(500)
            }
        }.onFailure {
            if (it is CancellationException) {
                logger.d("PlayRingtone cancelled.")
                player.stop()
                throw it
            }
        }
        Actor.Res.EMPTY
    }
}