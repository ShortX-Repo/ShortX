package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ClickNotificationActionButton : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ClickNotificationActionButton::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d =
                action unpack_ tornaco.apps.shortx.core.proto.action.ClickNotificationActionButton::class.java
            shortXContext.notification.clickNotificationButton(d.notification, d.button)
        }
    }
}