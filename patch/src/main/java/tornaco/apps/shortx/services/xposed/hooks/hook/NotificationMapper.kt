package tornaco.apps.shortx.services.xposed.hooks.hook

import android.os.UserHandle
import android.service.notification.StatusBarNotification
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import util.Reflections

private val logger = Logger("NotificationMapper")

fun Any.androidNotificationRecordToNotification(): Notification? {
    return kotlin.runCatching {
        val sbn = Reflections
            .getObjectField(this, "sbn") as StatusBarNotification
        val n = sbn.notification ?: return null
        val isFgService = kotlin.runCatching { n.isForegroundService }.getOrElse { false }
        Notification.newBuilder()
            .setTitle(getTitle(n) ?: "")
            .setContentText(getContent(n) ?: "")
            .setIsFgService(isFgService)
            .addApps(
                AppPkg.newBuilder()
                    .setUserId(getNormalizedUserId(sbn))
                    .setPkgName(sbn.packageName)
                    .build()
            )
            .apply {
                if (OsUtils.isOOrAbove()) {
                    notificationChannel = n.channelId
                }
            }
            .build()

    }.getOrElse {
        logger.e(it, "androidNotificationRecordToNotification error")
        null
    }
}

private fun getTitle(notification: android.app.Notification): String? {
    val extras = notification.extras
    if (extras != null) {
        // android.text.SpannableString cannot be cast to java.lang.String
        val title: Any? = extras.get(android.app.Notification.EXTRA_TITLE)
        logger.p("title: $title")
        return title?.toString()
    }
    return null
}

fun getContent(notification: android.app.Notification): String? {
    val extras = notification.extras
    if (extras != null) {
        // java.lang.ClassCastException: android.text.SpannableString cannot be cast to java.lang.String
        val ext: Any? = extras.get(android.app.Notification.EXTRA_TEXT)
        logger.p("ext: $ext")
        return ext?.toString()
    }
    return null
}

private fun getNormalizedUserId(sbn: StatusBarNotification): Int {
    var userId = sbn.userId
    if (userId == UserHandle.USER_ALL) {
        userId = UserHandle.USER_SYSTEM
    }
    return userId
}