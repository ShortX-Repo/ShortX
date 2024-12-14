package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object HideSideBar : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.HideSideBar::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        withContext(Dispatchers.Main) {
            params.shortXContext.overlay.sideBarManager.postHideSideBar()
        }
        return Actor.Res.EMPTY
    }
}