package tornaco.apps.shortx.services.gesture

import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture

interface DetectorService {
    fun enabledEdgeGestures(): Set<EdgeGesture>

    fun handleEdgeGestureEvent(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
        downX: Float,
        downY: Float,
    ): Boolean

    fun getGestureAreaWidthDp(): Int
    fun getGestureLengthScale(): Float
    fun getSwipeDistanceThresholdDp(): Int
}
