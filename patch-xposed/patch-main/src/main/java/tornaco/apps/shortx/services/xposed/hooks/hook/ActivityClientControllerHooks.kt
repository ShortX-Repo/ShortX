package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.platform.Classes.activityClientControllerClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

object ActivityClientControllerHooks {
    private val logger = Logger("ShortXHook.ActivityClientControllerHooks")

    fun hookOnBackPressed(lpparam: ISystemServerLoaded.Param) {
        if (BuildPropInternal.APP_IS_DEBUG) {
            kotlin.runCatching {
                if (OsUtils.isTOrAbove()) {
                    beforeMethod(
                        lpparam.classLoader.activityClientControllerClass(),
                        "onBackPressed"
                    ) { param ->
                        val args = param.args.joinToString { it?.toString().orEmpty() }
                        logger.d("onBackPressed: $args")
                    }
                }
            }.onFailure {
                logger.e(it, "hookOnBackPressed")
            }
        }
    }
}