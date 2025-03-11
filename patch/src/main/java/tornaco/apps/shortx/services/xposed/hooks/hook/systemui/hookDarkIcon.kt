package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.ShortXManagerNative
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
            // void setIconsDark(boolean dark, boolean animate)
            logger.d("setIconsDark: ${param.args.map { it }}")
            val isDark = param.args[0] as Boolean
            ShortXManagerNative.shortX?.isLightBarTransitionsIconsDark = isDark
        }
    }
}.onFailure {
    logger.e(it, "hookDarkIcon")
}