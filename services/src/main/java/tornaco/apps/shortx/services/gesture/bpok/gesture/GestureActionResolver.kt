package tornaco.apps.shortx.services.gesture.bpok.gesture

import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.util.Logger

class GestureActionResolver(
    private val bridge: GestureBridge,
    private val regionResolver: EdgeRegionResolver
) {
    private val logger = Logger("GestureActionResolver")

    fun resolve(areas: List<DetectedArea>, gesture: Gesture): EdgeGesture? {
        areas.forEach { area ->
            val direct = findAction(area, gesture)
            if (direct != null) return direct

            if (area is DetectedArea.PredefinedEdge) {
                val parent = regionResolver.getParentEdgeRegion(area.edge)
                if (parent != area.edge) {
                    val fallback = findAction(DetectedArea.PredefinedEdge(parent), gesture)
                    if (fallback != null) return fallback
                }
            }
        }
        return null
    }

    fun hasAction(areas: List<DetectedArea>, gesture: Gesture): Boolean {
        return resolve(areas, gesture) != null
    }

    private fun findAction(area: DetectedArea, gesture: Gesture): EdgeGesture? {
        return bridge.enabledEdgeGestures.firstOrNull { rule ->
            !rule.isDisabled &&
                    rule.gesture == gesture &&
                    rule.childStepsCount == 0 &&
                    areaMatchesRule(area, rule)
        }
    }

    private fun areaMatchesRule(area: DetectedArea, rule: EdgeGesture): Boolean {
        return when (area) {
            is DetectedArea.PredefinedEdge -> {
                rule.edge == area.edge &&
                        (!rule.hasCustomRect() || rule.customRect == tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance())
            }

            is DetectedArea.CustomRect -> {
                rule.hasCustomRect() &&
                        rule.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance() &&
                        rule.customRect == area.rect
            }
        }
    }
}
