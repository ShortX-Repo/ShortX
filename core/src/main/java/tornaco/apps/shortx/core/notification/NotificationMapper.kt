package tornaco.apps.shortx.core.notification

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
        sbn.androidSBNToNotification()
    }.getOrElse {
        logger.e(it, "androidNotificationRecordToNotification error")
        null
    }
}


fun StatusBarNotification.androidSBNToNotification(): Notification? {
    return kotlin.runCatching {
        val sbn = this
        val n = sbn.notification ?: return null
        val isFgService = kotlin.runCatching { n.isForegroundService }.getOrElse { false }
        Notification.newBuilder()
            .setTitle(getTitle(n) ?: "")
            .setContentText(getContent(n) ?: "")
            .setTag(getTag(sbn) ?: "")
            .setKey(sbn.key ?: "")
            .setNotificationId(getId(sbn)?.toString() ?: "")
            .setIsFgService(isFgService)
            .addApps(
                AppPkg.newBuilder()
                    .setUserId(getNormalizedUserId(sbn))
                    .setPkgName(sbn.packageName)
                    .build()
            )
            .apply {
                if (OsUtils.isOOrAbove()) {
                    notificationChannel = n.channelId ?: "Unknown"
                }
            }
            .build()

    }.getOrElse {
        logger.e(it, "androidNotificationRecordToNotification error")
        null
    }
}

fun getTitle(notification: android.app.Notification): String? {
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
        logger.p("content: $ext")
        return ext?.toString()
    }
    return null
}

fun getTag(sbn: StatusBarNotification): String? {
    return runCatching { sbn.tag }.getOrElse {
        logger.w(it)
        null
    }.also {
        logger.p("tag: $it")
    }
}

fun getId(sbn: StatusBarNotification): Int? {
    return runCatching { sbn.id }.getOrElse {
        logger.w(it)
        null
    }.also {
        logger.p("id: $it")
    }
}

fun getNormalizedUserId(sbn: StatusBarNotification): Int {
    var userId = sbn.userId
    if (userId == UserHandle.USER_ALL) {
        userId = UserHandle.USER_SYSTEM
    }
    return userId
}