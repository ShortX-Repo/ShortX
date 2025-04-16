package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls

import android.content.Context
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.SystemUIHook
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture.Detector
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture.DetectorCallbackImpl

fun SystemUIHook.installInputMonitor(context: Context) {
    if (BuildPropInternal.APP_IS_DEBUG) {
        val detector = Detector(
            context,
            newHandlerOfNewThread("shortx-input"),
            DetectorCallbackImpl()
        )
        logger.w("installInputMonitor.")
        OplusInputMonitorHelper.getInstance().apply {
            init(context)
            enableInputReceiver(
                {
                    if (it is MotionEvent) {
                        val consumed = detector.consumeEvent(it)
                        logger.v("InputMonitor consumed: $consumed $it")
                        if (consumed == true) {
                            pilferPointers()
                            return@enableInputReceiver true
                        }
                    }
                    return@enableInputReceiver false
                }, false
            )
        }
    }
}