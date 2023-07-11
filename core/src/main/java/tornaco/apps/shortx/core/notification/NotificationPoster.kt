package tornaco.apps.shortx.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import tornaco.apps.shortx.core.compat.NotificationCompat
import tornaco.apps.shortx.core.compat.NotificationCompat.VISIBILITY_PUBLIC
import tornaco.apps.shortx.core.compat.NotificationManagerCompat
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import java.util.Objects

class NotificationPoster(
    private val context: Context,
    private val silenceNotificationChannelLabel: String,
    private val importNotificationChannelLabel: String,
    private val overrideName: String? = null,
    private val getIcon: (String) -> android.graphics.drawable.Icon?,
    private val getBitmap: (String) -> Bitmap?,
) {
    private val logger = Logger("NotificationPoster")

    operator fun invoke(
        notification: PostNotification
    ) {
        logger.d("post: ${notification.tag} ${notification.smallIcon}")
        with(notification) {
            val importantChannel = createImportantNotificationChannel(context)
            val silenceChannel = createSilenceNotificationChannel(context)

            val builder = NotificationCompat.Builder(
                context,
                if (isImportant) {
                    importantChannel
                } else {
                    silenceChannel
                }
            )

            val pendingIntent: PendingIntent? = pkgToLaunchOnClick?.let {
                val launchIntent: Intent? =
                    context.packageManager.getLaunchIntentForPackage(it.pkgName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val nId =
                        NotificationIdFactory.getNextId()
                    PendingIntent.getActivity(
                        context,
                        nId,
                        launchIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    logger.w("showNotification, launch intent is null for pkg: $it")
                    null
                }
            }

            SystemUI.overrideNotificationAppName(
                context, builder,
                overrideName
            )

            val style = NotificationCompat.BigTextStyle()
            style.bigText(notification.message)
            style.setBigContentTitle(notification.title)

            val curSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            builder.setContentTitle(notification.title)
                .setContentText(notification.message)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setVisibility(VISIBILITY_PUBLIC)
                .setPriority(if (isImportant) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setStyle(style)

            if (largeIcon.isNotEmpty()) {
                getBitmap(largeIcon)?.let {
                    builder.setLargeIcon(it)
                }
            }

            pendingIntent?.let {
                builder.setContentIntent(it)
            }

            if (sound) {
                builder.setSound(curSoundUri)
            } else {
                builder.setSound(null)
            }

            if (vibrate) {
                builder.setVibrate(longArrayOf(200L, 200L, 100L, 100L))
            }

            val n = builder.build()

            if (smallIcon.isNotEmpty()) {
                getIcon(smallIcon)?.let {
                    n.smallIcon = it
                }
            }

            overrideName?.let {
                kotlin.runCatching {
                    SystemUI.overrideNotificationAppName(
                        context,
                        builder,
                        it
                    )
                }
            }

            cancelNotification(tag)
            NotificationManagerCompat.from(context)
                .notify(NotificationIdFactory.getIdByTag(tag), n)
        }
    }

    fun cancelNotification(notificationTag: String) {
        NotificationManagerCompat.from(context)
            .cancel(NotificationIdFactory.getIdByTag(notificationTag))
    }

    private fun createSilenceNotificationChannel(context: Context): String {
        val channelId = "ShortX.silence"
        if (OsUtils.isOOrAbove()) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val nc =
                notificationManager.getNotificationChannel(channelId)
            if (nc != null) {
                return channelId
            }
            val notificationChannel = NotificationChannel(
                channelId,
                silenceNotificationChannelLabel,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            Objects.requireNonNull(notificationManager)
                .createNotificationChannel(notificationChannel)
        }
        return channelId
    }

    private fun createImportantNotificationChannel(context: Context): String {
        val channelId = "ShortX.important"
        if (OsUtils.isOOrAbove()) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val nc =
                notificationManager.getNotificationChannel(channelId)
            if (nc != null) {
                return channelId
            }
            val notificationChannel = NotificationChannel(
                channelId,
                importNotificationChannelLabel,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            Objects.requireNonNull(notificationManager)
                .createNotificationChannel(notificationChannel)
        }
        return channelId
    }

}