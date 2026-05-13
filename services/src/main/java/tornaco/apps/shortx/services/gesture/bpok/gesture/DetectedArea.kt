package tornaco.apps.shortx.services.gesture.bpok.gesture

import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture

enum class PredefinedEdgeCoordinateMode {
    RelativeToDisplay,
    AbsolutePortrait,
}

sealed interface DetectedArea {
    data class PredefinedEdge(
        val edge: Edge,
        val coordinateMode: PredefinedEdgeCoordinateMode = PredefinedEdgeCoordinateMode.RelativeToDisplay,
    ) : DetectedArea

    data class CustomRect(val rect: Rect) : DetectedArea
}

fun EdgeGesture.usesAbsolutePredefinedArea(): Boolean {
    val hasCustomRect = hasCustomRect() && customRect != Rect.getDefaultInstance()
    return !hasCustomRect && hasUseAbsoluteArea() && useAbsoluteArea
}