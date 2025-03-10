package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.SystemUIHook

object HookStub {
    private val logger = Logger("ShortXHook.HookStub")

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        logger.logOnDebug("handleLoadPackage- ${lpparam.packageName}")
        if (PackageNames.android == lpparam.packageName) {
            logger.i("Loading android.")
            androidHooks(lpparam)
            CustomHook.hookAndroidSystem(lpparam)
        } else {
            SystemUIHook.hook(lpparam)
            MediaProviderHook.hook(lpparam)
            NFCServiceHook.hook(lpparam)
            MMSHook.hook(lpparam)

            if (lpparam.packageName != PackageNames.systemUI) {
                CustomHook.hookApp(
                    lpparam.classLoader,
                    lpparam.packageName,
                    MethodHookLifecycle.LoadedPackage
                )
            }
        }
    }

    fun onPackageLoaded() {
        RuntimeInitHook.hookUncaughtException()
        ContextImplHook.onPackageLoaded()
        // ActivityClientHooks.hook()
        SystemUIHook.onPackageLoaded()
        CustomHook.hookApp(
            null,
            null,
            MethodHookLifecycle.InitZygote
        )
    }

    private fun androidHooks(lpparam: XC_LoadPackage.LoadPackageParam) {
        ShortXService.main()
        AMSHook.hookAMS(lpparam)
        AMSHook.hookAMSLifecycleStart(lpparam)
        AMSHook.hookAMSShellCommand(lpparam)
        BackNavHooks.hookStartNav(lpparam)
        BackNavHooks.hookOnNavDone(lpparam)
        InputManagerHook.hookInputManager(lpparam)
        InputManagerHook.debugHooks(lpparam)
        AppWidgetServiceHook.hookAppWidgetService(lpparam)
        NotificationUsageStatsHook.hookNotificationUsageStats(lpparam)
        ActivityTaskSupervisorHook.hookActivityTaskSupervisor(lpparam)
        ProcessListHook.hookProcessListRemoveProcess(lpparam)
        ProcessListHook.hookProcessListOnProcessStarted(lpparam)
        ActivityRecordHook.hook(lpparam)
        VPNHook.hook(lpparam)
        PhoneWindowManagerHook.hook(lpparam)
        ClipboardServiceHook.hookClipboardService(lpparam)
        ActivityStarterHook.hookEnqueueRequest(lpparam)
        ActiveServiceHook.hookUpdateScreenState(lpparam)
        DisplayRotationHook.hookOnProposedRotationChanged(lpparam)
        IMEServiceHook.hook(lpparam)
        WMServiceHook.hook(lpparam)
        DisplayPolicyHook.hook(lpparam)
        AccManagerHook.hook(lpparam)
        ActivityMetricsLoggerHook.hook(lpparam)
        ShortcutServiceHook.hook(lpparam)
        TaskOrganizerControllerHooks.hookHandleInterceptBackPressedOnTaskRoot(lpparam)
        ActivityClientControllerHooks.hookOnBackPressed(lpparam)
        StatusBarManagerServiceHook.hook(lpparam)
        ComputerEngineHook.hook(lpparam)
    }
}