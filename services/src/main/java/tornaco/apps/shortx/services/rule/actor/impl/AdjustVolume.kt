package tornaco.apps.shortx.services.rule.actor.impl

import android.media.AudioManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object AdjustVolume : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.AdjustVolume::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.AdjustVolume::class.java

        androidContext.audioManager.adjustVolume(
            d.direction.number.apply {
                logger.d("adjustVolume: $this")
            },
            if (d.showUI) AudioManager.FLAG_SHOW_UI else 0
        )
    }

}