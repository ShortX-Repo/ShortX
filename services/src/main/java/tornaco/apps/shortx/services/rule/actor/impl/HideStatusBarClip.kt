package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object HideStatusBarClip : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.HideStatusBarClip::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        logger.d("hideStatusBarClip.")
        withContext(Dispatchers.Main) {
            shortXContext.statusbar.hideStatusBarClip()
        }
    }
}