package tornaco.apps.shortx.services.notification

import android.app.PendingIntent
import android.content.Context

import android.content.Intent
import android.service.notification.StatusBarNotification
import tornaco.apps.shortx.core.notification.getNormalizedUserId
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils


object VendorFunc {

    object ColorOS {
        private val logger = Logger("VendorFunc.ColorOS")

        private const val ACTION_NOTIFICATION_ACTION_CLICK =
            "oppo.intent.action.NOTIFICATION_ACTION_CLICK"
        private const val ENTRY_KEY = "entryKey"
        private const val PACKAGE_NAME = "packageName"
        private const val PENDING_INTENT = "pendingIntent"
        private const val UID = "uId"
        private const val USER_ID = "userId"

        val pipReplyText = listOf(
            "浮窗回复",
            "懸浮視窗回覆",
            "Reply in floating window",
        )

        fun colorOsPipReply(context: Context, sbn: StatusBarNotification) {
            runCatching {
                onDebugBuild {
                    logger.d("isColorOS?: ${OsUtils.isColorOS()}")
                }

                if (OsUtils.isROrAbove() && OsUtils.isColorOS()) {
                    logger.d("colorOsPipReply, ${sbn.packageName}")
                    var pendingIntent: PendingIntent? = sbn.notification.contentIntent
                    if (pendingIntent == null) {
                        pendingIntent = sbn.notification.fullScreenIntent
                    }
                    val intent = Intent(ACTION_NOTIFICATION_ACTION_CLICK)
                    intent.setPackage("com.android.systemui")
                    intent.putExtra(PENDING_INTENT, pendingIntent)
                    intent.putExtra(PACKAGE_NAME, sbn.packageName)
                    intent.putExtra(USER_ID, getNormalizedUserId(sbn))
                    intent.putExtra(UID, sbn.uid)
                    intent.putExtra(ENTRY_KEY, sbn.key)
                    val broadcast = PendingIntent.getBroadcast(
                        context,
                        System.currentTimeMillis().toInt(),
                        intent,
                        301989888
                    )
                    broadcast.send()
                    logger.d("colorOsPipReply complete")
                }
            }.onFailure {
                logger.e(it, "colorOsPipReply")
            }
        }
    }


}