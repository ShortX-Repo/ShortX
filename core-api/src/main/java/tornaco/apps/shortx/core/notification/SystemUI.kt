package tornaco.apps.shortx.core.notification

import android.app.Notification
import android.content.Context
import android.os.Bundle
import tornaco.apps.shortx.core.compat.NotificationCompat

object SystemUI {

    @JvmStatic
    fun overrideNotificationAppName(context: Context, n: Notification.Builder, name: String?) {
        val extras = Bundle()
        extras.putString(
            Notification.EXTRA_SUBSTITUTE_APP_NAME,
            name ?: context.getString(com.android.internal.R.string.android_system_label)
        )
        n.addExtras(extras)
    }

    @JvmStatic
    fun overrideNotificationAppName(
        context: Context,
        n: NotificationCompat.Builder,
        name: String?
    ) {
        val extras = Bundle()
        extras.putString(
            Notification.EXTRA_SUBSTITUTE_APP_NAME,
            name ?: context.getString(com.android.internal.R.string.android_system_label)
        )
        n.addExtras(extras)
    }

    fun overrideNotificationAppName(
        context: Context,
        extras: Bundle,
        name: String?
    ) {
        extras.putString(
            Notification.EXTRA_SUBSTITUTE_APP_NAME,
            name ?: context.getString(com.android.internal.R.string.android_system_label)
        )
    }
}