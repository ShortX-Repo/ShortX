package tornaco.apps.shortx.services.rule.actor.impl

import android.app.NotificationManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.SetDNDEnabled
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.notificationManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SetDNDEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetDNDEnabled::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ SetDNDEnabled::class.java
        val notificationManager = context.notificationManager
        val currentFilter = notificationManager.currentInterruptionFilter
        logger.d("currentFilter: $currentFilter")
        if (d.enable) {
            notificationManager.setInterruptionFilter(
                NotificationManager.INTERRUPTION_FILTER_PRIORITY
            )
        } else {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }
}