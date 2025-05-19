package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.Reflections

object DisplayRotationHook {
    private val logger = Logger("ShortXHook.DisplayRotationHook")

    fun hookOnProposedRotationChanged(lpparam: ISystemServerLoaded.Param) {
        if (OsUtils.isQOrAbove()) {
            kotlin.runCatching {
                afterMethod(
                    Reflections.findClass(
                        "com.android.server.wm.DisplayRotation\$OrientationListener",
                        lpparam.classLoader
                    ), "onProposedRotationChanged"
                ) {
                    logger.d("onProposedRotationChanged: ${it.args[0]}")
                    ShortXService.shortXContext.wm.onProposedRotationChanged((it.args[0] as Int))
                }
            }.onFailure {
                logger.e(it, "hookOnProposedRotationChanged")
            }
        } else {
            logger.w("hookOnProposedRotationChanged not supported.")
        }
    }

}
