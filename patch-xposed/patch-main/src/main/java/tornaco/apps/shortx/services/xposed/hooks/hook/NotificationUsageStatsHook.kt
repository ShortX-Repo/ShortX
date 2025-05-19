package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.notification.androidNotificationRecordToNotification
import tornaco.apps.shortx.core.platform.Classes.notificationUsageStatsClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

object NotificationUsageStatsHook {
    private val logger = Logger("ShortXHook.NotificationUsageStatsHook")

    fun hookNotificationUsageStats(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            beforeMethod(
                lpparam.classLoader.notificationUsageStatsClass(),
                Methods.registerPostedByApp
            ) { methodHookParam ->
                logger.d("registerPostedByApp")
                methodHookParam.args[0]?.androidNotificationRecordToNotification()?.let {
                    ShortXService.shortXContext.notification.registerPostedByApp(it)
                }
            }

            beforeMethod(
                lpparam.classLoader.notificationUsageStatsClass(),
                Methods.registerUpdatedByApp
            ) { methodHookParam ->
                logger.d("registerUpdatedByApp")
                methodHookParam.args[0]?.androidNotificationRecordToNotification()?.let {
                    ShortXService.shortXContext.notification.registerUpdatedByApp(it)
                }
            }

            beforeMethod(
                lpparam.classLoader.notificationUsageStatsClass(),
                Methods.registerRemovedByApp
            ) { methodHookParam ->
                logger.d("registerRemovedByApp")
                methodHookParam.args[0]?.androidNotificationRecordToNotification()?.let {
                    ShortXService.shortXContext.notification.registerRemovedByApp(it)
                }
            }

            beforeMethod(
                lpparam.classLoader.notificationUsageStatsClass(),
                Methods.registerDismissedByUser
            ) { methodHookParam ->
                logger.d("registerDismissedByUser")
                methodHookParam.args[0]?.androidNotificationRecordToNotification()?.let {
                    ShortXService.shortXContext.notification.registerDismissedByUser(it)
                }
            }
        }.onFailure {
            logger.e(it, "hookNotificationUsageStats")
        }
}
