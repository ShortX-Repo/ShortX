package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.graphics.Point
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp

class EdgeRegionResolver(
    private val bridge: GestureBridge,
    private val getScreenSize: () -> Point,
) {
    private val logger = Logger("EdgeRegionResolver")

    fun resolve(rawX: Float, rawY: Float): Edge {
        val screenSize = getScreenSize()
        val areaSize = bridge.getGestureAreaWidthDp()._dp.toFloat()
        val areaLengthScale = bridge.getGestureLengthScale()
        val sw = screenSize.x.toFloat()
        val sh = screenSize.y.toFloat()

        val edges = Edge.entries.filter { it != Edge.UNRECOGNIZED }
        for (edge in edges) {
            val rect = edge.getRect(
                isPortrait = true,
                screenWidth = sw,
                screenHeight = sh,
                areaSize = areaSize,
                areaLengthScale = areaLengthScale
            )
            if (rect.contains(rawX, rawY)) {
                return edge
            }
        }
        return Edge.UNRECOGNIZED
    }

    fun getParentEdgeRegion(edge: Edge): Edge {
        return when (edge) {
            Edge.LeftTop, Edge.LeftCenter, Edge.LeftBottom -> Edge.LeftAll
            Edge.RightTop, Edge.RightCenter, Edge.RightBottom -> Edge.RightAll
            Edge.TopLeft, Edge.TopCenter, Edge.TopRight -> Edge.TopAll
            Edge.BottomLeft, Edge.BottomCenter, Edge.BottomRight -> Edge.BottomAll
            else -> edge
        }
    }
}
