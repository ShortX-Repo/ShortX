package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.xposed.hooks.hook.afterConstruct
import util.Reflections

fun SystemUIHook.hookQS(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
    if (PackageNames.systemUI == lpparam.packageName) {
        afterConstruct(
            clazz = Reflections.findClass(
                "com.android.systemui.qs.QSTileHost",
                lpparam.classLoader
            ),
        ) { param ->
            logger.d("afterConstruct.QSTileHost")
            QSHostAdapter.qsTileHost = param.thisObject
        }

        // Android 15.
        if (OsUtils.isVOrAbove()) {
            kotlin.runCatching {
                afterConstruct(
                    clazz = Reflections.findClass(
                        "com.android.systemui.qs.QSHostAdapter",
                        lpparam.classLoader
                    ),
                ) { param ->
                    logger.d("QSHostAdapter init: ${param.thisObject}")
                    QSHostAdapter._QSHostAdapter = param.thisObject
                }
            }
        }
    }
}.onFailure {
    logger.e(it, "hookTile")
}