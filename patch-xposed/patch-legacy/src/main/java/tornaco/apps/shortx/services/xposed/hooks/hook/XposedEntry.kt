package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

@DoNotStrip
class XposedEntry : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedRuntime.init(LegacyXposedAdapter, systemServer = lpparam.packageName == PackageNames.android)
        val pkgParam = IPackageLoaded.Param(
            lpparam.packageName,
            lpparam.processName,
            lpparam.classLoader
        )
        if (lpparam.packageName == PackageNames.android) {
            HookStub2.systemServerLoaded(ISystemServerLoaded.Param(lpparam.classLoader))
        } else {
            HookStub2.packageLoaded(pkgParam)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        XposedRuntime.init(LegacyXposedAdapter)
        HookStub2.initZygote()
    }
}