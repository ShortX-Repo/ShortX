package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls

import android.content.Context
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.SystemUIHook
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture.Detector

fun SystemUIHook.installInputMonitor(context: Context) {
    if (BuildPropInternal.APP_IS_DEBUG) {
        val detector = Detector(context, newHandlerOfNewThread("shortx-input"))
        logger.w("installInputMonitor.")
        OplusInputMonitorHelper.getInstance().apply {
            init(context)
            enableInputReceiver(
                {
                    true
                }, false
            )
        }
    }
}