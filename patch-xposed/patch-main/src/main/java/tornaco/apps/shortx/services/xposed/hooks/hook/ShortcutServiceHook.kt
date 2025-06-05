package tornaco.apps.shortx.services.xposed.hooks.hook

import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ParceledListSlice
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutQueryWrapper
import android.graphics.drawable.Icon
import tornaco.apps.shortx.core.AppGlobals
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.platform.Classes.launcherAppsServiceImplClass
import tornaco.apps.shortx.core.platform.Classes.shortcutServiceClass
import tornaco.apps.shortx.core.platform.Classes.shortcutServiceLocalServiceClass
import tornaco.apps.shortx.core.platform.Methods.requestPinItem
import tornaco.apps.shortx.core.proto.common.AppCategory
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.ReflectionsExt
import java.util.UUID


object ShortcutServiceHook {
    private val logger = Logger("ShortXHook.SS")

    fun hook(lpparam: ISystemServerLoaded.Param) {
        hookPinItem(lpparam)
        @Suppress("KotlinConstantConditions")
        if (BuildPropInternal.APP_IS_DEBUG) {
            hookLocalService(lpparam)
            hookLauncherApps(lpparam)
            hookStartShortcut(lpparam)
        }
    }

    private fun hookPinItem(lpparam: ISystemServerLoaded.Param) {
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

    private fun hookLauncherApps(lpparam: ISystemServerLoaded.Param) {
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
                val activity = query.activity ?: return@afterMethod

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
                val ourInfo = newShortcutInfo(activity, activity.packageName)
                val newList = mutableListOf(ourInfo).apply {
                    addAll(infos)
                }
                val newRes = ParceledListSlice(newList)
                param.result = newRes
                logger.d("Add: $ourInfo to activity query.")
            }
        }.onFailure {
            logger.e(it, "hook getShortcuts")
        }
    }

    private fun hookLocalService(lpparam: ISystemServerLoaded.Param) {
        kotlin.runCatching {
            //  @Override
            //        public List<ShortcutInfo> getShortcuts(int launcherUserId,
            //                @NonNull String callingPackage, long changedSince,
            //                @Nullable String packageName, @Nullable List<String> shortcutIds,
            //                @Nullable List<LocusId> locusIds, @Nullable ComponentName componentName,
            //                int queryFlags, int userId, int callingPid, int callingUid)
            afterMethod(
                lpparam.classLoader.shortcutServiceLocalServiceClass(),
                "getShortcuts",
                logger
            ) { param ->
                param.logParamsOnDebug(logger)

                @Suppress("UNCHECKED_CAST")
                val resList = param.result as List<ShortcutInfo>
                if (resList.isNotEmpty()) {
                    logger.d("============")
                    logger.d(
                        "Local shortcuts, ${
                            resList.joinToString(System.lineSeparator()) {
                                "${it.`package`}-${it.label}"
                            }
                        }"
                    )
                    logger.d("============")

                    val callingPackage = param.args[1]
                    val changedSince = param.args[2]
                    val packageName = param.args[3]
                    val shortcutIds = param.args[4]
                    val componentName = param.args[6]
                    val isLauncher =
                        getAllLaunchers(AppGlobals.context).also { logger.d("getAllLaunchers: $it") }
                            .contains(callingPackage)
                    logger.d("isLauncher: $isLauncher, callingPackage: $callingPackage, packageName: $packageName, changedSince: $changedSince, shortcutIds: $shortcutIds, componentName: $componentName")
                    if (isLauncher && packageName == null && shortcutIds == null && componentName == null) {
                        logger.w("Insert for all packages!!")

                        val insertShortcuts: List<ShortcutInfo> =
                            ShortXService.shortXContext.appState.getInstalledApps()
                                .apply {
                                    logger.d("getInstalledApps: ${this.size}")
                                }
                                .filter { it.category == AppCategory.User }.mapNotNull {
                                    val mainActivity =
                                        AppGlobals.context.packageManager.getLaunchIntentForPackage(
                                            it.pkg.pkgName
                                        )?.component
                                            ?: return@mapNotNull null
                                    val appShortcut =
                                        newShortcutInfo(mainActivity, mainActivity.packageName)
                                    logger.d("Add appShortcut: ${appShortcut.`package`}")
                                    appShortcut
                                }

                        val updatedResList = insertShortcuts.toMutableList().apply {
                            addAll(resList)
                        }
                        param.result = updatedResList
                    }
                } else {
                    logger.d("No shortcuts.")
                }
            }
        }.onFailure {
            logger.e(it, "hook getShortcuts")
        }
    }

    private fun hookStartShortcut(lpparam: ISystemServerLoaded.Param) {
        kotlin.runCatching {
            afterMethod(
                lpparam.classLoader.launcherAppsServiceImplClass(),
                "startShortcutInner",
                logger
            ) { param ->
                //  boolean startShortcutInner(int callerUid, int callerPid, int callingUserId,
                //                String callingPackage, String packageName, String featureId, String shortcutId,
                //                Rect sourceBounds, Bundle startActivityOptions, int targetUserId)
                param.logParamsOnDebug(logger)


            }
        }.onFailure {
            logger.e(it, "hook startShortcutInner")
        }
    }

    private val shortxIconOrNull by lazy {
        runCatching {
            val bitmap = BitmapUtils.getBitmap(
                AppGlobals.context,
                AppGlobals.context.packageManager.getApplicationIcon(
                    BuildPropInternal.APP_PACKAGE_NAME
                )
            )
            Icon.createWithBitmap(bitmap)
        }.getOrElse {
            logger.e(it, "getIcon")
            null
        }
    }

    private fun newShortcutInfo(
        activity: ComponentName,
        pkgName: String,
        label: String = "ShortX"
    ): ShortcutInfo {
        val ourInfo =
            ShortcutInfo.Builder(object :
                ContextWrapper(AppGlobals.context) {
                override fun getPackageName(): String {
                    return pkgName
                }
            }, UUID.randomUUID().toString())
                .setShortLabel(label)
                .setLongLabel(label)
                .apply {
                    shortxIconOrNull?.let {
                        setIcon(it)
                    }
                }
                .setActivity(activity)
                .setRank(1)
                .build()

        ourInfo.addFlags(ShortcutInfo.FLAG_DYNAMIC)
        ourInfo.addFlags(ShortcutInfo.FLAG_IMMUTABLE)
        ourInfo.setReturnedByServer()
        return ourInfo
    }
}

private fun getAllLaunchers(context: Context): List<String> {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    return context.packageManager.queryIntentActivities(intent, 0)
        .map { it.activityInfo.packageName }
}