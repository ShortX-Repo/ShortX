package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.os.Handler
import android.os.SystemClock
import android.view.MotionEvent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp

class EdgeGestureEngine(
    private val bridge: GestureBridge,
    private val handler: Handler,
    private val regionResolver: EdgeRegionResolver,
    private val directionResolver: GestureDirectionResolver,
    private val timerController: GestureTimerController,
    private val actionResolver: GestureActionResolver,
    private val injectionBridge: InputInjectionBridge,
    private val sequenceDetector: EdgeGestureSequenceDetector,
    private val hintDetector: EdgeGestureHintDetector,
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
    private var downAbsoluteX = 0f
    private var downAbsoluteY = 0f
    private var hitAreas: List<DetectedArea> = emptyList()
    private var hitRegion: Edge = Edge.UNRECOGNIZED
    private var recognizedDirection: Direction = Direction.UNKNOWN
    private var recognizedSwipeGestures: Set<SwipeGestureCandidate> = emptySet()
    private var gestureCaptured = false
    private var executionStarted = false
    private var cachedDownEvent: MotionEvent? = null
    private var firstTapUpEvent: MotionEvent? = null
    private var continuousHandler: ContinuousActionHandler? = null
    private var maxTravelDistanceSq = 0f
    private var lockedDeviceId = MotionEvent.INVALID_POINTER_ID
    private var lockedPointerId = MotionEvent.INVALID_POINTER_ID

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
        hintDetector.onTouchEvent(event)

        if (firstTapUpEvent != null) {
            return handleDoubleTapDown(event)
        }

        sequenceDetector.cancelTracking()
        resetState()

        downX = event.rawX
        downY = event.rawY
        regionResolver.toAbsolutePortraitPoint(downX, downY).also {
            downAbsoluteX = it.x
            downAbsoluteY = it.y
        }
        lockedDeviceId = event.deviceId
        lockedPointerId = event.getPointerId(event.actionIndex)

        val detectedAreas = regionResolver.detectAreas(downX, downY)
        if (detectedAreas.isEmpty()) {
            return false
        }

        hitAreas = detectedAreas
        hitRegion = detectedAreas.filterIsInstance<DetectedArea.PredefinedEdge>().firstOrNull()?.edge ?: Edge.UNRECOGNIZED
        gestureCaptured = true
        cachedDownEvent = MotionEvent.obtain(event)

        timerController.startLongPressTimer(LONG_PRESS_TIMEOUT_MS) {
            onLongPressTimeout()
        }

        listener?.onEdgeTouchDown(hitRegion, downX, downY)

        if (LOGV) logger.d("handleEdgeDownStart: areas=$detectedAreas, pos=($downX,$downY)")
        return true
    }

    private fun handleDoubleTapDown(event: MotionEvent): Boolean {
        val detectedAreas = regionResolver.detectAreas(event.rawX, event.rawY)
        if (detectedAreas.isEmpty() || detectedAreas != hitAreas) {
            resetState()
            return handleEdgeDownStart(event)
        }

        downX = event.rawX
        downY = event.rawY
        regionResolver.toAbsolutePortraitPoint(downX, downY).also {
            downAbsoluteX = it.x
            downAbsoluteY = it.y
        }
        lockedDeviceId = event.deviceId
        lockedPointerId = event.getPointerId(event.actionIndex)
        cachedDownEvent?.recycle()
        cachedDownEvent = MotionEvent.obtain(event)

        if (startContinuousExecution(Gesture.DoubleTap, event.eventTime)) {
            listener?.onGestureTriggered(hitRegion, GestureType.DoubleTap)
            hintDetector.onDiscreteGestureTriggered(hitAreas, Gesture.DoubleTap, downX, downY)
        }
        return true
    }

    private fun handleMove(event: MotionEvent): Boolean {
        hintDetector.onTouchEvent(event)

        if (!gestureCaptured) return false
        if (event.deviceId != lockedDeviceId) return false

        if (sequenceDetector.isTracking()) {
            return sequenceDetector.onTouchEvent(event)
        }

        if (executionStarted) {
            continuousHandler?.onMove(event.rawX, event.rawY, event.eventTime)
            return true
        }

        if (recognizedSwipeGestures.isEmpty()) {
            val snapshot = resolveSwipeGestureSnapshot(event.rawX, event.rawY)
            if (snapshot.gestures.isNotEmpty()) {
                recognizedSwipeGestures = snapshot.gestures.toSet()
                recognizedDirection = snapshot.preferredDirection
                timerController.cancelLongPressTimer()

                listener?.onDirectionResolved(
                    snapshot.preferredDirection,
                    gestureToGestureType(snapshot.preferredDirection)
                )

                val travelSq = travelDistanceSq(event.rawX, event.rawY)
                val thresholdPx = bridge.getSwipeDistanceThresholdDp()._dp.toFloat()
                if (travelSq >= thresholdPx * thresholdPx) {
                    startExecution(snapshot.gestures, event.eventTime, event.rawX, event.rawY)
                }
            }
        } else {
            val snapshot = resolveSwipeGestureSnapshot(event.rawX, event.rawY)
            val currentGestures = snapshot.gestures.toSet()
            if (currentGestures.isNotEmpty() && currentGestures.intersect(recognizedSwipeGestures).isEmpty()) {
                logger.logOnDebug(
                    "Direction changed: $recognizedSwipeGestures -> $currentGestures, cancelling"
                )
                cancelAndReplay()
                return false
            }

            val travelSq = travelDistanceSq(event.rawX, event.rawY)
            if (travelSq > maxTravelDistanceSq) {
                maxTravelDistanceSq = travelSq
            }

            val thresholdPx = bridge.getSwipeDistanceThresholdDp()._dp.toFloat()
            if (travelSq >= thresholdPx * thresholdPx && !executionStarted) {
                val gesturesForExecution = if (snapshot.gestures.isEmpty()) {
                    recognizedSwipeGestures.toList()
                } else {
                    recognizedSwipeGestures.intersect(snapshot.gestures.toSet()).ifEmpty {
                        recognizedSwipeGestures
                    }.toList()
                }
                startExecution(gesturesForExecution, event.eventTime, event.rawX, event.rawY)
            }

            if (executionStarted) {
                val progress = (travelSq / (thresholdPx * thresholdPx)).coerceIn(0f, 1f)
                listener?.onSwipeProgress(hitRegion, recognizedDirection, progress)
            }
        }

        return true
    }

    private fun handleUp(event: MotionEvent): Boolean {
        hintDetector.onTouchEvent(event)

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

        if (recognizedSwipeGestures.isNotEmpty()) {
            val travelSq = travelDistanceSq(event.rawX, event.rawY)
            val thresholdPx = bridge.getSwipeDistanceThresholdDp()._dp.toFloat()
            if (travelSq >= thresholdPx * thresholdPx) {
                val matchedGestures = recognizedSwipeGestures.filter { candidate ->
                    actionResolver.resolve(hitAreas, candidate.gesture, candidate.coordinateMode) != null
                }
                if (matchedGestures.isNotEmpty()) {
                    matchedGestures.forEach { candidate ->
                        listener?.onGestureTriggered(hitRegion, gestureToGestureType(candidate.gesture)!!)
                        bridge.handleEdgeGestureEvent(
                            hitAreas,
                            candidate.gesture,
                            downX,
                            downY,
                            candidate.coordinateMode,
                        )
                    }
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
        hintDetector.onTouchEvent(event)

        if (!gestureCaptured) return false
        sequenceDetector.cancelTracking()
        cancelAndReplay()
        return true
    }

    private fun handleTapRelease(event: MotionEvent): Boolean {
        val hasDoubleTap = actionResolver.hasAction(hitAreas, Gesture.DoubleTap)
        if (hasDoubleTap) {
            firstTapUpEvent = MotionEvent.obtain(event)
            timerController.startDoubleTapTimer(DOUBLE_TAP_TIMEOUT_MS) {
                onDoubleTapTimeout()
            }
            return true
        }

        val resolvedAction = actionResolver.resolve(hitAreas, Gesture.SingleTap)
        if (resolvedAction != null) {
            listener?.onGestureTriggered(hitRegion, GestureType.Tap)
            bridge.handleEdgeGestureEvent(
                hitAreas,
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
            val resolvedAction = actionResolver.resolve(hitAreas, Gesture.DoubleTap)
            if (resolvedAction != null) {
                listener?.onGestureTriggered(hitRegion, GestureType.DoubleTap)
                bridge.handleEdgeGestureEvent(
                    hitAreas,
                    Gesture.DoubleTap,
                    downX,
                    downY
                )
                consumeCachedEvent()
                resetState()
                return true
            }
        }
        bridge.handleEdgeGestureEvent(
            hitAreas,
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

        val gesture = Gesture.LongTap
        val resolvedAction = actionResolver.resolve(hitAreas, gesture)

        if (startContinuousExecution(gesture, SystemClock.uptimeMillis())) {
            hintDetector.onDiscreteGestureTriggered(hitAreas, gesture, downX, downY)
            return
        }

        if (resolvedAction != null) {
            listener?.onGestureTriggered(hitRegion, GestureType.LongPress)
            bridge.handleEdgeGestureEvent(
                hitAreas,
                gesture,
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
        bridge.handleEdgeGestureEvent(
            hitAreas,
            Gesture.SingleTap,
            downX,
            downY
        )
        consumeCachedEvent()
        resetState()
    }

    private fun startExecution(
        gestures: Collection<SwipeGestureCandidate>,
        eventTime: Long,
        currentX: Float,
        currentY: Float,
    ) {
        val areas = hitAreas
        val executionCandidates = gestures.distinct().mapNotNull { candidate ->
            val action = actionResolver.resolve(areas, candidate.gesture, candidate.coordinateMode)
            val continuousBinding = bridge.findContinuousGestureBinding(
                areas,
                candidate.gesture,
                candidate.coordinateMode,
            )
            val sequenceCandidates = bridge.findMatchingEdgeGestureSequences(
                areas,
                candidate.gesture,
                candidate.coordinateMode,
            )
            if (action == null && continuousBinding == null && sequenceCandidates.isEmpty()) {
                null
            } else {
                SwipeExecutionCandidate(
                    candidate.gesture,
                    candidate.coordinateMode,
                    action,
                    continuousBinding,
                    sequenceCandidates,
                )
            }
        }
        if (executionCandidates.isEmpty()) {
            // 这里不能只看单一方向，因为同一次物理滑动可能同时对应
            // “相对屏幕方向”和“绝对竖屏方向”两套语义。
            logger.logOnDebug("No action and no sequence candidates for $gestures in $hitRegion, replaying")
            cancelAndReplay()
            return
        }

        executionStarted = true
        consumeCachedEvent()

        val continuousBinding = executionCandidates.firstNotNullOfOrNull { it.continuousBinding }
        if (continuousBinding != null) {
            continuousHandler = bridge.createContinuousActionHandler(continuousBinding.payload)
            continuousHandler?.onStart(edgeGroup(hitRegion), downX, downY, eventTime)
            return
        }

        executionCandidates
            .filter { it.action != null }
            .forEach { candidate ->
                listener?.onGestureTriggered(hitRegion, gestureToGestureType(candidate.gesture) ?: GestureType.SwipeLeft)
                bridge.handleEdgeGestureEvent(
                    areas,
                    candidate.gesture,
                    downX,
                    downY,
                    candidate.coordinateMode,
                )
                hintDetector.onEdgeGestureTriggered(areas, candidate.gesture, true)
            }

        val sequenceCandidate = executionCandidates.firstOrNull { it.sequenceCandidates.isNotEmpty() }
        if (sequenceCandidate != null) {
            hintDetector.onEdgeGestureSequenceStarted(areas, sequenceCandidate.gesture)
            sequenceDetector.startTracking(
                pointerId = lockedPointerId,
                primaryGesture = sequenceCandidate.gesture,
                downX = downX,
                downY = downY,
                anchorX = currentX,
                anchorY = currentY,
                candidates = sequenceCandidate.sequenceCandidates,
            )
        }
    }

    private fun startContinuousExecution(gesture: Gesture, eventTime: Long): Boolean {
        val continuousBinding = bridge.findContinuousGestureBinding(hitAreas, gesture) ?: return false
        val handler = bridge.createContinuousActionHandler(continuousBinding.payload) ?: return false

        timerController.cancelAll()
        firstTapUpEvent?.recycle()
        firstTapUpEvent = null
        executionStarted = true
        consumeCachedEvent()
        continuousHandler = handler
        handler.onStart(edgeGroup(hitRegion), downX, downY, eventTime)
        return true
    }

    private fun edgeGroup(region: Edge): Int {
        return when (region) {
            Edge.LeftAll, Edge.LeftTop, Edge.LeftCenter, Edge.LeftBottom -> 0
            Edge.RightAll, Edge.RightTop, Edge.RightCenter, Edge.RightBottom -> 1
            Edge.TopAll, Edge.TopLeft, Edge.TopCenter, Edge.TopRight -> 2
            Edge.BottomAll, Edge.BottomLeft, Edge.BottomCenter, Edge.BottomRight -> 3
            else -> -1
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
        downAbsoluteX = 0f
        downAbsoluteY = 0f
        hitAreas = emptyList()
        hitRegion = Edge.UNRECOGNIZED
        recognizedDirection = Direction.UNKNOWN
        recognizedSwipeGestures = emptySet()
        gestureCaptured = false
        executionStarted = false
        maxTravelDistanceSq = 0f
        lockedDeviceId = MotionEvent.INVALID_POINTER_ID
        lockedPointerId = MotionEvent.INVALID_POINTER_ID
    }

    private fun travelDistanceSq(currentX: Float, currentY: Float): Float {
        val dx = currentX - downX
        val dy = currentY - downY
        return dx * dx + dy * dy
    }

    private fun resolveSwipeGestureSnapshot(currentX: Float, currentY: Float): SwipeGestureSnapshot {
        val gestures = linkedSetOf<SwipeGestureCandidate>()
        val relativeDirection = directionResolver.resolve(
            downX,
            downY,
            currentX,
            currentY,
            DIRECTION_MIN_THRESHOLD_DP._dp.toFloat()
        )
        if (relativeDirection != Direction.UNKNOWN) {
            gestures.add(
                SwipeGestureCandidate(
                    gesture = directionToGesture(relativeDirection),
                    coordinateMode = PredefinedEdgeCoordinateMode.RelativeToDisplay,
                )
            )
        }

        // 绝对区域模式需要先把当前触点映射回“竖屏坐标系”，再判断滑动方向。
        // 这样横屏下用户的实际手势，会被还原成竖屏时配置的方向语义。
        val currentAbsolutePoint = regionResolver.toAbsolutePortraitPoint(currentX, currentY)
        val absoluteDirection = directionResolver.resolve(
            downAbsoluteX,
            downAbsoluteY,
            currentAbsolutePoint.x,
            currentAbsolutePoint.y,
            DIRECTION_MIN_THRESHOLD_DP._dp.toFloat()
        )
        if (absoluteDirection != Direction.UNKNOWN) {
            gestures.add(
                SwipeGestureCandidate(
                    gesture = directionToGesture(absoluteDirection),
                    coordinateMode = PredefinedEdgeCoordinateMode.AbsolutePortrait,
                )
            )
        }

        return SwipeGestureSnapshot(
            gestures = gestures.toList(),
            preferredDirection = when {
                relativeDirection != Direction.UNKNOWN -> relativeDirection
                absoluteDirection != Direction.UNKNOWN -> absoluteDirection
                else -> Direction.UNKNOWN
            }
        )
    }

    private data class SwipeGestureSnapshot(
        val gestures: List<SwipeGestureCandidate>,
        val preferredDirection: Direction,
    )

    private data class SwipeExecutionCandidate(
        val gesture: Gesture,
        val coordinateMode: PredefinedEdgeCoordinateMode,
        val action: EdgeGesture?,
        val continuousBinding: ContinuousGestureBinding?,
        val sequenceCandidates: List<EdgeGestureSequenceCandidate>,
    )

    private data class SwipeGestureCandidate(
        val gesture: Gesture,
        val coordinateMode: PredefinedEdgeCoordinateMode,
    )

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

    private fun gestureToGestureType(gesture: Gesture): GestureType? {
        return when (gesture) {
            Gesture.SwipeFromLeft -> GestureType.SwipeRight
            Gesture.SwipeFromRight -> GestureType.SwipeLeft
            Gesture.SwipeFromTop -> GestureType.SwipeDown
            Gesture.SwipeFromBottom -> GestureType.SwipeUp
            else -> null
        }
    }
}
