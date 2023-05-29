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
        val packageName = event.packageName
        val className = event.className
        if (type == UsageEvents.Event.MOVE_TO_BACKGROUND) {
            ShortXService.shortXContext.appState.onComponentMoveToBg(
                ComponentName(
                    packageName,
                    className
                ), userId
            )
        } else if (type == UsageEvents.Event.MOVE_TO_FOREGROUND) {
            ShortXService.shortXContext.appState.onComponentMoveToFg(
                ComponentName(
                    packageName,
                    className
                ), userId
            )
        }
    }
}