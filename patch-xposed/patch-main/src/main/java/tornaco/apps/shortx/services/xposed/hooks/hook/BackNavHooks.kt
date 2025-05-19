package tornaco.apps.shortx.services.xposed.hooks.hook

import android.os.Bundle
import tornaco.apps.shortx.core.platform.Classes.backNavControllerClass
import tornaco.apps.shortx.core.platform.Methods.onBackNavigationDone
import tornaco.apps.shortx.core.platform.Methods.startBackNavigation
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

object BackNavHooks {
    private val logger = Logger("ShortXHook.BackNavHooks")

    fun hookStartNav(lpparam: ISystemServerLoaded.Param) = kotlin.runCatching {
        if (OsUtils.isTOrAbove()) {
            beforeMethod(
                lpparam.classLoader.backNavControllerClass(), startBackNavigation
            ) { param ->
                val command = param.args.joinToString { it?.toString().orEmpty() }
                logger.d("BackNavigationController - startBackNavigation: $command")
                ShortXService.shortXContext.wm.onBackNavStart()
            }
        }
    }.onFailure {
        logger.e(it, "hookStartNav")
    }

    fun hookOnNavDone(lpparam: ISystemServerLoaded.Param) = kotlin.runCatching {
        if (OsUtils.isTOrAbove()) {
            beforeMethod(
                lpparam.classLoader.backNavControllerClass(), onBackNavigationDone
            ) { param ->
                val command = param.args.joinToString { it?.toString().orEmpty() }
                val key = "TriggerBack";
                val triggerBack = (param.args[0] as? Bundle)?.getBoolean(key, false) ?: false
                // Android 13 http://aospxref.com/android-13.0.0_r3/xref/frameworks/base/services/core/java/com/android/server/wm/BackNavigationController.java
                val type = Int.MIN_VALUE
                logger.d("BackNavigationController - onBackNavigationDone(triggerBack-$triggerBack type-$type): $command")
                ShortXService.shortXContext.wm.onBackNavDone(triggerBack, type)
            }
        }
    }.onFailure {
        logger.e(it, "hookOnNavDone")
    }
}