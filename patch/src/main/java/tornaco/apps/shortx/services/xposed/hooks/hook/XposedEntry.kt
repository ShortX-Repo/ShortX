package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.PackageNames.android
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService

class XposedEntry : IXposedHookLoadPackage, IXposedHookZygoteInit {
    internal val logger = Logger("XposedEntry")

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (android == lpparam.packageName) {
            ShortXService.main()
            logger.i("Loading android.")
            hookAMS(lpparam)
            hookInputManager(lpparam)
            hookUsageStats(lpparam)
            hookAppWidgetService(lpparam)
            hookNotificationUsageStats(lpparam)
            hookActivityTaskSupervisor(lpparam)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        // Noop.
    }
}
