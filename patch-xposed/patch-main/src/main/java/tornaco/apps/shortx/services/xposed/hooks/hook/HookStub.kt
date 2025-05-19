package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.SystemUIHook

object HookStub {
    private val logger = Logger("ShortXHook.HookStub")

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("HookStub handleLoadPackage- ${lpparam.packageName} ${lpparam.processName}")
        require(isSupportedAndroidVersion)
        if (PackageNames.android == lpparam.packageName) {

        } else {

        }
    }

    fun onPackageLoaded() {

    }

}