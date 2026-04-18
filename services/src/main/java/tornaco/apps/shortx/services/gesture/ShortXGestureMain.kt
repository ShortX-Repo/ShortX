package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.gesture.bpok.gesture.Detector
import tornaco.apps.shortx.services.gesture.bpok.gesture.DetectorServiceImpl

class ShortXGestureMain(
    private val context: Context,
) {
    companion object {
        const val LOGV = BuildPropInternal.APP_IS_DEBUG
    }

    private val logger = Logger("Detector")

    val service = DetectorServiceImpl(context)

    val detector = Detector(
        context = context,
        handler = newHandlerOfNewThread("shortx-detector"),
        service = service
    )

    fun filterMotionEvent(event: MotionEvent): Boolean? {
        if (!service.shouldDetectMotion) {
            logger.logOnDebug("shouldDetectMotion is false.")
            return null
        }
        val consumed = detector.consumeEvent(event)
        // logger.logOnDebug { "InputMonitor consumed: $consumed $it" }
        if (consumed == true) {
            logger.logOnDebug("pilferPointers. consumed")
            return false
        } else if (service.shouldStealMotionEvent(event)) {
            logger.logOnDebug("pilferPointers. steal.")
            return false
        }
        return null
    }
}
