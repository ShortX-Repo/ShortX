package tornaco.apps.shortx.services.gesture.bpok.gesture

import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.Edge

sealed interface DetectedArea {
    data class PredefinedEdge(val edge: Edge) : DetectedArea
    data class CustomRect(val rect: Rect) : DetectedArea
}