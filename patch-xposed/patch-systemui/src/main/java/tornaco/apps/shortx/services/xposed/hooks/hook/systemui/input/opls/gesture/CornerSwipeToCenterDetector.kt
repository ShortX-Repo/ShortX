package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.content.Context
import android.view.MotionEvent
import tornaco.apps.shortx.core.proto.fact.ScreenCorner
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.screenSize
import kotlin.math.cos
import kotlin.math.hypot

class CornerSwipeToCenterDetector(
    private val context: Context,
    private val service: DetectorService,
    private val gestureHintController: GestureHintController,
) {
    companion object {
        private const val CORNER_SWIPE_MAX_ANGLE_DEVIATION_DEG = 30f
        private const val CORNER_HINT_MAX_ANGLE_DEVIATION_DEG = 50f
        private const val GESTURE_HINT_SHOW_DISTANCE_DP = 12
        private const val GESTURE_HINT_MAX_VISUAL_DISTANCE_DP = 72
        private const val GESTURE_HINT_READY_PROGRESS = 0.72f
        private val MIN_DIRECTION_DOT =
            cos(Math.toRadians(CORNER_SWIPE_MAX_ANGLE_DEVIATION_DEG.toDouble())).toFloat()
        private val MIN_HINT_DIRECTION_DOT =
            cos(Math.toRadians(CORNER_HINT_MAX_ANGLE_DEVIATION_DEG.toDouble())).toFloat()
    }

    private var nextToken = 1L
    private var trackedToken = 0L
    private var trackedPointerId = MotionEvent.INVALID_POINTER_ID
    private var trackedCorner: ScreenCorner? = null
    private var downX = 0f
    private var downY = 0f
    private var hasTriggered = false
    private var hasHintShown = false

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                reset()
                trackedPointerId = event.getPointerId(event.actionIndex)
                downX = event.x
                downY = event.y
                trackedCorner = detectCorner(downX, downY)
                trackedToken = if (trackedCorner != null) nextToken++ else 0L
            }

            MotionEvent.ACTION_MOVE -> {
                val corner = trackedCorner ?: return false
                if (hasTriggered) return false

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

                val (expectedX, expectedY) = expectedDirection(corner)
                val normalizedX = deltaX / distance
                val normalizedY = deltaY / distance
                val dot = normalizedX * expectedX + normalizedY * expectedY

                handleGestureHint(corner, distance, dot)

                if (distance < service.getCornerSwipeMinDistanceDp()._dp) {
                    return false
                }
                if (dot < MIN_DIRECTION_DOT) {
                    return false
                }

                val handled = service.handleCornerSwipeToCenterEvent(corner, downX, downY)
                if (handled) {
                    gestureHintController.onGestureSuccess(trackedToken)
                }
                hasTriggered = handled
                return handled
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

    private fun handleGestureHint(corner: ScreenCorner, distance: Float, dot: Float) {
        if (trackedToken == 0L) return
        val shouldShowHint = service.enabledCornerSwipeToCenterFacts.any {
            !it.isDisabled && it.corner == corner && it.enableGestureHint
        }
        if (!shouldShowHint) {
            if (hasHintShown) {
                gestureHintController.onGestureCancel(trackedToken)
                hasHintShown = false
            }
            return
        }
        val showThresholdPx = GESTURE_HINT_SHOW_DISTANCE_DP._dp.toFloat()
        if (distance < showThresholdPx) {
            return
        }

        if (dot < MIN_HINT_DIRECTION_DOT) {
            if (hasHintShown) {
                gestureHintController.onGestureCancel(trackedToken)
                hasHintShown = false
            }
            return
        }

        val progress = ((distance - showThresholdPx) /
                (GESTURE_HINT_MAX_VISUAL_DISTANCE_DP._dp.toFloat() - showThresholdPx)).coerceIn(0f, 1f)
        val state = if (progress >= GESTURE_HINT_READY_PROGRESS && dot >= MIN_DIRECTION_DOT) {
            GestureHintState.Ready
        } else {
            GestureHintState.Showing
        }
        gestureHintController.onGestureProgress(
            GestureHintModel(
                token = trackedToken,
                anchorX = downX,
                anchorY = downY,
                directionX = expectedDirection(corner).first,
                directionY = expectedDirection(corner).second,
                progress = progress,
                state = state,
            )
        )
        hasHintShown = true
    }

    private fun detectCorner(x: Float, y: Float): ScreenCorner? {
        val enabledCorners = service.enabledCornerSwipeToCenterFacts
            .asSequence()
            .filter { !it.isDisabled }
            .map { it.corner }
            .toSet()
        if (enabledCorners.isEmpty()) return null

        val screenSize = context.screenSize()
        val width = screenSize.x.toFloat()
        val height = screenSize.y.toFloat()
        val radiusRatio = service.getCornerGestureRegionRadiusRatio()

        return enabledCorners.firstOrNull { corner ->
            CornerSwipeToCenterGeometry.contains(corner, x, y, width, height, radiusRatio)
        }
    }

    private fun expectedDirection(corner: ScreenCorner): Pair<Float, Float> {
        val normalized = 0.70710677f
        return when (corner) {
            ScreenCorner.CornerTopLeft -> normalized to normalized
            ScreenCorner.CornerTopRight -> -normalized to normalized
            ScreenCorner.CornerBottomLeft -> normalized to -normalized
            ScreenCorner.CornerBottomRight -> -normalized to -normalized
            else -> 0f to 0f
        }
    }

    private fun cornerPoint(corner: ScreenCorner, width: Float, height: Float): Pair<Float, Float> {
        return CornerSwipeToCenterGeometry.cornerPoint(corner, width, height)
    }

    private fun reset() {
        trackedToken = 0L
        trackedPointerId = MotionEvent.INVALID_POINTER_ID
        trackedCorner = null
        downX = 0f
        downY = 0f
        hasTriggered = false
        hasHintShown = false
    }
}