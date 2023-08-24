package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService

object HookStub {
    private val logger = Logger("ShortXHook.HookStub")

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (PackageNames.android == lpparam.packageName) {
            logger.i("Loading android.")
            androidHooks(lpparam)
        } else {
            SystemUIHook.hookApp(lpparam)
            SystemUIHook.hookTile(lpparam)
            MediaProviderHook.hook(lpparam)
        }
    }

    fun onPackageLoaded() {
        RuntimeInitHook.hookUncaughtException()
        ContextImplHook.onPackageLoaded()
    }

    private fun androidHooks(lpparam: XC_LoadPackage.LoadPackageParam) {
        ShortXService.main()
        AMSHook.hookAMS(lpparam)
        InputManagerHook.hookInputManager(lpparam)
        AppWidgetServiceHook.hookAppWidgetService(lpparam)
        NotificationUsageStatsHook.hookNotificationUsageStats(lpparam)
        ActivityTaskSupervisorHook.hookActivityTaskSupervisor(lpparam)
        ProcessListHook.hookProcessList(lpparam)
        ActivityRecordHook.hook(lpparam)
        VPNHook.hook(lpparam)
        PhoneWindowManagerHook.hook(lpparam)
        ClipboardServiceHook.hookClipboardService(lpparam)
        ActivityStarterHook.hookEnqueueRequest(lpparam)
    }
}