package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.core.platform.Classes.notificationUsageStatsClass

fun XposedEntry.hookNotificationUsageStats(lpparam: XC_LoadPackage.LoadPackageParam) {
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
}