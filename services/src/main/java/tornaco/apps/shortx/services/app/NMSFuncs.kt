package tornaco.apps.shortx.services.app

import android.app.NotificationManager
import tornaco.apps.shortx.core.notification.androidNotificationRecordToNotification
import tornaco.apps.shortx.core.notification.androidSBNToNotification
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object NMSFuncs {
    private val logger = Logger("NMSFuncs")

    fun getActiveNotifications(nms: Any?): List<Notification> = kotlin.runCatching {
        // Will not call NotificationManager#getActiveNotifications
        // See: traces_SystemServer_WDT20_10_13_28_23.153_pid2154
        // https://github.com/Tornaco/Thanox/issues/609

        // final ArrayList<NotificationRecord> mNotificationList = new ArrayList();
        logger.d("getActiveNotifications, nms: $nms")

        if (nms != null) {
            @Suppress("UNCHECKED_CAST")
            val mNotificationList: ArrayList<Any?> =
                Reflections.getObjectField(nms, "mNotificationList") as ArrayList<Any?>
            mNotificationList.mapNotNull { nrOrNull ->
                nrOrNull?.androidNotificationRecordToNotification()
            }
        } else {
            logger.e("getActiveNotifications, nms is null, calling legacy API")
            NotificationManager.getService()
                .getActiveNotifications("android")
                .mapNotNull {
                    it?.androidSBNToNotification()
                }
        }
    }.getOrElse {
        logger.e(it, "getActiveNotifications error")
        emptyList()
    }
}