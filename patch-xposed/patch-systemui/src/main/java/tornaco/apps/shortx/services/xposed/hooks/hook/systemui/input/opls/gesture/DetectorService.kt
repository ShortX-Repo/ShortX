package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture

interface DetectorService {
    fun handleEdgeGestureEvent(areas: List<DetectedArea>, gestureEvent: Gesture): Boolean
    fun getSwipeDistanceThresholdDp(): Int
    fun getGestureAreaWidthDp(): Int
    fun getGestureLengthScale(): Float

    fun obsGestureSettingsChanged(obs: () -> Unit)
    fun obsConfigChanged(obs: () -> Unit)

    val enabledEdgeGestures: Set<EdgeGesture>
}
