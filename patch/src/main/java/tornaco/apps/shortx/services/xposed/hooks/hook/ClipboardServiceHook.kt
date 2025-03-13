package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import com.elvishew.xlog.XLog
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.platform.Classes.clipboardServiceClass
import tornaco.apps.shortx.core.platform.Classes.clipboardServiceImplClass
import tornaco.apps.shortx.core.platform.Methods.getPrimaryClip
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.util.Logger

object ClipboardServiceHook {
    private val logger = Logger("ShortXHook.ClipboardServiceHook")

    fun hookClipboardService(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        hookCheckAccess(lpparam)
        hookGetPrimaryClip(lpparam)
    }.onFailure {
        logger.e(it, "hookClipboardService")
    }

    // ReadClipBoard Need this.
    private fun hookCheckAccess(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(
            lpparam.classLoader.clipboardServiceClass(),
            "clipboardAccessAllowed"
        ) { param ->
            //  private boolean clipboardAccessAllowed(int op, String callingPackage, int uid, @UserIdInt int userId, boolean shouldNoteOp)
            val callingPackage = param.args[1]
            if (callingPackage == "android") {
                logger.d("Allow android to read clipboard")
                param.result = true
            }
        }
    }.onFailure {
        logger.e(it, "hookClipboardService")
    }

    private fun hookGetPrimaryClip(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(
            lpparam.classLoader.clipboardServiceImplClass(),
            getPrimaryClip
        ) { param ->
            var originRes: ClipData? = param.result as ClipData?
            logger.d("getPrimaryClip " + param.args.contentToString() + ", originRes: " + originRes)
            val pkg = param.args[0] as String
            if (pkg.startsWith(BuildPropInternal.APP_PACKAGE_NAME) || pkg == PackageNames.systemUI) {
                XLog.w("Inject binder for ClipboardService.getPrimaryClip")
                val service = ShortXManagerNative.shortX ?: return@afterMethod
                val bundle = Bundle()
                bundle.putBinder("shortx-binder", service.asBinder())
                val intent = Intent().putExtra("shortx-bundle", bundle)
                val item = ClipData.Item(intent)
                if (originRes == null) {
                    originRes = ClipData.newIntent("shortx-binder", intent)
                } else {
                    originRes.addItem(item)
                }
                param.result = originRes
                XLog.w("Set Result to inject shortx binder.")
            }
        }
    }.onFailure {
        logger.e(it, "hookGetPrimaryClip")
    }
}
