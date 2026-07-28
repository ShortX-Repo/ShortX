package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowCustomRecents : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowCustomRecents::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        shortXContext.wm.showRecentTasks()
        Actor.Res.EMPTY
    }
}
