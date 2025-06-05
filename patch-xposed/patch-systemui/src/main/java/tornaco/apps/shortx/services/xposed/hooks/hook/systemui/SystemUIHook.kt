package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import android.util.Log
import de.robv.android.xposed.XposedBridge
import tornaco.apps.shortx.core.AppGlobals
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded

object SystemUIHook {
    internal val logger = Logger("SystemUIHook")

    fun handleLoadPackage(lpparam: IPackageLoaded.Param) {
        if (PackageNames.systemUI == lpparam.packageName) {
            val isMainProcess = PackageNames.systemUI == lpparam.processName
            XposedBridge.log("SystemUIHook.isMainProcess- $isMainProcess")
            if (!isMainProcess) return
            // Install log
            logAdapter = { level: Int, tag: String, msg: String ->
                if (BuildPropInternal.APP_IS_DEBUG || level > Log.INFO) {
                    XposedBridge.log("ShortXSystemUi $tag - $msg")
                } else {
                    Log.println(Log.INFO, "ShortXSystemUi", "$tag - $msg")
                }
            }
            hookApp(lpparam)
            hookTile(lpparam)
            hookQS(lpparam)
            hookTileQuery(lpparam)
            hookView(lpparam)
            hookDarkIcon(lpparam)
        }
    }

    fun onPackageLoaded() {
        hookTileIcon()
    }

    fun initShortXAppContext(context: Context) {
        AppGlobals.setContext(context)
        val appRes = AppResources(
            context,
            BuildPropInternal.APP_PACKAGE_NAME
        )
        AppGlobals.setAppResources(appRes)
        logger.w("initShortXAppContext: ${AppGlobals.appContext}")
    }
}
