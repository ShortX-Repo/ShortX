package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.view.MotionEvent
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.util._dp
import kotlin.math.cos
import kotlin.math.hypot

class EdgeGestureHintDetector(
    private val bridge: GestureBridge,
    private val gestureHintController: GestureHintController,
    private val gestureHapticController: GestureHapticController,
    private val detectAreas: (Float, Float) -> List<DetectedArea>,
) {
    companion object {
        private const val EDGE_HINT_MAX_ANGLE_DEVIATION_DEG = 50f
        private const val EDGE_TRIGGER_MAX_ANGLE_DEVIATION_DEG = 30f
        private const val GESTURE_HINT_SHOW_DISTANCE_DP = 12
        private const val GESTURE_HINT_MAX_VISUAL_DISTANCE_DP = 72
        private val MIN_HINT_DIRECTION_DOT =
            cos(Math.toRadians(EDGE_HINT_MAX_ANGLE_DEVIATION_DEG.toDouble())).toFloat()
        private val MIN_TRIGGER_DIRECTION_DOT =
            cos(Math.toRadians(EDGE_TRIGGER_MAX_ANGLE_DEVIATION_DEG.toDouble())).toFloat()
    }

    private data class HintCandidate(
        val directionX: Float,
        val directionY: Float,
        val dot: Float,
        val hintEnabled: Boolean,
        val hapticEnabled: Boolean,
    )

    private var nextToken = 1L
    private var trackedToken = 0L
    private var trackedPointerId = MotionEvent.INVALID_POINTER_ID
    private var trackedAreas: List<DetectedArea> = emptyList()
    private var downX = 0f
    private var downY = 0f
    private var hasHintShown = false
    private var hasTriggered = false

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                reset()
                trackedPointerId = event.getPointerId(event.actionIndex)
                downX = event.x
                downY = event.y
                trackedAreas = detectAreas(downX, downY).filter(::hasFeedbackEnabledSwipeRule)
                trackedToken = if (trackedAreas.isNotEmpty()) nextToken++ else 0L
                if (trackedToken != 0L && trackedAreas.any(::hasHapticEnabledSwipeRule)) {
                    gestureHapticController.onGestureCandidate(trackedToken)
                }
                if (trackedToken != 0L) {
                    showStartIndicator()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (trackedToken == 0L || hasTriggered) return false
                val pointerIndex = event.findPointerIndex(trackedPointerId)
                if (pointerIndex < 0) {
                    reset()
                    return false
                }

                val moveX = event.getX(pointerIndex)
                val moveY = event.getY(pointerIndex)
                val deltaX = moveX - downX
                val deltaY = moveY - downY
                val distance = hypot(deltaX.toDouble(), deltaY.toDouble()).toFloat()
                if (distance <= 0f) return false

                handleGestureHint(distance, findBestCandidate(deltaX, deltaY, distance))
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (!hasTriggered) {
                    gestureHintController.onGestureCancel(trackedToken)
                }
                reset()
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerId = event.getPointerId(event.actionIndex)
                if (pointerId == trackedPointerId) {
                    if (!hasTriggered) {
                        gestureHintController.onGestureCancel(trackedToken)
                    }
                    reset()
                }
            }
        }
        return false
    }

    fun onEdgeGestureTriggered(areas: List<DetectedArea>, gesture: Gesture, handled: Boolean) {
        if (!handled || trackedToken == 0L || hasTriggered) return
        val matches = areas.any { area ->
            trackedAreas.any { trackedArea -> trackedArea == area } && hasFeedbackEnabledSwipeRule(area, gesture)
        }
        if (!matches) return
        hasTriggered = true
        if (areas.any { area ->
                trackedAreas.any { trackedArea -> trackedArea == area } && hasHapticEnabledSwipeRule(area, gesture)
            }) {
            gestureHapticController.onGestureSuccess(trackedToken)
        }
        gestureHintController.onGestureSuccess(trackedToken)
    }

    fun onEdgeGestureSequenceStarted(areas: List<DetectedArea>, gesture: Gesture) {
        if (trackedToken == 0L || hasTriggered) return
        val matches = areas.any { area ->
            trackedAreas.any { trackedArea -> trackedArea == area } && hasFeedbackEnabledSwipeRule(area, gesture)
        }
        if (!matches) return
        gestureHintController.onGestureCancel(trackedToken)
        reset()
    }

    private fun handleGestureHint(distance: Float, candidate: HintCandidate?) {
        if (trackedToken == 0L) return
        val showThresholdPx = GESTURE_HINT_SHOW_DISTANCE_DP._dp.toFloat()
        if (distance < showThresholdPx) {
            return
        }

        if (candidate == null || candidate.dot < MIN_HINT_DIRECTION_DOT) {
            if (hasHintShown) {
                gestureHintController.onGestureCancel(trackedToken)
                hasHintShown = false
            }
            return
        }

        val readyDistancePx = maxOf(
            bridge.getSwipeDistanceThresholdDp()._dp.toFloat(),
            GESTURE_HINT_MAX_VISUAL_DISTANCE_DP._dp.toFloat()
        )
        val progress = ((distance - showThresholdPx) /
                (readyDistancePx - showThresholdPx)).coerceIn(0f, 1f)
        val state = if (distance >= bridge.getSwipeDistanceThresholdDp()._dp.toFloat() &&
            candidate.dot >= MIN_TRIGGER_DIRECTION_DOT
        ) {
            GestureHintState.Ready
        } else {
            GestureHintState.Showing
        }
        if (candidate.hapticEnabled && state == GestureHintState.Ready) {
            gestureHapticController.onGestureReady(trackedToken)
        }
        if (candidate.hintEnabled) {
            gestureHintController.onGestureProgress(
                GestureHintModel(
                    token = trackedToken,
                    anchorX = downX,
                    anchorY = downY,
                    directionX = candidate.directionX,
                    directionY = candidate.directionY,
                    progress = progress,
                    state = state,
                    showArrow = false,
                )
            )
            hasHintShown = true
        } else if (hasHintShown) {
            gestureHintController.onGestureCancel(trackedToken)
            hasHintShown = false
        }
    }

    private fun showStartIndicator() {
        if (!trackedAreas.any(::hasHintEnabledSwipeRule)) return
        gestureHintController.onGestureProgress(
            GestureHintModel(
                token = trackedToken,
                anchorX = downX,
                anchorY = downY,
                directionX = 0f,
                directionY = 0f,
                progress = 0.18f,
                state = GestureHintState.Candidate,
                showArrow = false,
            )
        )
    }

    private fun findBestCandidate(deltaX: Float, deltaY: Float, distance: Float): HintCandidate? {
        if (trackedAreas.isEmpty()) return null
        val normalizedX = deltaX / distance
        val normalizedY = deltaY / distance
        return trackedAreas
            .asSequence()
            .flatMap { area ->
                bridge.enabledEdgeGestures
                    .asSequence()
                    .filter { rule ->
                        !rule.isDisabled &&
                                rule.isGestureFeedbackEnabled() &&
                                rule.gesture.supportsGestureIndicator() &&
                                areaMatchesRule(area, rule)
                    }
                    .mapNotNull { rule ->
                        gestureDirection(rule.gesture)?.let { (directionX, directionY) ->
                            HintCandidate(
                                directionX = directionX,
                                directionY = directionY,
                                dot = normalizedX * directionX + normalizedY * directionY,
                                hintEnabled = rule.isGestureHintEnabled(),
                                hapticEnabled = rule.isGestureHapticEnabled(),
                            )
                        }
                    }
            }
            .groupBy { it.directionX to it.directionY }
            .values
            .map { grouped ->
                val first = grouped.first()
                HintCandidate(
                    directionX = first.directionX,
                    directionY = first.directionY,
                    dot = first.dot,
                    hintEnabled = grouped.any { it.hintEnabled },
                    hapticEnabled = grouped.any { it.hapticEnabled },
                )
            }
            .maxByOrNull { it.dot }
    }

    private fun hasFeedbackEnabledSwipeRule(area: DetectedArea): Boolean {
        return bridge.enabledEdgeGestures.any { rule ->
            !rule.isDisabled &&
                    rule.isGestureFeedbackEnabled() &&
                    rule.gesture.supportsGestureIndicator() &&
                    areaMatchesRule(area, rule)
        }
    }

    private fun hasFeedbackEnabledSwipeRule(area: DetectedArea, gesture: Gesture): Boolean {
        return bridge.enabledEdgeGestures.any { rule ->
            !rule.isDisabled &&
                    rule.isGestureFeedbackEnabled() &&
                    rule.gesture == gesture &&
                    rule.gesture.supportsGestureIndicator() &&
                    areaMatchesRule(area, rule)
        }
    }

    private fun hasHintEnabledSwipeRule(area: DetectedArea): Boolean {
        return bridge.enabledEdgeGestures.any { rule ->
            !rule.isDisabled &&
                    rule.isGestureHintEnabled() &&
                    rule.gesture.supportsGestureIndicator() &&
                    areaMatchesRule(area, rule)
        }
    }

    private fun hasHapticEnabledSwipeRule(area: DetectedArea): Boolean {
        return bridge.enabledEdgeGestures.any { rule ->
            !rule.isDisabled &&
                    rule.isGestureHapticEnabled() &&
                    rule.gesture.supportsGestureIndicator() &&
                    areaMatchesRule(area, rule)
        }
    }

    private fun hasHapticEnabledSwipeRule(area: DetectedArea, gesture: Gesture): Boolean {
        return bridge.enabledEdgeGestures.any { rule ->
            !rule.isDisabled &&
                    rule.isGestureHapticEnabled() &&
                    rule.gesture == gesture &&
                    rule.gesture.supportsGestureIndicator() &&
                    areaMatchesRule(area, rule)
        }
    }

    private fun areaMatchesRule(area: DetectedArea, rule: EdgeGesture): Boolean {
        return when (area) {
            is DetectedArea.PredefinedEdge -> {
                val hasCustomRect = rule.customRect != null &&
                        rule.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance()
                if (hasCustomRect) {
                    false
                } else {
                    rule.edge == area.edge ||
                            (getParentEdgeRegion(area.edge) != area.edge &&
                                    rule.edge == getParentEdgeRegion(area.edge))
                }
            }

            is DetectedArea.CustomRect -> {
                rule.customRect != null &&
                        rule.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance() &&
                        rule.customRect == area.rect
            }
        }
    }

    private fun getParentEdgeRegion(edge: tornaco.apps.shortx.core.proto.fact.Edge): tornaco.apps.shortx.core.proto.fact.Edge {
        return when (edge) {
            tornaco.apps.shortx.core.proto.fact.Edge.LeftTop,
            tornaco.apps.shortx.core.proto.fact.Edge.LeftCenter,
            tornaco.apps.shortx.core.proto.fact.Edge.LeftBottom -> tornaco.apps.shortx.core.proto.fact.Edge.LeftAll

            tornaco.apps.shortx.core.proto.fact.Edge.RightTop,
            tornaco.apps.shortx.core.proto.fact.Edge.RightCenter,
            tornaco.apps.shortx.core.proto.fact.Edge.RightBottom -> tornaco.apps.shortx.core.proto.fact.Edge.RightAll

            tornaco.apps.shortx.core.proto.fact.Edge.TopLeft,
            tornaco.apps.shortx.core.proto.fact.Edge.TopCenter,
            tornaco.apps.shortx.core.proto.fact.Edge.TopRight -> tornaco.apps.shortx.core.proto.fact.Edge.TopAll

            tornaco.apps.shortx.core.proto.fact.Edge.BottomLeft,
            tornaco.apps.shortx.core.proto.fact.Edge.BottomCenter,
            tornaco.apps.shortx.core.proto.fact.Edge.BottomRight -> tornaco.apps.shortx.core.proto.fact.Edge.BottomAll

            tornaco.apps.shortx.core.proto.fact.Edge.LeftAll,
            tornaco.apps.shortx.core.proto.fact.Edge.RightAll,
            tornaco.apps.shortx.core.proto.fact.Edge.TopAll,
            tornaco.apps.shortx.core.proto.fact.Edge.BottomAll,
            tornaco.apps.shortx.core.proto.fact.Edge.UNRECOGNIZED -> edge
        }
    }

    private fun gestureDirection(gesture: Gesture): Pair<Float, Float>? {
        return when (gesture) {
            Gesture.SwipeFromLeft -> 1f to 0f
            Gesture.SwipeFromRight -> -1f to 0f
            Gesture.SwipeFromTop -> 0f to 1f
            Gesture.SwipeFromBottom -> 0f to -1f
            else -> null
        }
    }

    private fun Gesture.supportsGestureIndicator(): Boolean {
        return when (this) {
            Gesture.SwipeFromTop,
            Gesture.SwipeFromBottom,
            Gesture.SwipeFromLeft,
            Gesture.SwipeFromRight -> true

            else -> false
        }
    }

    private fun EdgeGesture.isGestureHintEnabled(): Boolean {
        return hasEnableGestureHint() && enableGestureHint
    }

    private fun EdgeGesture.isGestureHapticEnabled(): Boolean {
        return hasEnableGestureHaptic() && enableGestureHaptic
    }

    private fun EdgeGesture.isGestureFeedbackEnabled(): Boolean {
        return isGestureHintEnabled() || isGestureHapticEnabled()
    }

    private fun reset() {
        gestureHapticController.onGestureCancel(trackedToken)
        trackedToken = 0L
        trackedPointerId = MotionEvent.INVALID_POINTER_ID
        trackedAreas = emptyList()
        downX = 0f
        downY = 0f
        hasHintShown = false
        hasTriggered = false
    }
}