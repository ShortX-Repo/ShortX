package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.util.statusBarManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ExpandNotification : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ExpandNotification::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        // TODO API
        params.androidContext.statusBarManager.expandNotificationsPanel()
        return Actor.Res.EMPTY
    }
}