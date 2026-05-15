package tornaco.apps.shortx.core.gesture

import android.graphics.PointF
import android.view.Surface
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.Edge
import kotlin.math.ceil
import kotlin.math.floor

data class DisplayPortraitMetrics(
    val currentWidth: Int,
    val currentHeight: Int,
    val portraitWidth: Float,
    val portraitHeight: Float,
    val rotation: Int,
)

fun buildDisplayPortraitMetrics(
    currentWidth: Int,
    currentHeight: Int,
    rotation: Int,
): DisplayPortraitMetrics {
    return DisplayPortraitMetrics(
        currentWidth = currentWidth,
        currentHeight = currentHeight,
        portraitWidth = minOf(currentWidth, currentHeight).toFloat(),
        portraitHeight = maxOf(currentWidth, currentHeight).toFloat(),
        rotation = rotation,
    )
}

fun toAbsolutePortraitPoint(
    rawX: Float,
    rawY: Float,
    metrics: DisplayPortraitMetrics,
): PointF {
    val mapped = when (metrics.rotation) {
        Surface.ROTATION_90 -> PointF(metrics.portraitWidth - rawY, rawX)
        Surface.ROTATION_180 -> PointF(
            metrics.portraitWidth - rawX,
            metrics.portraitHeight - rawY,
        )

        Surface.ROTATION_270 -> PointF(rawY, metrics.portraitHeight - rawX)
        else -> PointF(rawX, rawY)
    }
    return PointF(
        mapped.x.coerceIn(0f, maxCoordinate(metrics.portraitWidth)),
        mapped.y.coerceIn(0f, maxCoordinate(metrics.portraitHeight)),
    )
}

fun projectPortraitPointToDisplay(
    portraitX: Float,
    portraitY: Float,
    metrics: DisplayPortraitMetrics,
): PointF {
    return when (metrics.rotation) {
        Surface.ROTATION_90 -> PointF(portraitY, metrics.portraitWidth - portraitX)
        Surface.ROTATION_180 -> PointF(
            metrics.portraitWidth - portraitX,
            metrics.portraitHeight - portraitY,
        )

        Surface.ROTATION_270 -> PointF(metrics.portraitHeight - portraitY, portraitX)
        else -> PointF(portraitX, portraitY)
    }
}

fun normalizeRectToPortrait(
    rect: Rect,
    metrics: DisplayPortraitMetrics,
): Rect {
    return transformRect(rect) { x, y ->
        toAbsolutePortraitPoint(x, y, metrics).let { it.x to it.y }
    }
}

fun projectPortraitRectToDisplay(
    rect: Rect,
    metrics: DisplayPortraitMetrics,
): Rect {
    return transformRect(rect) { x, y ->
        projectPortraitPointToDisplay(x, y, metrics).let { it.x to it.y }
    }
}

fun mapPortraitEdgeToDisplayEdge(edge: Edge, rotation: Int): Edge {
    return when (rotation) {
        Surface.ROTATION_90 -> when (edge) {
            Edge.TopAll -> Edge.LeftAll
            Edge.TopLeft -> Edge.LeftBottom
            Edge.TopCenter -> Edge.LeftCenter
            Edge.TopRight -> Edge.LeftTop
            Edge.RightAll -> Edge.TopAll
            Edge.RightTop -> Edge.TopLeft
            Edge.RightCenter -> Edge.TopCenter
            Edge.RightBottom -> Edge.TopRight
            Edge.BottomAll -> Edge.RightAll
            Edge.BottomLeft -> Edge.RightBottom
            Edge.BottomCenter -> Edge.RightCenter
            Edge.BottomRight -> Edge.RightTop
            Edge.LeftAll -> Edge.BottomAll
            Edge.LeftTop -> Edge.BottomLeft
            Edge.LeftCenter -> Edge.BottomCenter
            Edge.LeftBottom -> Edge.BottomRight
            else -> edge
        }

        Surface.ROTATION_180 -> when (edge) {
            Edge.TopAll -> Edge.BottomAll
            Edge.TopLeft -> Edge.BottomRight
            Edge.TopCenter -> Edge.BottomCenter
            Edge.TopRight -> Edge.BottomLeft
            Edge.RightAll -> Edge.LeftAll
            Edge.RightTop -> Edge.LeftBottom
            Edge.RightCenter -> Edge.LeftCenter
            Edge.RightBottom -> Edge.LeftTop
            Edge.BottomAll -> Edge.TopAll
            Edge.BottomLeft -> Edge.TopRight
            Edge.BottomCenter -> Edge.TopCenter
            Edge.BottomRight -> Edge.TopLeft
            Edge.LeftAll -> Edge.RightAll
            Edge.LeftTop -> Edge.RightBottom
            Edge.LeftCenter -> Edge.RightCenter
            Edge.LeftBottom -> Edge.RightTop
            else -> edge
        }

        Surface.ROTATION_270 -> when (edge) {
            Edge.TopAll -> Edge.RightAll
            Edge.TopLeft -> Edge.RightTop
            Edge.TopCenter -> Edge.RightCenter
            Edge.TopRight -> Edge.RightBottom
            Edge.RightAll -> Edge.BottomAll
            Edge.RightTop -> Edge.BottomRight
            Edge.RightCenter -> Edge.BottomCenter
            Edge.RightBottom -> Edge.BottomLeft
            Edge.BottomAll -> Edge.LeftAll
            Edge.BottomLeft -> Edge.LeftTop
            Edge.BottomCenter -> Edge.LeftCenter
            Edge.BottomRight -> Edge.LeftBottom
            Edge.LeftAll -> Edge.TopAll
            Edge.LeftTop -> Edge.TopRight
            Edge.LeftCenter -> Edge.TopCenter
            Edge.LeftBottom -> Edge.TopLeft
            else -> edge
        }

        else -> edge
    }
}

private fun transformRect(
    rect: Rect,
    transform: (Float, Float) -> Pair<Float, Float>,
): Rect {
    val points = listOf(
        transform(rect.left.toFloat(), rect.top.toFloat()),
        transform(rect.right.toFloat(), rect.top.toFloat()),
        transform(rect.left.toFloat(), rect.bottom.toFloat()),
        transform(rect.right.toFloat(), rect.bottom.toFloat()),
    )
    val left = floor(points.minOf { it.first }.toDouble()).toInt()
    val top = floor(points.minOf { it.second }.toDouble()).toInt()
    val right = ceil(points.maxOf { it.first }.toDouble()).toInt()
    val bottom = ceil(points.maxOf { it.second }.toDouble()).toInt()
    return Rect.newBuilder()
        .setLeft(left)
        .setTop(top)
        .setRight(right)
        .setBottom(bottom)
        .build()
}

private fun maxCoordinate(size: Float): Float {
    return if (size <= 1f) 0f else size - 1f
}
