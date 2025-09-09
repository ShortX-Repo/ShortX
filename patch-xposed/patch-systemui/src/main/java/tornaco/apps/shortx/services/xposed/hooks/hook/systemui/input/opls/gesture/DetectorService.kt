package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture

interface DetectorService {
    fun handleEdgeGestureEvent(area: Array<Edge>, gestureEvent: Gesture): Boolean
    fun handleScreenGestureEvent(downX: Float, downY: Float, gestureEvent: Gesture)
    fun getSwipeDistanceThresholdDp(): Int
    fun getGestureAreaWidthDp(): Int
    fun getGestureLengthScale(): Float

    fun obsGestureSettingsChanged(obs: () -> Unit)
    fun obsConfigChanged(obs: () -> Unit)
}
