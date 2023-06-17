package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.usage.UsageEvents
import android.content.ComponentName
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.usageStatsServiceClass

fun XposedEntry.hookUsageStats(lpparam: XC_LoadPackage.LoadPackageParam) {
    beforeMethod(lpparam.classLoader.usageStatsServiceClass(), Methods.reportEvent) { param ->
        val event = param.args[0] as UsageEvents.Event
        val userId = param.args[1] as Int
        val type = event.mEventType
        val packageName: String? = event.packageName
        val className: String? = event.className

        // logger.d("reportEvent: $type $packageName $className")

        if (type == UsageEvents.Event.ACTIVITY_PAUSED) {
            logger.d("ACTIVITY_PAUSED: $packageName $className")
            ShortXService.shortXContext.appState.onActivityPaused(
                ComponentName(
                    packageName,
                    className
                ), userId
            )
        } else if (type == UsageEvents.Event.ACTIVITY_RESUMED) {
            logger.d("ACTIVITY_RESUMED: $packageName $className")
            ShortXService.shortXContext.appState.onActivityResumed(
                ComponentName(
                    packageName,
                    className
                ), userId
            )
        }
    }
}