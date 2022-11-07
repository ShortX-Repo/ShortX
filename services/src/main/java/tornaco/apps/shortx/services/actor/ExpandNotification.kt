package tornaco.apps.shortx.services.actor

import android.content.Context
import tornaco.apps.shortx.core.util.statusBarManager

class ExpandNotification(private val context: Context) {
    operator fun invoke() {
        context.statusBarManager.expandNotificationsPanel()
    }
}