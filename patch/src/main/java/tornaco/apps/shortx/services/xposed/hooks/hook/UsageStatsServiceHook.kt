package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.usage.UsageEvents
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.usageStatsServiceClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService

@Deprecated("UsageStats Screen event not work on SAMSUNG android 12?")
object UsageStatsServiceHook {
    private val logger = Logger("ShortXHook.UsageStatsServiceHook")

    fun hookUsageStats(lpparam: XC_LoadPackage.LoadPackageParam) {
        beforeMethod(lpparam.classLoader.usageStatsServiceClass(), Methods.reportEvent) { param ->
            val event = param.args[0] as UsageEvents.Event
            val type = event.mEventType
            logger.p("reportEvent: $type")
            if (type == UsageEvents.Event.SCREEN_INTERACTIVE) {
                logger.d("SCREEN_INTERACTIVE")
                ShortXService.shortXContext.user.onScreenOn()

            } else if (type == UsageEvents.Event.SCREEN_NON_INTERACTIVE) {
                logger.d("SCREEN_NON_INTERACTIVE")
                ShortXService.shortXContext.user.onScreenOff()
            }
        }
    }
}