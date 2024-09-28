package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.util.Logger

object SystemUIHook {
    internal val logger = Logger("ShortXHook.SystemUIHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (PackageNames.systemUI == lpparam.packageName) {
            hookApp(lpparam)
            hookTile(lpparam)
            hookQS(lpparam)
            hookView(lpparam)
            hookDarkIcon(lpparam)
        }
    }
}
