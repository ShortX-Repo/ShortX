package tornaco.apps.shortx.services.gesture

import android.annotation.ColorInt
import android.graphics.Color
import android.graphics.RectF
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.util.select

data class HotSeat(val area: Edge, @ColorInt val color: Int)

fun Edge.getRect(
    isPortrait: Boolean,
    screenWidth: Float,
    screenHeight: Float,
    areaSize: Float,
    areaLengthScale: Float
): RectF {
    return isPortrait.select(
        { getRectP(screenWidth, screenHeight, areaSize, areaLengthScale) },
        { getRectL(screenWidth, screenHeight, areaSize, areaLengthScale) })
}

private fun Edge.getRectL(
    screenWidth: Float,
    screenHeight: Float,
    areaSize: Float,
    areaLengthScale: Float
): RectF {
    return getRectP(screenHeight, screenWidth, areaSize, areaLengthScale)
}

private fun Edge.getRectP(
    screenWidth: Float,
    screenHeight: Float,
    areaSize: Float,
    areaLengthScale: Float
): RectF {
    return when (this) {
        Edge.BottomAll -> {
            RectF(
                0f, screenHeight - areaSize, screenWidth, screenHeight
            )
        }

        Edge.BottomCenter -> {
            val splitSize = screenWidth / 3
            val length = splitSize * areaLengthScale
            val left = (screenWidth - length) / 2
            val right = left + length
            val top = screenHeight - areaSize
            val bottom = screenHeight
            RectF(left, top, right, bottom)
        }

        Edge.BottomLeft -> {
            val splitSize = screenWidth / 3
            val length = splitSize * areaLengthScale
            val left = (splitSize - length) / 2
            val right = left + length
            val top = screenHeight - areaSize
            val bottom = screenHeight
            RectF(left, top, right, bottom)
        }

        Edge.BottomRight -> {
            val splitSize = screenWidth / 3
            val length = splitSize * areaLengthScale
            val left = splitSize * 2 + (splitSize - length) / 2
            val right = left + length
            val top = screenHeight - areaSize
            val bottom = screenHeight
            RectF(left, top, right, bottom)
        }

        Edge.LeftAll -> RectF(0f, 0f, areaSize, screenHeight)

        Edge.LeftTop -> {
            val splitSize = screenHeight / 3
            val length = splitSize * areaLengthScale
            val left = 0f
            val right = areaSize
            val top = (splitSize - length) / 2
            val bottom = top + length
            RectF(left, top, right, bottom)
        }

        Edge.LeftCenter -> {
            val splitSize = screenHeight / 3
            val length = splitSize * areaLengthScale
            val left = 0f
            val right = areaSize
            val top = splitSize + (splitSize - length) / 2
            val bottom = top + length
            RectF(left, top, right, bottom)
        }

        Edge.LeftBottom -> {
            val splitSize = screenHeight / 3
            val length = splitSize * areaLengthScale
            val left = 0f
            val right = areaSize
            val top = splitSize * 2 + (splitSize - length) / 2
            val bottom = top + length
            RectF(left, top, right, bottom)
        }


        Edge.RightAll -> RectF(screenWidth - areaSize, 0f, screenWidth, screenHeight)
        Edge.RightCenter -> {
            val splitSize = screenHeight / 3
            val length = splitSize * areaLengthScale
            val left = screenWidth - areaSize
            val right = screenWidth
            val top = splitSize + (splitSize - length) / 2
            val bottom = top + length
            RectF(left, top, right, bottom)
        }

        Edge.RightTop -> {
            val splitSize = screenHeight / 3
            val length = splitSize * areaLengthScale
            val left = screenWidth - areaSize
            val right = screenWidth
            val top = (splitSize - length) / 2
            val bottom = top + length
            RectF(left, top, right, bottom)
        }

        Edge.RightBottom -> {
            val splitSize = screenHeight / 3
            val length = splitSize * areaLengthScale
            val left = screenWidth - areaSize
            val right = screenWidth
            val top = splitSize * 2 + (splitSize - length) / 2
            val bottom = top + length
            RectF(left, top, right, bottom)
        }

        Edge.TopAll -> RectF(0f, 0f, screenWidth, areaSize)
        Edge.TopCenter -> {
            val splitSize = screenWidth / 3
            val length = splitSize * areaLengthScale
            val left = (screenWidth - length) / 2
            val right = left + length
            val top = 0f
            val bottom = areaSize
            RectF(left, top, right, bottom)
        }

        Edge.TopLeft -> {
            val splitSize = screenWidth / 3
            val length = splitSize * areaLengthScale
            val left = (splitSize - length) / 2
            val right = left + length
            val top = 0f
            val bottom = areaSize
            RectF(left, top, right, bottom)
        }

        Edge.TopRight -> {
            val splitSize = screenWidth / 3
            val length = splitSize * areaLengthScale
            val left = splitSize * 2 + (splitSize - length) / 2
            val right = left + length
            val top = 0f
            val bottom = areaSize
            RectF(left, top, right, bottom)
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