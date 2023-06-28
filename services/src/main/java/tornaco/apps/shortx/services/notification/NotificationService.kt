package tornaco.apps.shortx.services.notification

import android.app.INotificationManager
import android.content.Context
import android.os.ServiceManager
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.proto.fact.NotificationPosted
import tornaco.apps.shortx.core.proto.fact.NotificationRemoved
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

class NotificationService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private var lastPostedN: Pair<Notification, Long>? = null

    private val appRes by lazy {
        AppResources(
            context,
            BuildProp.APP_PACKAGE_NAME
        )
    }

    val notificationPoster by lazy {
        NotificationPoster(
            context,
            "ShortX silence",
            "ShortX alert",
            "ShortX",
            getIcon = { remixNam ->
                val resName = Remix.allFlat[remixNam]
                resName?.let {
                    appRes.getIcon(it)
                }
            },
            getBitmap = {
                appRes.getBitmap(it)
            }
        )
    }


    fun registerPostedByApp(n: Notification) {
        logger.p("registerPostedByApp: $n")
        if (checkIfShouldSkip(n)) {
            logger.i("shouldSkipFact is true")
        } else {
            systemServiceScope.launchSafety {
                shortXContext.rule.publishFact(
                    "registerPostedByApp",
                    NotificationPosted.newBuilder().setRecord(n).build(),
                    n.contextMap()
                )
            }
        }
    }

    private fun checkIfShouldSkip(n: Notification): Boolean {
        val lastNROrNull = lastPostedN
        var shouldSkipFact = false
        if (lastNROrNull != null && System.currentTimeMillis() - lastNROrNull.second < 300) {
            val lastN = lastNROrNull.first
            if (lastN.appsList.first() == n.appsList.first() && lastN.title == n.title && lastN.contentText == n.contentText) {
                shouldSkipFact = true
            }
        }

        lastPostedN = n to System.currentTimeMillis()
        logger.d("shouldSkipFact: $shouldSkipFact")
        return shouldSkipFact
    }

    fun registerUpdatedByApp(n: Notification) {
        logger.p("registerUpdatedByApp: $n")
        systemServiceScope.launchSafety {
            shortXContext.rule.publishFact(
                "registerUpdatedByApp",
                NotificationUpdated.newBuilder().setNew(n).build(),
                n.contextMap()
            )
        }
    }

    fun registerDismissedByUser(n: Notification) {
        logger.p("registerDismissedByUser: $n")
        systemServiceScope.launchSafety {
            shortXContext.rule.publishFact(
                "registerDismissedByUser",
                NotificationRemoved.newBuilder().setByUser(true).setRecord(n).build(),
                n.contextMap()
            )
        }
    }

    fun registerRemovedByApp(n: Notification) {
        logger.p("registerRemovedByApp: $n")
        systemServiceScope.launchSafety {
            shortXContext.rule.publishFact(
                "registerDismissedByUser",
                NotificationRemoved.newBuilder().setByUser(false).setRecord(n).build(),
                n.contextMap()
            )
        }
    }

    fun removeNotificationForPackage(appPkgs: List<AppPkg>) {
        logger.p("Remove n for pkg: ${appPkgs.map { it.appPkgToString() }}")
        val nms =
            INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE))
        requireNotNull(nms) { "Can not get nms" }
        appPkgs.forEach {
            nms.cancelAllNotifications(it.pkgName, it.userId)
            logger.p("Notification has been removed: ${it.appPkgToString()}")
        }
    }

    private fun Notification.contextMap() = mapOf(
        ContextDataMapping.NotificationRemoved.title.name to title,
        ContextDataMapping.NotificationRemoved.contentText.name to contentText,
        ContextDataMapping.NotificationRemoved.pkgName.name to appsList.first().pkgName,
        ContextDataMapping.NotificationRemoved.userId.name to appsList.first().userId,
    )
}