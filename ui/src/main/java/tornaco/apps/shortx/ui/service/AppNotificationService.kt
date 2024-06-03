package tornaco.apps.shortx.ui.service

import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import androidx.annotation.Keep
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.ui.base.remixResId

@Keep
class AppNotificationService : Service() {
    private val shortx by lazy { shortXManager.service }

    val notificationPoster by lazy {
        NotificationPoster(
            applicationContext,
            "ShortX silence",
            "ShortX alert",
            "ShortX",
            getIcon = {
                Icon.createWithResource(applicationContext, applicationContext.remixResId(it))
            },
            getBitmap = {
                BitmapUtils.getBitmap(applicationContext, applicationContext.remixResId(it))
            },
            onButtonClick = { button ->
                button.actionList.forEach {
                    val evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId("Notification button")
                        .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
                        .setSrcTitle(button.label)
                        .build()
                    shortx.executeAction(
                        ByteArrayWrapper(evaluateContext.toByteArray()),
                        ByteArrayWrapper(it.toByteArray())
                    )
                }
            },
            onPostNotificationClick = { post ->
                post.clickActionList.forEach {
                    val evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId("Notification")
                        .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
                        .setSrcTitle(post.title)
                        .build()
                    shortx.executeAction(
                        ByteArrayWrapper(evaluateContext.toByteArray()),
                        ByteArrayWrapper(it.toByteArray())
                    )
                }
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