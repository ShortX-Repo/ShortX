package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.windowManagerServiceClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import util.Reflections

object WMServiceHook {
    private val logger = Logger("ShortXHook.WMServiceHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        hookNotifyFocusChanged(lpparam)
    }.onFailure {
        logger.e(it, "hookClipboardService")
    }

    private fun hookNotifyFocusChanged(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            afterMethod(
                lpparam.classLoader.windowManagerServiceClass(),
                "notifyFocusChanged"
            ) { param ->
                logger.d("notifyFocusChanged: ${param.args.map { it }}")
                val focused = Reflections.callMethod(param.thisObject, "getFocusedWindow")
                val ownerUid = Reflections.getIntField(focused, "mOwnerUid")
                logger.d("notifyFocusChanged: $focused, owner: $ownerUid")
                ShortXService.shortXContext.wm.onWindowFocusChanged(ownerUid)

            }
        }.onFailure {
            logger.e(it, "hookNotifyFocusChanged")
        }
}
