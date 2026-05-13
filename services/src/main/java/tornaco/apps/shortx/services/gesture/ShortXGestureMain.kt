package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.view.MotionEvent
import android.view.Surface
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.core.util.windowManager
import tornaco.apps.shortx.services.gesture.bpok.gesture.CornerSwipeToCenterDetector
import tornaco.apps.shortx.services.gesture.bpok.gesture.DetectedArea
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureBridgeImpl
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureEngine
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureSequenceDetector
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureSequenceHintReporter
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeGestureHintDetector
import tornaco.apps.shortx.services.gesture.bpok.gesture.EdgeRegionResolver
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureActionResolver
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureDirectionResolver
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureHapticController
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureHintController
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureSequenceScheduler
import tornaco.apps.shortx.services.gesture.bpok.gesture.GestureTimerController
import tornaco.apps.shortx.services.gesture.bpok.gesture.InputInjectionBridgeImpl

class ShortXGestureMain(
    private val context: Context,
) {
    companion object {
        const val LOGV = BuildPropInternal.APP_IS_DEBUG
    }

    private val logger = Logger("Detector")

    val service = GestureBridgeImpl(context)

    private val detectorHandler = newHandlerOfNewThread("shortx-detector")

    private val regionResolver = EdgeRegionResolver(
        bridge = service,
        getScreenSize = { context.screenSize() },
        getDisplayRotation = { context.windowManager.defaultDisplay?.rotation ?: Surface.ROTATION_0 },
    )
    private val directionResolver = GestureDirectionResolver()
    private val timerController = GestureTimerController(detectorHandler)
    private val actionResolver = GestureActionResolver(service, regionResolver)
    private val injectionBridge = InputInjectionBridgeImpl()

    private val gestureHintController = GestureHintController(context)
    private val gestureHapticController = GestureHapticController(context)

    private val sequenceHintReporter = EdgeGestureSequenceHintReporter(
        gestureHintController, gestureHapticController
    )

    private val sequenceDetector = EdgeGestureSequenceDetector(
        scheduler = object : GestureSequenceScheduler {
            override fun removeCallbacks(runnable: Runnable) {
                detectorHandler.removeCallbacks(runnable)
            }
            override fun postDelayed(runnable: Runnable, delayMillis: Long): Boolean {
                return detectorHandler.postDelayed(runnable, delayMillis)
            }
        },
        service = service,
        elapsedRealtime = android.os.SystemClock::elapsedRealtime,
        dpToPx = { it._dp },
        hintSink = sequenceHintReporter,
    )

    private val hintDetector = EdgeGestureHintDetector(
        bridge = service,
        gestureHintController = gestureHintController,
        gestureHapticController = gestureHapticController,
        detectAreas = { x, y ->
            regionResolver.detectAreas(x, y)
        },
        toAbsolutePortraitPoint = regionResolver::toAbsolutePortraitPoint,
    )

    val engine = EdgeGestureEngine(
        bridge = service,
        handler = detectorHandler,
        regionResolver = regionResolver,
        directionResolver = directionResolver,
        timerController = timerController,
        actionResolver = actionResolver,
        injectionBridge = injectionBridge,
        sequenceDetector = sequenceDetector,
        hintDetector = hintDetector,
    )

    private val cornerSwipeDetector = CornerSwipeToCenterDetector(
        context = context,
        bridge = service,
        gestureHintController = gestureHintController,
        gestureHapticController = gestureHapticController,
        injectionBridge = injectionBridge,
    )

    fun filterMotionEvent(event: MotionEvent): Boolean? {
        if (!service.shouldDetectMotion) {
            logger.logOnDebug("shouldDetectMotion is false.")
            return null
        }

        val cornerConsumed = cornerSwipeDetector.onTouchEvent(event)
        if (cornerConsumed) {
            logger.logOnDebug("cornerSwipe consumed")
            return false
        }

        val consumed = engine.handleTouchSequence(event)
        if (consumed) {
            logger.logOnDebug("pilferPointers. consumed")
            return false
        }
        return null
    }
}
