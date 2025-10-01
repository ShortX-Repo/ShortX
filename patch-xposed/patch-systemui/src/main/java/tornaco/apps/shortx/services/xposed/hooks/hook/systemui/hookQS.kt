package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.xposed.hooks.hook.afterConstruct
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import tornaco.apps.shortx.services.xposed.hooks.hook.logParamsOnDebug
import util.Reflections

fun SystemUIHook.hookQS(lpparam: IPackageLoaded.Param) = kotlin.runCatching {
    if (PackageNames.systemUI == lpparam.packageName) {
        runCatching {
            afterConstruct(
                clazz = Reflections.findClass(
                    "com.android.systemui.qs.QSTileHost",
                    lpparam.classLoader
                ),
            ) { param ->
                logger.w("afterConstruct.QSTileHost")
                QSHostAdapter.qsTileHost = param.thisObject
            }
        }.onFailure {
            logger.e("afterConstruct.QSTileHost error, maybe 15+?.")
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
            kotlin.runCatching {
                afterConstruct(
                    clazz = Reflections.findClass(
                        "com.android.systemui.qs.pipeline.domain.adapter.MiuiQSHostAdapter",
                        lpparam.classLoader
                    ),
                ) { param ->
                    logger.d("MiuiQSHostAdapter init: ${param.thisObject}")
                    QSHostAdapter._QSHostAdapter = param.thisObject
                }
            }

            if (BuildPropInternal.APP_IS_DEBUG) runCatching {
                afterMethod(
                    Reflections.findClass(
                        "com.android.systemui.qs.tileimpl.QSTileImpl",
                        lpparam.classLoader
                    ),
                    "refreshState"
                ) {
                    it.logParamsOnDebug(logger, true)
                }
            }
        }
    }
}.onFailure {
    logger.e(it, "hookTile")
}