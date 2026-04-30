package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.os.Handler
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp

class EdgeGestureEngine(
    private val service: DetectorService,
    private val handler: Handler,
    private val regionResolver: EdgeRegionResolver,
    private val directionResolver: GestureDirectionResolver,
    private val timerController: GestureTimerController,
    private val actionResolver: GestureActionResolver,
    private val injectionBridge: InputInjectionBridge,
    private val sequenceDetector: EdgeGestureSequenceDetector,
) {
    companion object {
        const val LOGV = BuildPropInternal.APP_IS_DEBUG
        private const val DIRECTION_MIN_THRESHOLD_DP = 8
        private const val LONG_PRESS_TIMEOUT_MS = 500L
        private const val DOUBLE_TAP_TIMEOUT_MS = 300L
        private const val MIN_TRAVEL_FOR_TRIGGER_DP = 24
    }

    private val logger = Logger("EdgeGestureEngine")

    private var downX = 0f
    private var downY = 0f
    private var hitRegion: Edge = Edge.UNRECOGNIZED
    private var recognizedDirection: Direction = Direction.UNKNOWN
    private var gestureCaptured = false
    private var executionStarted = false
    private var cachedDownEvent: MotionEvent? = null
    private var firstTapUpEvent: MotionEvent? = null
    private var continuousHandler: ContinuousActionHandler? = null
    private var maxTravelDistanceSq = 0f
    private var lockedDeviceId = MotionEvent.INVALID_POINTER_ID

    var listener: GestureEngineListener? = null

    fun handleTouchSequence(event: MotionEvent): Boolean {
        if (!event.isTouchEvent) return false

        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> handleEdgeDownStart(event)
            MotionEvent.ACTION_MOVE -> handleMove(event)
            MotionEvent.ACTION_UP -> handleUp(event)
            MotionEvent.ACTION_CANCEL -> handleCancel(event)
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP -> {
                if (gestureCaptured) {
                    cancelAndReplay()
                    true
                } else false
            }
            else -> false
        }
    }

    fun cancelAndReplay() {
        sequenceDetector.cancelTracking()
        if (executionStarted) {
            continuousHandler?.onCancel()
        }
        replayCachedEvent()
        resetState()
    }

    fun isTracking(): Boolean = gestureCaptured

    private fun handleEdgeDownStart(event: MotionEvent): Boolean {
        if (firstTapUpEvent != null) {
            return handleDoubleTapDown(event)
        }

        sequenceDetector.cancelTracking()
        resetState()

        downX = event.rawX
        downY = event.rawY
        lockedDeviceId = event.deviceId

        val region = regionResolver.resolve(downX, downY)
        if (region == Edge.UNRECOGNIZED) {
            return false
        }

        hitRegion = region
        gestureCaptured = true
        cachedDownEvent = MotionEvent.obtain(event)

        timerController.startLongPressTimer(LONG_PRESS_TIMEOUT_MS) {
            onLongPressTimeout()
        }

        listener?.onEdgeTouchDown(region, downX, downY)

        if (LOGV) logger.d("handleEdgeDownStart: region=$region, pos=($downX,$downY)")
        return true
    }

    private fun handleDoubleTapDown(event: MotionEvent): Boolean {
        val region = regionResolver.resolve(event.rawX, event.rawY)
        if (region == Edge.UNRECOGNIZED || region != hitRegion) {
            resetState()
            return handleEdgeDownStart(event)
        }

        downX = event.rawX
        downY = event.rawY
        lockedDeviceId = event.deviceId
        cachedDownEvent?.recycle()
        cachedDownEvent = MotionEvent.obtain(event)
        return true
    }

    private fun handleMove(event: MotionEvent): Boolean {
        if (!gestureCaptured) return false
        if (event.deviceId != lockedDeviceId) return false

        if (sequenceDetector.isTracking()) {
            return sequenceDetector.onTouchEvent(event)
        }

        if (executionStarted) {
            continuousHandler?.onMove(event.rawX, event.rawY, event.eventTime)
            return true
        }

        if (recognizedDirection == Direction.UNKNOWN) {
            val dir = directionResolver.resolve(
                downX, downY, event.rawX, event.rawY,
                DIRECTION_MIN_THRESHOLD_DP._dp.toFloat()
            )
            if (dir != Direction.UNKNOWN) {
                recognizedDirection = dir
                timerController.cancelLongPressTimer()

                val gesture = directionToGesture(dir)
                val resolvedAction = actionResolver.resolve(hitRegion, gesture)

                listener?.onDirectionResolved(dir, gestureToGestureType(dir))

                val travelSq = travelDistanceSq(event.rawX, event.rawY)
                val thresholdPx = service.getSwipeDistanceThresholdDp()._dp.toFloat()
                if (travelSq >= thresholdPx * thresholdPx) {
                    startExecution(resolvedAction, dir, event.eventTime, event.rawX, event.rawY)
                }
            }
        } else {
            val currentDir = directionResolver.resolve(
                downX, downY, event.rawX, event.rawY,
                DIRECTION_MIN_THRESHOLD_DP._dp.toFloat()
            )
            if (currentDir != Direction.UNKNOWN && currentDir != recognizedDirection) {
                logger.logOnDebug("Direction changed: $recognizedDirection -> $currentDir, cancelling")
                cancelAndReplay()
                return false
            }

            val travelSq = travelDistanceSq(event.rawX, event.rawY)
            if (travelSq > maxTravelDistanceSq) {
                maxTravelDistanceSq = travelSq
            }

            val thresholdPx = service.getSwipeDistanceThresholdDp()._dp.toFloat()
            if (travelSq >= thresholdPx * thresholdPx && !executionStarted) {
                val gesture = directionToGesture(recognizedDirection)
                val resolvedAction = actionResolver.resolve(hitRegion, gesture)
                startExecution(resolvedAction, recognizedDirection, event.eventTime, event.rawX, event.rawY)
            }

            if (executionStarted) {
                val progress = (travelSq / (thresholdPx * thresholdPx)).coerceIn(0f, 1f)
                listener?.onSwipeProgress(hitRegion, recognizedDirection, progress)
            }
        }

        return true
    }

    private fun handleUp(event: MotionEvent): Boolean {
        if (!gestureCaptured) return false
        if (event.deviceId != lockedDeviceId) return false

        if (sequenceDetector.isTracking()) {
            return sequenceDetector.onTouchEvent(event)
        }

        if (executionStarted) {
            continuousHandler?.onFinish(event.rawX, event.rawY, event.eventTime)
            consumeCachedEvent()
            resetState()
            return true
        }

        if (recognizedDirection != Direction.UNKNOWN) {
            val travelSq = travelDistanceSq(event.rawX, event.rawY)
            val thresholdPx = service.getSwipeDistanceThresholdDp()._dp.toFloat()
            if (travelSq >= thresholdPx * thresholdPx) {
                val gesture = directionToGesture(recognizedDirection)
                val resolvedAction = actionResolver.resolve(hitRegion, gesture)
                if (resolvedAction != null) {
                    listener?.onGestureTriggered(hitRegion, gestureToGestureType(recognizedDirection)!!)
                    service.handleEdgeGestureEvent(
                        listOf(DetectedArea.PredefinedEdge(hitRegion)),
                        gesture,
                        downX,
                        downY
                    )
                    consumeCachedEvent()
                    resetState()
                    return true
                }
            }
            cancelAndReplay()
            return false
        }

        if (firstTapUpEvent != null) {
            return handleSecondTap(event)
        }

        return handleTapRelease(event)
    }

    private fun handleCancel(event: MotionEvent): Boolean {
        if (!gestureCaptured) return false
        sequenceDetector.cancelTracking()
        cancelAndReplay()
        return true
    }

    private fun handleTapRelease(event: MotionEvent): Boolean {
        val hasDoubleTap = actionResolver.hasAction(hitRegion, Gesture.DoubleTap)
        if (hasDoubleTap) {
            firstTapUpEvent = MotionEvent.obtain(event)
            timerController.startDoubleTapTimer(DOUBLE_TAP_TIMEOUT_MS) {
                onDoubleTapTimeout()
            }
            return true
        }

        val resolvedAction = actionResolver.resolve(hitRegion, Gesture.SingleTap)
        if (resolvedAction != null) {
            listener?.onGestureTriggered(hitRegion, GestureType.Tap)
            service.handleEdgeGestureEvent(
                listOf(DetectedArea.PredefinedEdge(hitRegion)),
                Gesture.SingleTap,
                downX,
                downY
            )
            consumeCachedEvent()
            resetState()
            return true
        }

        cancelAndReplay()
        return false
    }

    private fun handleSecondTap(event: MotionEvent): Boolean {
        val firstUp = firstTapUpEvent ?: return false
        val interval = event.eventTime - firstUp.eventTime
        if (interval < 40) {
            cancelAndReplay()
            return false
        }
        if (interval <= DOUBLE_TAP_TIMEOUT_MS) {
            val resolvedAction = actionResolver.resolve(hitRegion, Gesture.DoubleTap)
            if (resolvedAction != null) {
                listener?.onGestureTriggered(hitRegion, GestureType.DoubleTap)
                service.handleEdgeGestureEvent(
                    listOf(DetectedArea.PredefinedEdge(hitRegion)),
                    Gesture.DoubleTap,
                    downX,
                    downY
                )
                consumeCachedEvent()
                resetState()
                return true
            }
        }
        service.handleEdgeGestureEvent(
            listOf(DetectedArea.PredefinedEdge(hitRegion)),
            Gesture.SingleTap,
            downX,
            downY
        )
        consumeCachedEvent()
        resetState()
        return true
    }

    private fun onLongPressTimeout() {
        if (!gestureCaptured || executionStarted) return
        if (recognizedDirection != Direction.UNKNOWN) return

        val resolvedAction = actionResolver.resolve(hitRegion, Gesture.LongTap)
        if (resolvedAction != null) {
            listener?.onGestureTriggered(hitRegion, GestureType.LongPress)
            service.handleEdgeGestureEvent(
                listOf(DetectedArea.PredefinedEdge(hitRegion)),
                Gesture.LongTap,
                downX,
                downY
            )
            consumeCachedEvent()
            resetState()
        } else {
            cancelAndReplay()
        }
    }

    private fun onDoubleTapTimeout() {
        if (!gestureCaptured) return
        service.handleEdgeGestureEvent(
            listOf(DetectedArea.PredefinedEdge(hitRegion)),
            Gesture.SingleTap,
            downX,
            downY
        )
        consumeCachedEvent()
        resetState()
    }

    private fun startExecution(action: Any?, direction: Direction, eventTime: Long, currentX: Float, currentY: Float) {
        val gesture = directionToGesture(direction)
        val areas = listOf(DetectedArea.PredefinedEdge(hitRegion))

        val sequenceCandidates = service.findMatchingEdgeGestureSequences(areas, gesture)
        if (action == null && sequenceCandidates.isEmpty()) {
            logger.logOnDebug("No action and no sequence candidates for $direction in $hitRegion, replaying")
            cancelAndReplay()
            return
        }

        executionStarted = true
        consumeCachedEvent()

        if (action != null) {
            listener?.onGestureTriggered(hitRegion, gestureToGestureType(direction) ?: GestureType.SwipeLeft)
            service.handleEdgeGestureEvent(areas, gesture, downX, downY)
        }

        if (sequenceCandidates.isNotEmpty()) {
            sequenceDetector.startTracking(
                pointerId = 0,
                primaryGesture = gesture,
                downX = downX,
                downY = downY,
                anchorX = currentX,
                anchorY = currentY,
                candidates = sequenceCandidates,
            )
        }
    }

    private fun replayCachedEvent() {
        cachedDownEvent?.let { event ->
            try {
                val copy = MotionEvent.obtain(event)
                injectionBridge.injectEvent(copy)
            } catch (e: Exception) {
                logger.e(e, "Failed to replay cached event")
            }
        }
    }

    private fun consumeCachedEvent() {
        cachedDownEvent?.recycle()
        cachedDownEvent = null
    }

    private fun resetState() {
        timerController.cancelAll()
        firstTapUpEvent?.recycle()
        firstTapUpEvent = null
        cachedDownEvent?.recycle()
        cachedDownEvent = null
        continuousHandler = null
        downX = 0f
        downY = 0f
        hitRegion = Edge.UNRECOGNIZED
        recognizedDirection = Direction.UNKNOWN
        gestureCaptured = false
        executionStarted = false
        maxTravelDistanceSq = 0f
        lockedDeviceId = MotionEvent.INVALID_POINTER_ID
    }

    private fun travelDistanceSq(currentX: Float, currentY: Float): Float {
        val dx = currentX - downX
        val dy = currentY - downY
        return dx * dx + dy * dy
    }

    private fun directionToGesture(dir: Direction): Gesture {
        return when (dir) {
            Direction.LEFT -> Gesture.SwipeFromRight
            Direction.RIGHT -> Gesture.SwipeFromLeft
            Direction.UP -> Gesture.SwipeFromBottom
            Direction.DOWN -> Gesture.SwipeFromTop
            Direction.UNKNOWN -> Gesture.SingleTap
        }
    }

    private fun gestureToGestureType(dir: Direction): GestureType? {
        return when (dir) {
            Direction.LEFT -> GestureType.SwipeLeft
            Direction.RIGHT -> GestureType.SwipeRight
            Direction.UP -> GestureType.SwipeUp
            Direction.DOWN -> GestureType.SwipeDown
            Direction.UNKNOWN -> null
        }
    }
}
