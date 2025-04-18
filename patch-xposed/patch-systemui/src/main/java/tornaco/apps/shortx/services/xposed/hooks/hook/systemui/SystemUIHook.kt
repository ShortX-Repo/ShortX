package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import android.util.Log
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.logAdapter

object SystemUIHook {
    internal val logger = Logger("SystemUIHook")

    var shortxAppContext: Context? = null
        private set

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (PackageNames.systemUI == lpparam.packageName) {
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
        val appRes = AppResources(
            context,
            BuildPropInternal.APP_PACKAGE_NAME
        )
        this.shortxAppContext = appRes.appContext
        logger.w("initShortXAppContext: $shortxAppContext")
    }
}
