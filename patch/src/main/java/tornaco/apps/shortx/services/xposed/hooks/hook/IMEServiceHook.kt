package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.imeService
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.services.ShortXService

object IMEServiceHook {
    private val logger = Logger("ShortXHook.IMEServiceHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookStartInput(lpparam)
        hookHideCurrentInputLocked(lpparam)
        hookShowCurrentInputLocked(lpparam)
    }

    private fun hookStartInput(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(lpparam.classLoader.imeService(), "startInputOrWindowGainedFocus") { param ->
            logger.d("startInputOrWindowGainedFocus ${param.args.map { it }}")
        }
    }.onFailure {
        logger.e(it, "hookStartInput")
    }

    private fun hookHideCurrentInputLocked(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            afterMethod(lpparam.classLoader.imeService(), "hideCurrentInputLocked") { param ->
                logger.d("hideCurrentInputLocked ${param.args.map { it }}")
                val result = param.result as Boolean
                if (result) {
                    ShortXService.shortXContext.input.onHideCurrentInput()
                }
            }
        }.onFailure {
            logger.e(it, "hookHideCurrentInputLocked")
        }

    private fun hookShowCurrentInputLocked(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            afterMethod(lpparam.classLoader.imeService(), "showCurrentInputLocked") { param ->
                logger.d("showCurrentInputLocked ${param.args.map { it }}")
                val result = param.result as Boolean
                if (result) {
                    ShortXService.shortXContext.input.onShowCurrentInput()
                }
            }
        }.onFailure {
            logger.e(it, "hookShowCurrentInputLocked")
        }
}