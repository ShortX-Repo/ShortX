package tornaco.apps.shortx.services.xposed.hooks.hook

import android.view.KeyEvent
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.phoneWindowManagerClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService

object PhoneWindowManagerHook {
    private val logger = Logger("XposedEntry.PhoneWindowManagerHook")
    private val keyCodesToHandle = arrayOf(
        KeyEvent.KEYCODE_HOME,
        KeyEvent.KEYCODE_BACK,
        KeyEvent.KEYCODE_POWER,
    )

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        logger.d("hook")
        // public int interceptKeyBeforeQueueing(KeyEvent event, int policyFlags) {
        afterMethod(
            lpparam.classLoader.phoneWindowManagerClass(),
            Methods.interceptKeyBeforeQueueing
        ) { param ->
            logger.d("interceptKeyBeforeQueueing: ${param.args.map { it }}")
            // Only handle HOME/BACK/RECENT/POWER
            val keyEvent = param.args[0] as KeyEvent

            if (keyCodesToHandle.contains(keyEvent.keyCode)) {
                val res = ShortXService.shortXContext.gesture.handleKeyEvent(keyEvent)
                if (res == false) {
                    param.result = 0
                }
            }
        }
    }
}