package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropExt
import tornaco.apps.shortx.core.platform.Classes.taskOrganizerControllerClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils

object TaskOrganizerControllerHooks {
    private val logger = Logger("ShortXHook.TaskOrganizerControllerHooks")

    fun hookHandleInterceptBackPressedOnTaskRoot(lpparam: XC_LoadPackage.LoadPackageParam) =
        BuildPropExt.onDebugBuild {
            kotlin.runCatching {
                if (OsUtils.isTOrAbove()) {
                    beforeMethod(
                        lpparam.classLoader.taskOrganizerControllerClass(),
                        "handleInterceptBackPressedOnTaskRoot"
                    ) { param ->
                        val args = param.args.joinToString { it?.toString().orEmpty() }
                        logger.d("handleInterceptBackPressedOnTaskRoot: $args")
                    }
                }
            }.onFailure {
                logger.e(it, "hookHandleInterceptBackPressedOnTaskRoot")
            }
        }
}