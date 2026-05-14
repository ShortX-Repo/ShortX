package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.graphics.Point
import android.graphics.PointF
import android.view.Surface
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp

class EdgeRegionResolver(
    private val bridge: GestureBridge,
    private val getScreenSize: () -> Point,
    private val getDisplayRotation: () -> Int,
) {
    private val logger = Logger("EdgeRegionResolver")

    companion object {
        private const val DIAG_PREFIX = "[SX_EDGE_DIAG]"
    }

    fun detectAreas(rawX: Float, rawY: Float): List<DetectedArea> {
        val detectedAreas = mutableListOf<DetectedArea>()
        val seenCustomRects = linkedSetOf<Rect>()

        bridge.enabledEdgeGestures
            .asSequence()
            .filter { it.hasCustomRect() && it.customRect != Rect.getDefaultInstance() }
            .map { it.customRect }
            .distinctBy { listOf(it.left, it.top, it.right, it.bottom) }
            .sortedWith(compareBy({ it.left }, { it.top }, { it.right }, { it.bottom }))
            .forEach { rect ->
                if (rect.toAndroidRect().contains(rawX.toInt(), rawY.toInt()) && seenCustomRects.add(rect)) {
                    detectedAreas.add(DetectedArea.CustomRect(rect))
                }
            }

        // 预置区域要同时产出两套命中结果：
        // 1. 跟随当前屏幕方向的相对区域。
        // 2. 固定竖屏语义的绝对区域。
        // 这样每条规则都可以按自己的开关选择匹配哪一套区域。
        val relativeEdge = resolveRelativePredefinedEdge(rawX, rawY)
        if (relativeEdge != Edge.UNRECOGNIZED) {
            detectedAreas.add(
                DetectedArea.PredefinedEdge(
                    edge = relativeEdge,
                    coordinateMode = PredefinedEdgeCoordinateMode.RelativeToDisplay,
                )
            )
        }

        val absoluteEdge = resolveAbsolutePredefinedEdge(rawX, rawY)
        if (absoluteEdge != Edge.UNRECOGNIZED) {
            detectedAreas.add(
                DetectedArea.PredefinedEdge(
                    edge = absoluteEdge,
                    coordinateMode = PredefinedEdgeCoordinateMode.AbsolutePortrait,
                )
            )
        }

        if (relativeEdge != Edge.UNRECOGNIZED || absoluteEdge != Edge.UNRECOGNIZED) {
            val screenSize = getScreenSize()
            logger.d(
                "$DIAG_PREFIX detectAreas: raw=($rawX,$rawY), rotation=${rotationName(getDisplayRotation())}, screen=${screenSize.x}x${screenSize.y}, relative=$relativeEdge, absolute=$absoluteEdge"
            )
        }

        return detectedAreas
    }

    fun resolve(rawX: Float, rawY: Float): Edge {
        return resolveRelativePredefinedEdge(rawX, rawY)
    }

    fun toAbsolutePortraitPoint(rawX: Float, rawY: Float): PointF {
        val portraitMetrics = getPortraitMetrics()
        val mapped = when (portraitMetrics.rotation) {
            // 这里要按当前 rotation 做“逆变换”，把横屏坐标还原回竖屏坐标。
            // 从日志可见，ROTATION_270 下横屏底边中部被错误映射到了竖屏左边，
            // 说明 90/270 两个逆变换分支仍然写反了。这里按真实旋转方向恢复：
            // ROTATION_90: 竖屏右边 -> 横屏上边；ROTATION_270: 竖屏右边 -> 横屏下边。
            Surface.ROTATION_90 -> PointF(portraitMetrics.width - rawY, rawX)
            Surface.ROTATION_180 -> PointF(
                portraitMetrics.width - rawX,
                portraitMetrics.height - rawY
            )

            Surface.ROTATION_270 -> PointF(rawY, portraitMetrics.height - rawX)
            else -> PointF(rawX, rawY)
        }
        val clamped = PointF(
            mapped.x.coerceIn(0f, maxCoordinate(portraitMetrics.width)),
            mapped.y.coerceIn(0f, maxCoordinate(portraitMetrics.height)),
        )
        logger.d(
            "$DIAG_PREFIX toAbsolutePortraitPoint: raw=($rawX,$rawY), rotation=${rotationName(portraitMetrics.rotation)}, portrait=${portraitMetrics.width}x${portraitMetrics.height}, mapped=(${clamped.x},${clamped.y})"
        )
        return clamped
    }

    private fun resolveRelativePredefinedEdge(rawX: Float, rawY: Float): Edge {
        val screenSize = getScreenSize()
        return resolvePredefinedEdge(
            point = PointF(rawX, rawY),
            screenWidth = screenSize.x.toFloat(),
            screenHeight = screenSize.y.toFloat(),
            isPortrait = isCurrentDisplayPortrait(),
        )
    }

    private fun resolveAbsolutePredefinedEdge(rawX: Float, rawY: Float): Edge {
        val portraitPoint = toAbsolutePortraitPoint(rawX, rawY)
        val portraitMetrics = getPortraitMetrics()
        return resolvePredefinedEdge(
            point = portraitPoint,
            screenWidth = portraitMetrics.width,
            screenHeight = portraitMetrics.height,
            isPortrait = true,
        )
    }

    private fun resolvePredefinedEdge(
        point: PointF,
        screenWidth: Float,
        screenHeight: Float,
        isPortrait: Boolean,
    ): Edge {
        val areaSize = bridge.getGestureAreaWidthDp()._dp.toFloat()
        val areaLengthScale = bridge.getGestureLengthScale()

        val edges = Edge.entries.filter { it != Edge.UNRECOGNIZED }
        for (edge in edges) {
            val rect = edge.getRect(
                isPortrait = isPortrait,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                areaSize = areaSize,
                areaLengthScale = areaLengthScale
            )
            if (rect.contains(point.x, point.y)) {
                return edge
            }
        }
        return Edge.UNRECOGNIZED
    }

    private fun getPortraitMetrics(): PortraitMetrics {
        val screenSize = getScreenSize()
        val screenWidth = screenSize.x.toFloat()
        val screenHeight = screenSize.y.toFloat()
        return PortraitMetrics(
            width = minOf(screenWidth, screenHeight),
            height = maxOf(screenWidth, screenHeight),
            rotation = getDisplayRotation(),
        )
    }

    private fun isCurrentDisplayPortrait(): Boolean {
        // 相对区域模式要跟随当前屏幕方向变化，
        // 这里显式区分横竖屏，避免横屏时仍然套用竖屏矩形。
        return when (getDisplayRotation()) {
            Surface.ROTATION_90, Surface.ROTATION_270 -> false
            else -> true
        }
    }

    private fun maxCoordinate(size: Float): Float {
        return if (size <= 1f) 0f else size - 0.001f
    }

    private fun rotationName(rotation: Int): String {
        return when (rotation) {
            Surface.ROTATION_0 -> "ROTATION_0"
            Surface.ROTATION_90 -> "ROTATION_90"
            Surface.ROTATION_180 -> "ROTATION_180"
            Surface.ROTATION_270 -> "ROTATION_270"
            else -> "ROTATION_UNKNOWN($rotation)"
        }
    }

    private data class PortraitMetrics(
        val width: Float,
        val height: Float,
        val rotation: Int,
    )

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
