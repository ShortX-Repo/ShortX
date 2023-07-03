package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.annotations.DoNotStrip

@DoNotStrip
class XposedEntry : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        HookStub.handleLoadPackage(lpparam)
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
    }
}
