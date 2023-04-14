package tornaco.apps.shortx.ui.demo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import github.tornaco.shortx.android.icon.Remix
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.util.BitmapUtils
import java.util.*

@SuppressLint("MissingPermission")
fun mockNotification(context: Context, title: String?, content: String?, `when`: Long) {
    createNotificationChannelIfNeed(context)
    val n = NotificationCompat.Builder(context, "ShortX-Mock")
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setWhen(`when`)
        .setStyle(NotificationCompat.BigTextStyle().bigText(content))
        .setFullScreenIntent(
            PendingIntent.getBroadcast(
                context,
                System.currentTimeMillis().toString().hashCode(),
                Intent("shortx.action.mock.notification"),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ),
            true
        ).build()
    NotificationManagerCompat.from(context)
        .notify(21, n)
}

private fun createNotificationChannelIfNeed(context: Context) {
    if (NotificationManagerCompat.from(context).getNotificationChannel("ShortX-Mock") != null) {
        return
    }
    NotificationManagerCompat.from(context)
        .createNotificationChannel(
            NotificationChannel(
                "ShortX-Mock",
                "ShortX-Mock",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
}

@Composable
fun NotificationDemo() {
    val context = LocalContext.current
    FilledTonalButton(onClick = {
        mockNotification(
            context,
            "Demo title",
            "Demo content-" + Date(),
            System.currentTimeMillis()
        )
    }) {
        Text(text = "Notification & Update")
    }

    FilledTonalButton(onClick = {
        NotificationPoster(
            context,
            "silence",
            "important",
            null,
            getIcon = {
                Icon.createWithResource(context, Remix.allFlat[it]!!)
            },
            getBitmap = {
                BitmapUtils.getBitmap(context, Remix.allFlat[it]!!)!!
            }
        ).invoke(
            PostNotification.newBuilder()
                .setTag("demo")
                .setSmallIcon(Remix.Communication.chat_smile_line)
                .setLargeIcon(Remix.Logos.bard_line)
                .setTitle("This is title")
                .setMessage("This is message")
                .build()
        )
    }) {
        Text(text = "Poster")
    }
}