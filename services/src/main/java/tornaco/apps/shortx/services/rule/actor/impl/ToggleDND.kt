package tornaco.apps.shortx.services.rule.actor.impl

import android.app.NotificationManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.util.notificationManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ToggleDND : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ToggleDND::class.java
    }

    override suspend fun handle(params: HandleParams) = with(params) {
        val notificationManager = androidContext.notificationManager
        val isEnabled = notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
        notificationManager.setInterruptionFilter(
            if (isEnabled) NotificationManager.INTERRUPTION_FILTER_ALL
            else NotificationManager.INTERRUPTION_FILTER_PRIORITY
        )
        Actor.Res.EMPTY
    }
}
