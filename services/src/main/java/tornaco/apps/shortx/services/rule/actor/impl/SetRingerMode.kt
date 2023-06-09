package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SetRingerMode : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetRingerMode::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.SetRingerMode::class.java
        context.audioManager.ringerMode = data.mode.number
    }
}