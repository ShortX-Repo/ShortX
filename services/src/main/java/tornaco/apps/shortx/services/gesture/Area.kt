package tornaco.apps.shortx.services.gesture

import android.annotation.ColorInt
import android.graphics.Color
import android.graphics.RectF
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.services.dp

val defaultHotSeatSize by lazy {
    16.dp
}

data class HotSeat(val area: Edge, @ColorInt val color: Int)

fun Edge.getRect(
    screenWidth: Float,
    screenHeight: Float,
    areaSize: Float = defaultHotSeatSize.toFloat(),
): RectF {
    return when (this) {
        Edge.BottomAll -> {
            RectF(
                areaSize, screenHeight - areaSize, screenWidth - areaSize, screenHeight
            )
        }
        Edge.BottomCenter -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(
                areaSize + splitSize,
                screenHeight - areaSize,
                areaSize + splitSize * 2,
                screenHeight
            )
        }
        Edge.BottomLeft -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(
                areaSize, screenHeight - areaSize, areaSize + splitSize, screenHeight
            )
        }
        Edge.BottomRight -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(
                areaSize + 2 * splitSize,
                screenHeight - areaSize,
                screenWidth - areaSize,
                screenHeight
            )
        }

        Edge.LeftAll -> RectF()
        Edge.LeftTop -> {
            val splitSize = screenHeight / 3
            RectF(
                0f, 0f, areaSize, splitSize
            )
        }
        Edge.LeftCenter -> {
            val splitSize = screenHeight / 3
            RectF(
                0f, splitSize, areaSize, splitSize * 2
            )
        }
        Edge.LeftBottom -> {
            val splitSize = screenHeight / 3
            RectF(
                0f, splitSize * 2, areaSize, screenHeight
            )
        }


        Edge.RightAll -> RectF()
        Edge.RightCenter -> {
            val splitSize = screenHeight / 3
            RectF(
                screenWidth - areaSize, splitSize, screenWidth, splitSize * 2
            )
        }
        Edge.RightTop -> {
            val splitSize = screenHeight / 3
            RectF(
                screenWidth - areaSize, 0f, screenWidth, splitSize
            )
        }
        Edge.RightBottom -> {
            val splitSize = screenHeight / 3
            RectF(
                screenWidth - areaSize, splitSize * 2, screenWidth, screenHeight
            )
        }

        Edge.TopAll -> RectF()
        Edge.TopCenter -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(
                areaSize + splitSize, 0f, areaSize + splitSize * 2, areaSize
            )
        }
        Edge.TopLeft -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(
                areaSize, 0f, areaSize + splitSize, areaSize
            )
        }
        Edge.TopRight -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(
                areaSize + 2 * splitSize, 0f, screenWidth - areaSize, areaSize
            )
        }
        Edge.UNRECOGNIZED -> {
            RectF()
        }
    }
}

val allHotSeats = listOf(
    HotSeat(Edge.BottomLeft, Color.RED),
    HotSeat(Edge.BottomCenter, Color.GREEN),
    HotSeat(Edge.BottomRight, Color.BLUE),

    HotSeat(Edge.TopLeft, Color.RED),
    HotSeat(Edge.TopCenter, Color.GREEN),
    HotSeat(Edge.TopRight, Color.BLUE),

    HotSeat(Edge.LeftTop, Color.RED),
    HotSeat(Edge.LeftCenter, Color.GREEN),
    HotSeat(Edge.LeftBottom, Color.BLUE),

    HotSeat(Edge.RightTop, Color.RED),
    HotSeat(Edge.RightCenter, Color.GREEN),
    HotSeat(Edge.RightBottom, Color.BLUE),
)