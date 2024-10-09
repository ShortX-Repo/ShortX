package tornaco.apps.shortx.ui.service

import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import androidx.annotation.Keep
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.notification.handleButtonClick
import tornaco.apps.shortx.core.notification.handleNotificationClick
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.res.remixResId
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.BitmapUtils

@Keep
class AppNotificationService : Service() {
    private val shortx by lazy { shortXManager }

    val notificationPoster by lazy {
        NotificationPoster(
            applicationContext,
            "AppNotificationService",
            "ShortX silence",
            "ShortX alert",
            "ShortX",
            getIcon = {
                Icon.createWithResource(applicationContext, applicationContext.remixResId(it))
            },
            getBitmap = {
                BitmapUtils.getBitmap(applicationContext, applicationContext.remixResId(it))
            },
            onButtonClick = { postN, buttonId ->
                handleButtonClick(postN, buttonId, shortx)
            },
            onPostNotificationClick = { post ->
                handleNotificationClick(post, shortx)
            }
        )
    }

    private val stub = object : INotificationPoster.Stub() {
        override fun post(data: ByteArrayWrapper) {
            runCatching {
                val n = PostNotification.parseFrom(data.byteData)
                notificationPoster.invoke(n)
            }
        }

        override fun cancel(tag: String) {
            kotlin.runCatching {
                notificationPoster.cancelNotification(tag)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return stub
    }
}