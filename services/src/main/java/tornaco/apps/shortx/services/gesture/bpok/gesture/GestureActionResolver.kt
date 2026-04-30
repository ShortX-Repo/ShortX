package tornaco.apps.shortx.services.gesture.bpok.gesture

import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.util.Logger

class GestureActionResolver(
    private val service: DetectorService,
    private val regionResolver: EdgeRegionResolver
) {
    private val logger = Logger("GestureActionResolver")

    fun resolve(edge: Edge, gesture: Gesture): EdgeGesture? {
        val direct = findAction(edge, gesture)
        if (direct != null) return direct

        val parent = regionResolver.getParentEdgeRegion(edge)
        if (parent != edge) {
            val fallback = findAction(parent, gesture)
            if (fallback != null) return fallback
        }
        return null
    }

    fun hasAction(edge: Edge, gesture: Gesture): Boolean {
        return resolve(edge, gesture) != null
    }

    private fun findAction(edge: Edge, gesture: Gesture): EdgeGesture? {
        return service.enabledEdgeGestures.firstOrNull { rule ->
            !rule.isDisabled &&
                    rule.gesture == gesture &&
                    rule.childStepsCount == 0 &&
                    rule.edge == edge &&
                    (!rule.hasCustomRect() || rule.customRect == tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance())
        }
    }
}
