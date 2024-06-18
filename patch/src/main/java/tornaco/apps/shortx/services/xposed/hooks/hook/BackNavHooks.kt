package tornaco.apps.shortx.services.xposed.hooks.hook

import android.os.Bundle
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.backNavControllerClass
import tornaco.apps.shortx.core.platform.Methods.onBackNavigationDone
import tornaco.apps.shortx.core.platform.Methods.startBackNavigation
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils

object BackNavHooks {
    private val logger = Logger("ShortXHook.BackNavHooks")

    fun hookStartNav(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (OsUtils.isTOrAbove()) {
            beforeMethod(
                lpparam.classLoader.backNavControllerClass(), startBackNavigation
            ) { param ->
                val command = param.args.joinToString { it?.toString().orEmpty() }
                logger.d("BackNavigationController - startBackNavigation: $command")
            }
        }
    }.onFailure {
        logger.e(it, "hookStartNav")
    }

    fun hookOnNavDone(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (OsUtils.isTOrAbove()) {
            beforeMethod(
                lpparam.classLoader.backNavControllerClass(), onBackNavigationDone
            ) { param ->
                val command = param.args.joinToString { it?.toString().orEmpty() }
                val key = "TriggerBack";
                val triggerBack = (param.args[0] as? Bundle)?.getBoolean(key, false) ?: false
                logger.d("BackNavigationController - onBackNavigationDone(triggerBack-$triggerBack): $command")
            }
        }
    }.onFailure {
        logger.e(it, "hookOnNavDone")
    }
}