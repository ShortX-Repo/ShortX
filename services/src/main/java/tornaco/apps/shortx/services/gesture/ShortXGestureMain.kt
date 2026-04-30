package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.services.gesture.bpok.gesture.DetectorServiceImpl
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureEngine
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureHintDetector
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeRegionResolver
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureActionResolver
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureDirectionResolver
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureHapticController
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureHintController
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureTimerController
import tornaco.apps.shortx.services.gesture.bpok.gesture.InputInjectionBridgeImpl

class ShortXGestureMain(
    private val context: Context,
) {
    companion object {
        const val LOGV = BuildPropInternal.APP_IS_DEBUG
    }

    private val logger = Logger("Detector")

    val service = DetectorServiceImpl(context)

    private val detectorHandler = newHandlerOfNewThread("shortx-detector")

    private val regionResolver = EdgeRegionResolver(service) { context.screenSize() }
    private val directionResolver = GestureDirectionResolver()
    private val timerController = GestureTimerController(detectorHandler)
    private val actionResolver = GestureActionResolver(service, regionResolver)
    private val injectionBridge = InputInjectionBridgeImpl()

    private val gestureHintController = GestureHintController(context)
    private val gestureHapticController = GestureHapticController(context)

    val engine = EdgeGestureEngine(
        service = service,
        handler = detectorHandler,
        regionResolver = regionResolver,
        directionResolver = directionResolver,
        timerController = timerController,
        actionResolver = actionResolver,
        injectionBridge = injectionBridge
    )

    fun filterMotionEvent(event: MotionEvent): Boolean? {
        if (!service.shouldDetectMotion) {
            logger.logOnDebug("shouldDetectMotion is false.")
            return null
        }
        val consumed = engine.handleTouchSequence(event)
        if (consumed) {
            logger.logOnDebug("pilferPointers. consumed")
            return false
        }
        return null
    }
}
