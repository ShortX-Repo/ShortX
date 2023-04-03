package tornaco.apps.shortx.services.notification

import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.proto.fact.NotificationPosted
import tornaco.apps.shortx.core.proto.fact.NotificationRemoved
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

class NotificationService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    fun registerPostedByApp(n: Notification) {
        logger.d("registerPostedByApp: $n")
        systemServiceScope.launchSafety {
            shortXContext.rule.publishFact(
                "registerPostedByApp",
                NotificationPosted.newBuilder().setRecord(n).build()
            )
        }
    }

    fun registerUpdatedByApp(n: Notification) {
        logger.d("registerUpdatedByApp: $n")
        systemServiceScope.launchSafety {
            shortXContext.rule.publishFact(
                "registerUpdatedByApp",
                NotificationUpdated.newBuilder().setNew(n).build(),
                n.contextMap()
            )
        }
    }

    fun registerDismissedByUser(n: Notification) {
        logger.d("registerDismissedByUser: $n")
        systemServiceScope.launchSafety {
            shortXContext.rule.publishFact(
                "registerDismissedByUser",
                NotificationRemoved.newBuilder().setByUser(true).setRecord(n).build(),
                n.contextMap()
            )
        }
    }

    fun registerRemovedByApp(n: Notification) {
        logger.d("registerRemovedByApp: $n")
        systemServiceScope.launchSafety {
            shortXContext.rule.publishFact(
                "registerDismissedByUser",
                NotificationRemoved.newBuilder().setByUser(false).setRecord(n).build(),
                n.contextMap()
            )
        }
    }

    private fun Notification.contextMap() = mapOf(
        ContextDataMapping.NotificationRemoved.title.name to title,
        ContextDataMapping.NotificationRemoved.contentText.name to contentText,
        ContextDataMapping.NotificationRemoved.pkgName.name to appsList.first().pkgName,
        ContextDataMapping.NotificationRemoved.userId.name to appsList.first().userId,
    )
}