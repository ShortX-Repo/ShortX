package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.usage.UsageEvents
import android.content.ComponentName
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.usageStatsServiceClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService

object UsageStatsServiceHook {
    private val logger = Logger("ShortXHook.UsageStatsServiceHook")

    @Deprecated("Use ActivityRecord hook instead, this can not cover STOP RESUME event for float windows.")
    fun hookUsageStats(lpparam: XC_LoadPackage.LoadPackageParam) {
        beforeMethod(lpparam.classLoader.usageStatsServiceClass(), Methods.reportEvent) { param ->
            val event = param.args[0] as UsageEvents.Event
            val userId = param.args[1] as Int
            val type = event.mEventType
            val packageName: String? = event.packageName
            val className: String? = event.className

            if (type == UsageEvents.Event.ACTIVITY_PAUSED) {
                logger.d("ACTIVITY_PAUSED: $packageName $className")
                ShortXService.shortXContext.appState.onActivityStopped(
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
}