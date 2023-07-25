package tornaco.apps.shortx.ui.demo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.ui.base.remixResId
import java.util.*

@SuppressLint("MissingPermission")
fun mockNotification(
    context: Context,
    title: String?,
    content: String?,
    `when`: Long,
    id: Int = 22
) {
    createNotificationChannelIfNeed(context)
    val n = NotificationCompat.Builder(context, "ShortX-Mock")
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(github.tornaco.shortx.icons.R.drawable.ic_remix_account_pin_box_fill)
        .setWhen(`when`)
        .setStyle(NotificationCompat.BigTextStyle().bigText(content))
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toString().hashCode(),
                context.packageManager.getLaunchIntentForPackage(BuildProp.APP_PACKAGE_NAME),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ),
        )
        .setFullScreenIntent(
            PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toString().hashCode(),
                context.packageManager.getLaunchIntentForPackage(BuildProp.APP_PACKAGE_NAME),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ),
            true
        )
        .setAutoCancel(true)
        .build()
    NotificationManagerCompat.from(context)
        .notify("demo-tag", id, n)
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

    var id by remember {
        mutableStateOf(100)
    }
    FilledTonalButton(onClick = {
        mockNotification(
            context,
            "Demo title",
            "Demo content",
            System.currentTimeMillis(),
            id = id
        )
        id += 1
    }) {
        Text(text = "Notification Post(diff ID.)")
    }

    FilledTonalButton(onClick = {
        mockNotification(
            context,
            "Demo title",
            "Demo content",
            System.currentTimeMillis(),
            id = 11
        )
        mockNotification(
            context,
            "Demo title",
            "Demo content",
            System.currentTimeMillis(),
            id = 12
        )
    }) {
        Text(text = "Notification Dup")
    }

    FilledTonalButton(onClick = {
        NotificationPoster(
            context,
            "silence",
            "important",
            null,
            getIcon = {
                Icon.createWithResource(context, context.remixResId(it))
            },
            getBitmap = {
                BitmapUtils.getBitmap(context, context.remixResId(it))!!
            }
        ).invoke(
            notification = PostNotification.newBuilder()
                .setTag("demo")
                .setSmallIcon(Remix.Communication.chat_smile_line)
                .setLargeIcon(Remix.Logos.bard_line)
                .setTitle("This is title")
                .setMessage("This is message")
                .build(),
        )
    }) {
        Text(text = "Poster")
    }
}