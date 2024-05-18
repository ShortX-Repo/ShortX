package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.clipboardServiceClass
import tornaco.apps.shortx.core.util.Logger

object ClipboardServiceHook {
    private val logger = Logger("ShortXHook.ClipboardServiceHook")

    fun hookClipboardService(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        hookCheckAccess(lpparam)
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
}
