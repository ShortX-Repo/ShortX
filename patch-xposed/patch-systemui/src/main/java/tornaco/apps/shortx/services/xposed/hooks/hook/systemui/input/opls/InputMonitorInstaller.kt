package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls

import android.content.Context
import android.view.MotionEvent
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.SystemUIHook
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture.Detector
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture.DetectorServiceImpl

fun SystemUIHook.installInputMonitor(context: Context) {
    val service = DetectorServiceImpl(context)
    if (!service.shouldInstallMonitor()) {
        logger.w("Should not install monitor.")
        return
    }
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
                service.reportEvent(it)
                if (!service.shouldDetectMotion) {
                    logger.v("shouldDetectMotion is false.")
                    return@enableInputReceiver false
                }
                if (it is MotionEvent) {
                    val consumed = detector.consumeEvent(it)
                    // logger.logOnDebug { "InputMonitor consumed: $consumed $it" }
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