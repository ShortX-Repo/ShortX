package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.classNameToCustomContextMenu
import tornaco.apps.shortx.core.platform.Classes.pmComputerEngineClass
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.res.remixResId
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService

object ComputerEngineHook {
    private val logger = Logger("ShortXHook.ComputerEngineHook")

    object QueryIntentActivities {
        val engineClassName =
            if (!OsUtils.isTOrAbove()) "com.android.server.pm.PackageManagerService" else "com.android.server.pm.ComputerEngine"
        val queryIntentActivityMethodName =
            if (!OsUtils.isTOrAbove()) "queryIntentActivities" else "queryIntentActivitiesInternal"

    }

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Replaced by TileHook.
        // hookGetServiceInfo(lpparam)
        hookQueryIntentActivities(lpparam)
    }

    private fun hookGetServiceInfo(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(
            lpparam.classLoader.pmComputerEngineClass(),
            "getServiceInfo"
        ) { param ->
            logger.d("getServiceInfo ${param.args.map { it }}")
            if (param.throwable != null) return@afterMethod
            if (param.result == null) return@afterMethod

            val name = param.args[0] as ComponentName?
            if (name != null && name.packageName == BuildPropInternal.APP_PACKAGE_NAME) {
                val tileName = name.className.substringAfterLast(".")
                val tileOrNull = StatusBarTile.entries.firstOrNull { it.name == tileName }
                logger.d("getServiceInfo: $name, tileOrNull?: $tileOrNull")
                if (tileOrNull != null) {
                    val iconName =
                        shortXManager.getStatusBarTileSettingsByNumber(tileOrNull.number)?.iconName
                    logger.d("getServiceInfo: $name, iconName?: $iconName")
                    if (iconName != null) {
                        val remixResId =
                            ShortXService.shortXContext.statusbar.appContext.remixResId(iconName)
                        logger.d("getServiceInfo: $name, remixResId?: $remixResId")
                        val resultServiceInfo = param.result as ServiceInfo
                        logger.d("getServiceInfo: $name, resultServiceInfo?: $resultServiceInfo")
                        resultServiceInfo.icon = remixResId
                        param.result = resultServiceInfo
                    }
                }
            }
        }
    }.onFailure {
        logger.e(it, "hookGetServiceInfo")
    }

    private fun hookQueryIntentActivities(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            afterMethod(
                XposedHelpers.findClass(QueryIntentActivities.engineClassName, lpparam.classLoader),
                QueryIntentActivities.queryIntentActivityMethodName
            ) { param ->
                logger.d("queryIntentActivities ${param.args.map { it }}")
                if (param.throwable != null) return@afterMethod
                if (param.result == null) return@afterMethod

                val intent = param.args[0] as Intent?
                if (Intent.ACTION_PROCESS_TEXT == intent?.action) {
                    logger.w("queryIntentActivities, ACTION_PROCESS_TEXT action.")
                    val result = param.result as List<*>
                    logger.d(
                        "queryIntentActivities, result: ${
                            result.joinToString {
                                val resolveInfo = it as ResolveInfo
                                resolveInfo.resolvePackageName + "-" + resolveInfo.activityInfo.name + "-" + resolveInfo.nonLocalizedLabel
                            }
                        }")
                    param.result = result.map { resultItem ->
                        val resolveInfo = resultItem as ResolveInfo
                        val name: String? = resolveInfo.activityInfo.name
                        if (name?.startsWith(BuildPropInternal.CUSTOM_CONTEXT_MENU_CLASSNAME_BASE) == true) {
                            logger.w("queryIntentActivities, name: $name")
                            val menu = name.classNameToCustomContextMenu()
                            logger.w("queryIntentActivities, menu: $menu")
                            if (menu != null) {
                                val label =
                                    ShortXService.shortXContext.systemSettings.getContextMenuSettingsByMenuNumber(
                                        menu.number
                                    )?.label
                                logger.w("queryIntentActivities, label: $label")
                                label?.takeIf { it.isNotBlank() }?.let {
                                    resolveInfo.nonLocalizedLabel = it
                                }
                            }
                        }
                        resolveInfo
                    }
                }
            }
        }.onFailure {
            logger.e(it, "hookGetServiceInfo")
        }
}