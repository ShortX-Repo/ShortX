package tornaco.apps.shortx.services.notification

import android.annotation.WorkerThread
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import tornaco.apps.shortx.core.notification.androidSBNToNotification
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

class Nls(val shortXContext: ShortXContext) : NotificationListenerService() {
    private val logger = Logger("Nls")
    private var isConnected = false

    @WorkerThread
    fun waitForBind(): Boolean {
        for (i in 0..19) {
            if (isConnected) {
                logger.d("Bound Shell NLS")
                return true
            } else {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    logger.e(e, "waitForBind")
                }
            }
        }
        return false
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        isConnected = true
        logger.w("onListenerConnected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isConnected = false
        logger.w("onListenerDisconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        logger.p("onNotificationPosted: ${sbn?.androidSBNToNotification()}")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        logger.p("onNotificationRemoved: ${sbn?.androidSBNToNotification()}")
    }
}