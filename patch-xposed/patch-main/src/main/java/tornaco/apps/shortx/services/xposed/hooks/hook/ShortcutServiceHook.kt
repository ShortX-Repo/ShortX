package tornaco.apps.shortx.services.xposed.hooks.hook

import android.appwidget.AppWidgetProviderInfo
import android.content.pm.ShortcutInfo
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.platform.Classes.shortcutServiceClass
import tornaco.apps.shortx.core.platform.Methods.requestPinItem
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.ReflectionsExt

object ShortcutServiceHook {
    private val logger = Logger("ShortXHook.SS")

    fun hook(lpparam: ISystemServerLoaded.Param) {
        if (OsUtils.isQOrAbove()) {
            kotlin.runCatching {
                val method = ReflectionsExt.findMethodWithMostArgs(
                    lpparam.classLoader.shortcutServiceClass(),
                    requestPinItem
                )
                logger.i("Method - requestPinItem: $method")
                // private boolean requestPinItem(String callingPackage, int userId, ShortcutInfo shortcut,
                //            AppWidgetProviderInfo appWidget, Bundle extras, IntentSender resultIntent,
                //            int callingPid, int callingUid)
                afterMethod(method) { param ->
                    logger.logOnDebug("requestPinItem: ${param.args.map { it }}")

                    val shortcut = param.args[2] as ShortcutInfo?
                    val appWidget = param.args[3] as AppWidgetProviderInfo?

                    shortcut?.let { ShortXService.shortXContext.user.onRequestPinShortcut(it) }
                    appWidget?.let { ShortXService.shortXContext.user.onRequestPinAppWidget(it) }

                }
            }.onFailure {
                logger.e(it, "hook requestPinItem")
            }
        } else {
            logger.w("requestPinItem not supported.")
        }
    }

}
