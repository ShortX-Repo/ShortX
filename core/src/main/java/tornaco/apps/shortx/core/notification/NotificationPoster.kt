package tornaco.apps.shortx.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.RingtoneManager
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.compat.NotificationCompat
import tornaco.apps.shortx.core.compat.NotificationCompat.VISIBILITY_PUBLIC
import tornaco.apps.shortx.core.compat.NotificationManagerCompat
import tornaco.apps.shortx.core.proto.action.NotificationButton
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import java.util.Objects

object NotificationPosterGlobal {
    const val BROADCAST_ACTION_NOTIFICATION = "tornaco.apps.shortx.services.action.post.n"
    const val BROADCAST_EXTRA__NOTIFICATION =
        "tornaco.apps.shortx.services.action.post.n.extra.n"


    const val BROADCAST_ACTION_BUTTON = "tornaco.apps.shortx.services.action.post.n.button"
    const val BROADCAST_EXTRA_BUTTON =
        "tornaco.apps.shortx.services.action.post.n.extra.button"

    fun buttonIntent(context: Context, button: NotificationButton): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            NotificationIdFactory.getNextId(),
            Intent(BROADCAST_ACTION_BUTTON)
                .putExtra(BROADCAST_EXTRA_BUTTON, button),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun notificationIntent(context: Context, notification: PostNotification): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            NotificationIdFactory.getNextId(),
            Intent(BROADCAST_ACTION_NOTIFICATION)
                .putExtra(BROADCAST_EXTRA__NOTIFICATION, notification),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

class NotificationPoster(
    private val context: Context,
    private val silenceNotificationChannelLabel: String,
    private val importNotificationChannelLabel: String,
    private val overrideName: String = "ShortX",
    private val getIcon: (String) -> android.graphics.drawable.Icon?,
    private val getBitmap: (String) -> Bitmap?,
    private val onButtonClick: (NotificationButton) -> Unit = {},
    private val onPostNotificationClick: (PostNotification) -> Unit = {},
) {
    private val logger by lazy {
        Logger("NotificationPoster#${hashCode()}")
    }

    init {
        ContextCompat.registerReceiver(
            context,
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    logger.d("onReceive: $intent")
                    if (intent == null || intent.action == null) return
                    val action = intent.action
                    if (action == NotificationPosterGlobal.BROADCAST_ACTION_BUTTON) {
                        logger.i("Received BROADCAST_ACTION_BUTTON")
                        val button =
                            intent.getSerializableExtra(NotificationPosterGlobal.BROADCAST_EXTRA_BUTTON) as NotificationButton
                        logger.i("Button extra: $button")
                        onButtonClick(button)

                    } else if (action == NotificationPosterGlobal.BROADCAST_ACTION_NOTIFICATION) {
                        logger.i("Received BROADCAST_ACTION_NOTIFICATION")
                        val notification =
                            intent.getSerializableExtra(NotificationPosterGlobal.BROADCAST_EXTRA__NOTIFICATION) as PostNotification
                        logger.i("Notification extra: $notification")
                        onPostNotificationClick(notification)
                    }
                }
            },
            IntentFilter(NotificationPosterGlobal.BROADCAST_ACTION_BUTTON).apply {
                addAction(NotificationPosterGlobal.BROADCAST_ACTION_NOTIFICATION)
            },
            ContextCompat.RECEIVER_EXPORTED
        )
    }

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

            val preferredOverrideName = notification.overrideAppName.takeIf { it.isNotEmpty() } ?: overrideName
            logger.d("preferredOverrideName: $preferredOverrideName")
            kotlin.runCatching {
                SystemUI.overrideNotificationAppName(
                    context,
                    builder,
                    preferredOverrideName
                )
            }

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
                .apply {
                    notification.buttonList.forEach {
                        val pi = NotificationPosterGlobal.buttonIntent(context, it)
                        logger.d("addAction from Button: ${it.label} -> $pi")
                        addAction(android.R.drawable.ic_menu_manage, it.label, pi)
                    }
                }
                .setStyle(style)

            if (largeIcon.isNotEmpty()) {
                getBitmap(largeIcon)?.let {
                    builder.setLargeIcon(it)
                }
            }

            if (notification.clickActionList.isNotEmpty()) {
                builder.setContentIntent(
                    NotificationPosterGlobal.notificationIntent(
                        context,
                        notification
                    )
                )
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

            cancelNotification(tag)
            NotificationManagerCompat.from(context)
                .notify(tag, NotificationIdFactory.getIdByTag(tag), n)
        }
    }

    fun cancelNotification(notificationTag: String) {
        NotificationManagerCompat.from(context)
            .cancel(notificationTag, NotificationIdFactory.getIdByTag(notificationTag))
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