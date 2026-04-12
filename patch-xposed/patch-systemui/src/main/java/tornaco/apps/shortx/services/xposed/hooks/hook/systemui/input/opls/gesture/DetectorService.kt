package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import tornaco.apps.shortx.core.proto.fact.CornerSwipeToCenter
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.ScreenCorner

interface DetectorService {
    fun handleEdgeGestureEvent(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
        downX: Float,
        downY: Float,
    ): Boolean

    fun handleCornerSwipeToCenterEvent(
        corner: ScreenCorner,
        downX: Float,
        downY: Float,
    ): Boolean

    fun getSwipeDistanceThresholdDp(): Int
    fun getGestureAreaWidthDp(): Int
    fun getGestureLengthScale(): Float
    fun getCornerGestureRegionRadiusRatio(): Float
    fun getCornerSwipeMinDistanceDp(): Int

    fun obsGestureSettingsChanged(obs: () -> Unit)
    fun obsConfigChanged(obs: () -> Unit)

    // Rule/DA/PkgSet...
    fun obsUserDataChanged(obs: () -> Unit)

    val enabledEdgeGestures: Set<EdgeGesture>
    val enabledCornerSwipeToCenterFacts: Set<CornerSwipeToCenter>
}
