package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls

import android.content.Context
import android.view.MotionEvent
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.SystemUIHook
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture.Detector
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture.DetectorServiceImpl

fun SystemUIHook.installInputMonitor(context: Context) {
    val service = DetectorServiceImpl(context)
    val detector = Detector(
        context = context,
        handler = newHandlerOfNewThread("shortx-detector"),
        service = service
    )
    logger.w("installInputMonitor.")
    InputMonitorHelper.getInstance().apply {
        init(context)
        enableInputReceiver(
            {
                if (!service.shouldDetectMotion) {
                    logger.v("shouldDetectMotion is false.")
                    return@enableInputReceiver false
                }
                if (it is MotionEvent) {
                    val consumed = detector.consumeEvent(it)
                    logger.v("InputMonitor consumed: $consumed $it")
                    if (consumed == true) {
                        logger.d("pilferPointers. consumed")
                        pilferPointers()
                        return@enableInputReceiver true
                    } else if (service.shouldStealMotionEvent(it)) {
                        logger.d("pilferPointers. steal.")
                        pilferPointers()
                    }
                }
                return@enableInputReceiver false
            }, false
        )
    }
}