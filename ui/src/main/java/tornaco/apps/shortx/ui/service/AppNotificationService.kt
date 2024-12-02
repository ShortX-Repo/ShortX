package tornaco.apps.shortx.ui.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
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
import tornaco.apps.shortx.core.util.Logger

@Keep
class AppNotificationService : Service() {
    companion object {
        private val shortx by lazy { shortXManager }
        private val logger = Logger("AppNotificationService")

        @SuppressLint("StaticFieldLeak")
        var notificationPoster: NotificationPoster? = null

        @Synchronized
        fun initPoster(context: Context) {
            if (notificationPoster == null) {
                logger.w("Init notificationPoster")
                notificationPoster = NotificationPoster(
                    context,
                    "AppNotificationService",
                    "ShortX silence",
                    "ShortX alert",
                    "ShortX",
                    getIcon = {
                        Icon.createWithResource(context, context.remixResId(it))
                    },
                    getBitmap = {
                        BitmapUtils.getBitmap(context, context.remixResId(it))
                    },
                    onButtonClick = { postN, buttonId ->
                        handleButtonClick(postN, buttonId, shortx)
                    },
                    onPostNotificationClick = { post ->
                        handleNotificationClick(post, shortx)
                    }
                )
            }
        }
    }

    private val stub = object : INotificationPoster.Stub() {
        override fun post(data: ByteArrayWrapper) {
            runCatching {
                initPoster(applicationContext)
                val n = PostNotification.parseFrom(data.byteData)
                requireNotNull(notificationPoster) { "notificationPoster is null." }.invoke(n)
            }
        }

        override fun cancel(tag: String) {
            kotlin.runCatching {
                initPoster(applicationContext)
                requireNotNull(notificationPoster) { "notificationPoster is null." }.cancelNotification(
                    tag
                )
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return stub
    }
}