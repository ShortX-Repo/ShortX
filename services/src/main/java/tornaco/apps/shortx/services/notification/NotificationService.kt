package tornaco.apps.shortx.services.notification

import android.app.INotificationManager
import android.content.Context
import android.os.RemoteCallbackList
import android.os.ServiceManager
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.proto.fact.NotificationPosted
import tornaco.apps.shortx.core.proto.fact.NotificationRemoved
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

class NotificationService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "NotificationService") {
    private val appRes by lazy {
        AppResources(
            context,
            BuildProp.APP_PACKAGE_NAME
        )
    }

    private val attachedPoster = RemoteCallbackList<INotificationPoster>()

    private val notificationPoster by lazy {
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

    fun post(postNotification: PostNotification) {
        if (!postByAttached(postNotification)) {
            notificationPoster.invoke(postNotification)
        }
    }

    private fun postByAttached(postNotification: PostNotification): Boolean {
        synchronized(attachedPoster) {
            val itemCount = attachedPoster.beginBroadcast()
            if (itemCount == 0) {
                logger.d("post, no registered poster.")
                return false
            }
            val first = attachedPoster.getBroadcastItem(0)
            logger.d("Using poster: $first")
            val shellRes =
                kotlin.runCatching { first.post(ByteArrayWrapper(postNotification.toByteArray())) }
                    .getOrElse {
                        logger.e(it, "Error call post")
                        null
                    }
            attachedPoster.finishBroadcast()
            return true
        }
    }

    fun attachNotificationPoster(from: String, poster: INotificationPoster) {
        logger.d("attachNotificationPoster: $from $poster")
        attachedPoster.register(poster)
    }

    fun registerPostedByApp(n: Notification) {
        logger.p("registerPostedByApp: $n")
        systemServiceScope.launchSafety {
            shortXContext.rule.publishFact(
                "registerPostedByApp",
                NotificationPosted.newBuilder().setRecord(n).build(),
                n.contextMap()
            )
        }
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
        ContextDataMapping.Notification.title.name to title,
        ContextDataMapping.Notification.contentText.name to contentText,
        ContextDataMapping.Notification.pkgName.name to appsList.first().pkgName,
        ContextDataMapping.Notification.userId.name to appsList.first().userId,
        ContextDataMapping.Notification.notificationTag.name to tag,
    )
}