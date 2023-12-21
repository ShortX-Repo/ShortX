package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ClipData
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.clipboardServiceClass
import tornaco.apps.shortx.core.platform.Classes.clipboardServiceImplClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService

object ClipboardServiceHook {
    private val logger = Logger("ShortXHook.ClipboardServiceHook")

    fun hookClipboardService(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        hookSetPrimaryClip(lpparam)
        hookSetPrimaryClipAsPackage(lpparam)
        hookCheckAccess(lpparam)
    }.onFailure {
        logger.e(it, "hookClipboardService")
    }

    private fun hookSetPrimaryClip(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(
            lpparam.classLoader.clipboardServiceImplClass(),
            "setPrimaryClip"
        ) { param ->
            handleSetPrimary(param)
        }
    }.onFailure {
        logger.e(it, "hookSetPrimaryClip")
    }

    private fun handleSetPrimary(param: XC_MethodHook.MethodHookParam) {
        val clip: ClipData? = param.args[0] as ClipData?
        clip?.let {
            logger.p("setPrimaryClip: $clip item count: ${clip.itemCount}")
            if (clip.itemCount > 0) {
                repeat(clip.itemCount) { index ->
                    val description = clip.description
                    val mime = description.getMimeType(index)
                    logger.p("setPrimaryClip@$index: $clip $description $mime")
                    val text = clip.getItemAt(index)?.text?.toString() ?: ""
                    if (text.isNotBlank()) {
                        ShortXService.shortXContext.input.onClipboardChange(text)
                    }
                }

            }

        }
    }

    private fun hookSetPrimaryClipAsPackage(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            afterMethod(
                lpparam.classLoader.clipboardServiceImplClass(),
                "setPrimaryClipAsPackage"
            ) { param ->
                handleSetPrimary(param)
            }
        }.onFailure {
            logger.e(it, "hookSetPrimaryClipAsPackage")
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
