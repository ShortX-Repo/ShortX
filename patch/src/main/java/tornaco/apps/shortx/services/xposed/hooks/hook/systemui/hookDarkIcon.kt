package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import util.Reflections

fun SystemUIHook.hookDarkIcon(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
    if (PackageNames.systemUI == lpparam.packageName) {
        afterMethod(
            clazz = Reflections.findClass(
                "com.android.systemui.statusbar.phone.LightBarTransitionsController",
                lpparam.classLoader
            ),
            methodName = "setIconsDark"
        ) { param ->
            logger.d("setIconsDark: ${param.args.map { it }}")
        }
    }
}.onFailure {
    logger.e(it, "hookDarkIcon")
}