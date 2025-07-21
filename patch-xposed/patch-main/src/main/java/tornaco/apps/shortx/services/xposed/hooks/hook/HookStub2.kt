package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.SystemUIHook

object HookStub2 {
    fun systemServerLoaded(param: ISystemServerLoaded.Param) {
        require(isSupportedAndroidVersion)

        androidHooks(param)
        CustomHookSystemServer.hookAndroidSystem(param)
    }

    fun packageLoaded(param: IPackageLoaded.Param) {
        require(isSupportedAndroidVersion)

        SystemUIHook.handleLoadPackage(param)
        MediaProviderHook.hook(param)
        NFCServiceHook.hook(param)
        MMSHook.hook(param)

        if (param.packageName != PackageNames.systemUI) {
            CustomHookApp.hookApp(
                param.classLoader,
                param.packageName,
                MethodHookLifecycle.LoadedPackage
            )
        }

    }

    fun initZygote() {
        // OLD: Init zygote.
        RuntimeInitHook.hookUncaughtException()
        ContextImplHook.onPackageLoaded()
        // ActivityClientHooks.hook()
        SystemUIHook.onPackageLoaded()
        CustomHookApp.hookApp(
            null,
            null,
            MethodHookLifecycle.InitZygote
        )
        InputConnHook.onPackageLoaded()
        RenderNodeHook.onPackageLoaded()
    }

    private fun androidHooks(param: ISystemServerLoaded.Param) {
        ShortXService.main()
        AMSHook.hookAMS(param)
        AMSHook.hookAMSLifecycleStart(param)
        AMSHook.hookAMSShellCommand(param)
        BackNavHooks.hookStartNav(param)
        BackNavHooks.hookOnNavDone(param)
        InputManagerHook.hookInputManager(param)
        AppWidgetServiceHook.hookAppWidgetService(param)
        NotificationUsageStatsHook.hookNotificationUsageStats(param)
        ActivityTaskSupervisorHook.hookActivityTaskSupervisor(param)
        ProcessListHook.hookProcessListRemoveProcess(param)
        ProcessListHook.hookProcessListOnProcessStarted(param)
        ActivityRecordHook.hook(param)
        VPNHook.hook(param)
        ClipboardServiceHook.hookClipboardService(param)
        ActivityStarterHook.hookEnqueueRequest(param)
        ActiveServiceHook.hookUpdateScreenState(param)
        DisplayRotationHook.hookOnProposedRotationChanged(param)
        IMEServiceHook.hook(param)
        WMServiceHook.hook(param)
        DisplayPolicyHook.hook(param)
        AccManagerHook.hook(param)
        ActivityMetricsLoggerHook.hook(param)
        ShortcutServiceHook.hook(param)
        TaskOrganizerControllerHooks.hookHandleInterceptBackPressedOnTaskRoot(param)
        ActivityClientControllerHooks.hookOnBackPressed(param)
        StatusBarManagerServiceHook.hook(param)
        ComputerEngineHook.hook(param)
    }
}