package tornaco.apps.shortx.services.notification

import android.app.INotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
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
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services.app.NMSFuncs
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.util.AppServiceProxy
import tornaco.apps.shortx.services.watchdog.launchSafety

class NotificationService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "NotificationService") {
    private val appRes by lazy {
        AppResources(
            context,
            BuildProp.APP_PACKAGE_NAME
        )
    }

    private val attachedPoster = RemoteCallbackList<INotificationPoster>()

    private val fallbackNotificationPoster by lazy {
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

    private val nms by lazy { ServiceRetriever(context.classLoader).getNMS() }

    override fun systemReady() {
        super.systemReady()
    }

    fun setPostNByAppEnabled(enable: Boolean) {
        store.write(Settings.BoolSettings.PostNByApp, enable)
    }

    fun isPostNByAppEnabled(): Boolean {
        return store.read(Settings.BoolSettings.PostNByApp)
    }

    fun AppPkg.hasNotification() = hasNotification(NMSFuncs.getActiveNotifications(nms))

    fun AppPkg.hasNotification(allN: List<Notification>) = allN.any { notification ->
        notification.appsList.any {
            it == this
        }
    }

    fun getActiveNotifications(): List<Notification> = NMSFuncs.getActiveNotifications(nms).also {
        logger.p(
            "getActiveNotifications, return: ${
                it.joinToString(System.lineSeparator())
            }"
        )
    }

    fun post(postNotification: PostNotification) {
        if (isPostNByAppEnabled()) {
            logger.d("post by app.")
            AppNotificationServiceProxy.post(context, postNotification)
        } else {
            logger.d("post by attached.")
            if (!postByAttached(postNotification)) {
                fallbackNotificationPoster.invoke(postNotification)
            }
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

    fun removeNotificationByTag(tag: String) {
        logger.d("removeNotificationByTag: $tag")

        AppNotificationServiceProxy.cancelWithTag(context, tag)

        synchronized(attachedPoster) {
            val itemCount = attachedPoster.beginBroadcast()
            if (itemCount == 0) {
                logger.d("removeNotificationByTag, no registered poster.")
                return
            }
            val first = attachedPoster.getBroadcastItem(0)
            logger.d("removeNotificationByTag, Using poster: $first")
            kotlin.runCatching { first.cancelWithTag(tag) }
                .getOrElse {
                    logger.e(it, "Error call cancelWithTag")
                    null
                }
            attachedPoster.finishBroadcast()
        }

        // Below logic not work, no error, no log.
        if (false) {
            val nms =
                INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE))
            requireNotNull(nms) { "Can not get nms" }
            getActiveNotifications().filter { it.tag == tag }.forEach {
                val pkg = it.appsList[0]
                val id = it.notificationId.toIntOrNull() ?: 0
                logger.d("removeNotificationByTag, pkg: ${pkg.appPkgToString()}, id: $id")
                nms.cancelNotificationWithTag("android", "android", tag, id, pkg.userId)
            }
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


internal class AppNotificationServiceProxy(context: Context, intent: Intent?) :
    AppServiceProxy(context, intent) {
    private val logger = tornaco.apps.shortx.core.util.Logger("AppNotificationServiceProxy")
    private var impl: INotificationPoster? = null

    override fun onConnected(binder: IBinder) {
        impl = INotificationPoster.Stub.asInterface(binder)
        logger.i("onConnected: $impl")
    }

    fun post(postNotification: PostNotification) {
        setTask(object : ProxyTask() {
            override fun run() {
                runCatching {
                    logger.i("call post: $impl")
                    requireNotNull(impl) { "impl is null." }.post(ByteArrayWrapper(postNotification.toByteArray()))
                }.onFailure {
                    logger.e(it, "ProxyTask")
                }
            }

        })
    }

    fun cancelWithTag(tag: String) {
        setTask(object : ProxyTask() {
            override fun run() {
                runCatching {
                    logger.i("call remove: $impl")
                    requireNotNull(impl) { "impl is null." }.cancelWithTag(tag)
                }.onFailure {
                    logger.e(it, "ProxyTask")
                }
            }

        })
    }

    companion object {
        fun post(context: Context, postNotification: PostNotification) {
            val intent = Intent()
            intent.setPackage(BuildProp.APP_PACKAGE_NAME)
            intent.component =
                ComponentName.unflattenFromString("${BuildProp.APP_PACKAGE_NAME}/${BuildProp.APP_NOTIFICATION_SERVICE_NAME}")
            return AppNotificationServiceProxy(context, intent).post(postNotification)
        }

        fun cancelWithTag(context: Context, tag: String) {
            val intent = Intent()
            intent.setPackage(BuildProp.APP_PACKAGE_NAME)
            intent.component =
                ComponentName.unflattenFromString("${BuildProp.APP_PACKAGE_NAME}/${BuildProp.APP_NOTIFICATION_SERVICE_NAME}")
            return AppNotificationServiceProxy(context, intent).cancelWithTag(tag)
        }
    }
}