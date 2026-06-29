package tornaco.apps.shortx.services.gesture.bpok.gesture

import tornaco.apps.shortx.core.proto.action.GesturePointerDragButton
import tornaco.apps.shortx.core.proto.action.GesturePointerReleaseButton
import tornaco.apps.shortx.core.proto.action.GesturePointerScrollMode
import tornaco.apps.shortx.core.proto.action.PieMenuItem
import tornaco.apps.shortx.core.proto.fact.CornerSwipeToCenter
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStep
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.ScreenCorner

data class EdgeGestureSequenceCandidate(
    val area: DetectedArea,
    val rule: EdgeGesture,
)

sealed interface ContinuousGestureActionPayload {
    data class Brightness(
        val sensitivityScale: Float,
        val minValue: Int,
        val maxValue: Int,
    ) : ContinuousGestureActionPayload

    data class Pointer(
        val sensitivityScale: Float,
        val releaseButton: GesturePointerReleaseButton,
        val dragButton: GesturePointerDragButton,
        val dragHoldDurationMs: Int,
        val scrollMode: GesturePointerScrollMode,
        val scrollScale: Float,
        val cancelRegionRadiusDp: Int,
        val enableCenterActionSwitch: Boolean,
    ) : ContinuousGestureActionPayload

    data class Volume(
        val streamType: Int,
        val sensitivityScale: Float,
    ) : ContinuousGestureActionPayload

    data class PieMenu(
        val items: List<PieMenuItem>,
        val pieColor: Int,
        val iconColor: Int,
        val scalePercent: Int,
        val arcSpanDegrees: Float,
    ) : ContinuousGestureActionPayload
}

data class ContinuousGestureBinding(
    val rule: EdgeGesture,
    val payload: ContinuousGestureActionPayload,
)

interface GestureBridge {
    fun getAreaGestureProfile(areas: List<DetectedArea>): AreaGestureProfile

    fun shouldDisableForIME(areas: List<DetectedArea>): Boolean

    fun handleEdgeGestureEvent(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
        downX: Float,
        downY: Float,
        coordinateMode: PredefinedEdgeCoordinateMode? = null,
    ): Boolean

    fun findMatchingEdgeGestureSequences(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
        coordinateMode: PredefinedEdgeCoordinateMode? = null,
    ): List<EdgeGestureSequenceCandidate>

    fun fireEdgeGestureSequence(
        area: DetectedArea,
        gesture: Gesture,
        childSteps: List<EdgeGestureStep>,
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
    fun getEdgeGestureSequenceHoverDurationMs(): Int
    fun getEdgeGestureSequenceHoverRadiusDp(): Int
    fun getEdgeGestureSequenceChildSwipeMinDistanceDp(): Int
    fun getEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(): Int
    fun isEdgeGestureSequenceChildSwipeTriggerOnRelease(): Boolean

    fun obsGestureSettingsChanged(obs: () -> Unit)
    fun obsConfigChanged(obs: () -> Unit)

    // Rule/DA/PkgSet...
    fun obsUserDataChanged(obs: () -> Unit)

    fun findContinuousGestureBinding(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
        coordinateMode: PredefinedEdgeCoordinateMode? = null,
    ): ContinuousGestureBinding?

    fun createContinuousActionHandler(payload: ContinuousGestureActionPayload): ContinuousActionHandler?

    val enabledEdgeGestures: Set<EdgeGesture>
    val enabledCornerSwipeToCenterFacts: Set<CornerSwipeToCenter>
}

data class AreaGestureProfile(
    val hasAnyTrigger: Boolean,
    val hasTapTrigger: Boolean,
    val hasLongPressTrigger: Boolean,
    val hasSwipeTrigger: Boolean,
) {
    val shouldCaptureOnDown: Boolean
        get() = hasAnyTrigger
}
