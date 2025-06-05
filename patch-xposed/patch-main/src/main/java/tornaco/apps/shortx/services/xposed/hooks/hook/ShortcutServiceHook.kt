package tornaco.apps.shortx.services.xposed.hooks.hook

import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ParceledListSlice
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutQueryWrapper
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.platform.Classes.launcherAppsServiceImplClass
import tornaco.apps.shortx.core.platform.Classes.shortcutServiceClass
import tornaco.apps.shortx.core.platform.Methods.requestPinItem
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.ReflectionsExt
import java.util.UUID

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

            kotlin.runCatching {
                val method = ReflectionsExt.findMethodWithMostArgs(
                    lpparam.classLoader.launcherAppsServiceImplClass(),
                    "getShortcuts"
                )
                logger.i("Method - getShortcuts: $method")
                // ParceledListSlice getShortcuts(@NonNull final String callingPackage,
                //                @NonNull final ShortcutQueryWrapper query, @NonNull final UserHandle targetUser)
                afterMethod(method) { param ->
                    param.logParamsOnDebug()

                    val query = param.args[1] as ShortcutQueryWrapper
                    logger.d("Query: ${query.activity}")
                    val activity = query.activity ?: ComponentName(
                        BuildPropInternal.APP_PACKAGE_NAME,
                        "Activity"
                    ).also {
                        logger.w("Query. NO ACTIVITY.")
                    }

                    @Suppress("UNCHECKED_CAST")
                    val resList = param.result as ParceledListSlice<ShortcutInfo>
                    val infos = resList.list
                    if (infos.isNotEmpty()) {
                        logger.d(
                            "shortcuts, ${
                                infos.joinToString(System.lineSeparator()) {
                                    it.toInsecureString()
                                }
                            }"
                        )
                    } else {
                        logger.d("No shortcuts.")
                    }


                    val ourInfo = newShortcutInfo(activity)
                    ourInfo.addFlags(ShortcutInfo.FLAG_DYNAMIC)
                    ourInfo.addFlags(ShortcutInfo.FLAG_IMMUTABLE)
                    ourInfo.setReturnedByServer()
                    logger.d("Add ${ourInfo.toInsecureString()}")


                    val newList = mutableListOf(ourInfo).apply {
                        addAll(infos)
                    }
                    val newRes = ParceledListSlice(newList)
                    param.result = newRes
                }
            }.onFailure {
                logger.e(it, "hook getShortcuts")
            }
        } else {
            logger.w("requestPinItem not supported.")
        }
    }

    fun newShortcutInfo(activity: ComponentName): ShortcutInfo {
        val intent = Intent().apply {
            component = ComponentName(
                BuildPropInternal.APP_PACKAGE_NAME,
                "tornaco.apps.shortx.ui.shortcut.ActionShortcutActivity"
            )
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_VIEW

        val ourInfo =
            ShortcutInfo.Builder(object :
                ContextWrapper(ShortXService.shortXContext.user.uiContext) {
                override fun getPackageName(): String {
                    return "com.android.messaging"
                }
            }, UUID.randomUUID().toString())
                .setShortLabel("ShortX")
                .setLongLabel("ShortX")
                //  .setIntent(intent)
                .setActivity(activity)
                .setRank(1)
                .build()

        return ourInfo
    }
}
