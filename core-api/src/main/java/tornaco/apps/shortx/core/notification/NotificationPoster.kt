package tornaco.apps.shortx.core.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Bundle
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.compat.NotificationCompat
import tornaco.apps.shortx.core.compat.NotificationCompat.VISIBILITY_PUBLIC
import tornaco.apps.shortx.core.compat.NotificationManagerCompat
import tornaco.apps.shortx.core.proto.action.NotificationButton
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import java.util.Objects

class NotificationPosterIntentHelper(clientName: String) {
    internal val broadcastActionNotification =
        "${clientName}.tornaco.apps.shortx.services.action.post.n"
    internal val broadcastExtraNotification =
        "${clientName}.tornaco.apps.shortx.services.action.post.n.extra.n"

    internal val broadcastActionButton =
        "${clientName}.tornaco.apps.shortx.services.action.post.n.button"
    internal val broadcastExtraButton =
        "${clientName}.tornaco.apps.shortx.services.action.post.n.extra.button"

    fun buttonIntent(
        context: Context,
        button: NotificationButton,
        notification: PostNotification,
    ): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            NotificationIdFactory.getNextId(),
            Intent(broadcastActionButton)
                .putExtra(broadcastExtraNotification, notification)
                .putExtra(broadcastExtraButton, button.id),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun notificationIntent(
        context: Context,
        notification: PostNotification
    ): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            NotificationIdFactory.getNextId(),
            Intent(broadcastActionNotification)
                .putExtra(broadcastExtraNotification, notification),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

class NotificationPoster(
    private val context: Context,
    private val clientName: String,
    private val silenceNotificationChannelLabel: String,
    private val importNotificationChannelLabel: String,
    private val overrideName: String = "ShortX",
    private val getIcon: (String) -> android.graphics.drawable.Icon?,
    private val getBitmap: (String) -> Bitmap?,
    // Button ID.
    private val onButtonClick: (PostNotification, String) -> Unit = { _, _ -> },
    private val onPostNotificationClick: (PostNotification) -> Unit = { },
) {
    private val logger by lazy {
        Logger("NotificationPoster-$clientName#${hashCode()}")
    }
    private val intentHelper = NotificationPosterIntentHelper(clientName)

    init {
        ContextCompat.registerReceiver(
            context,
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    logger.d("onReceive: $intent")
                    if (intent == null || intent.action == null) return
                    val action = intent.action
                    if (action == intentHelper.broadcastActionButton) {
                        logger.i("Received BROADCAST_ACTION_BUTTON")
                        val buttonId =
                            intent.getStringExtra(intentHelper.broadcastExtraButton)
                        logger.i("buttonId extra: $buttonId")
                        val notification =
                            intent.getSerializableExtra(intentHelper.broadcastExtraNotification) as PostNotification
                        logger.i("Notification extra: $notification")
                        onButtonClick(notification, buttonId)

                    } else if (action == intentHelper.broadcastActionNotification) {
                        logger.i("Received BROADCAST_ACTION_NOTIFICATION")
                        val notification =
                            intent.getSerializableExtra(intentHelper.broadcastExtraNotification) as PostNotification
                        logger.i("Notification extra: $notification")
                        onPostNotificationClick(notification)
                    }
                }
            },
            IntentFilter(intentHelper.broadcastActionButton).apply {
                addAction(intentHelper.broadcastActionNotification)
            },
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    operator fun invoke(
        notification: PostNotification
    ) {
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

            val style = NotificationCompat.BigTextStyle()
            style.bigText(message)
            style.setBigContentTitle(title)

            val curSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setVisibility(VISIBILITY_PUBLIC)
                .setPriority(if (isImportant) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(!onGoing)
                .apply {
                    notification.buttonList.forEach {
                        val pi = intentHelper.buttonIntent(
                            context = context,
                            button = it,
                            notification = notification
                        )
                        logger.d("addAction from Button: ${it.label} -> $pi")
                        addAction(android.R.drawable.ic_menu_manage, it.label, pi)
                    }
                }
                .setStyle(style)
                .setOngoing(onGoing)
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setExtras(Bundle().apply {
                    val preferredOverrideName =
                        overrideAppName.takeIf { it.isNotEmpty() } ?: overrideName
                    logger.d("preferredOverrideName: $preferredOverrideName")
                    SystemUI.overrideNotificationAppName(
                        context,
                        this,
                        preferredOverrideName
                    )
                    extrasList.forEach { putString(it.key, it.value) }
                })

            if (largeIcon.isNotEmpty()) {
                getBitmap(largeIcon)?.let {
                    builder.setLargeIcon(it)
                }
            }

            if (clickActionList.isNotEmpty()) {
                builder.setContentIntent(
                    intentHelper.notificationIntent(
                        context = context,
                        notification = notification
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

            if (onGoing) {
                n.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
            }

            if (smallIcon.isNotEmpty()) {
                getIcon(smallIcon)?.let {
                    n.smallIcon = it
                }
            }
            val id = NotificationIdFactory.getIdByTag(tag)
            logger.d("post: ${notification.tag} ${notification.smallIcon} id: $id")
            NotificationManagerCompat.from(context).notify(tag, id, n)
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