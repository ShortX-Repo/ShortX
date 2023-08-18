package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.ClipData
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.clipboardServiceImplClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService

object ClipboardServiceHook {
    private val logger = Logger("ShortXHook.ClipboardServiceHook")

    fun hookClipboardService(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        hookSetPrimaryClip(lpparam)
        hookSetPrimaryClipAsPackage(lpparam)
    }.onFailure {
        logger.e(it, "hookClipboardService")
    }

    fun hookSetPrimaryClip(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
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
        val clip: ClipData = param.args[0] as ClipData
        logger.d("setPrimaryClip: $clip")
        val text = clip.getItemAt(0)?.text?.toString() ?: ""
        ShortXService.shortXContext.input.onClipboardChange(text)
    }

    fun hookSetPrimaryClipAsPackage(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(
            lpparam.classLoader.clipboardServiceImplClass(),
            "setPrimaryClipAsPackage"
        ) { param ->
            handleSetPrimary(param)
        }
    }.onFailure {
        logger.e(it, "hookSetPrimaryClipAsPackage")
    }
}
