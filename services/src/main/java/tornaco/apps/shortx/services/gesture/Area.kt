package tornaco.apps.shortx.services.gesture

import android.annotation.ColorInt
import android.graphics.Color
import android.graphics.RectF
import tornaco.apps.shortx.core.rule.fact.gesture.Area
import tornaco.apps.shortx.services.dp

val defaultHotSeatSize by lazy {
    16.dp
}

data class HotSeat(val area: Area, @ColorInt val color: Int)

fun Area.getRect(
    screenWidth: Float,
    screenHeight: Float,
    areaSize: Float = defaultHotSeatSize.toFloat(),
): RectF {
    return when (this) {
        Area.BottomAll -> {
            RectF(areaSize,
                screenHeight - areaSize,
                screenWidth - areaSize,
                screenHeight)
        }
        Area.BottomCenter -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(areaSize + splitSize,
                screenHeight - areaSize,
                areaSize + splitSize * 2,
                screenHeight)
        }
        Area.BottomLeft -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(areaSize,
                screenHeight - areaSize,
                areaSize + splitSize,
                screenHeight)
        }
        Area.BottomRight -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(areaSize + 2 * splitSize,
                screenHeight - areaSize,
                screenWidth - areaSize,
                screenHeight)
        }

        Area.LeftAll -> RectF()
        Area.LeftTop -> {
            val splitSize = screenHeight / 3
            RectF(0f,
                0f,
                areaSize,
                splitSize)
        }
        Area.LeftCenter -> {
            val splitSize = screenHeight / 3
            RectF(0f,
                splitSize,
                areaSize,
                splitSize * 2)
        }
        Area.LeftBottom -> {
            val splitSize = screenHeight / 3
            RectF(0f,
                splitSize * 2,
                areaSize,
                screenHeight)
        }


        Area.RightAll -> RectF()
        Area.RightCenter -> {
            val splitSize = screenHeight / 3
            RectF(screenWidth - areaSize,
                splitSize,
                screenWidth,
                splitSize * 2)
        }
        Area.RightTop -> {
            val splitSize = screenHeight / 3
            RectF(screenWidth - areaSize,
                0f,
                screenWidth,
                splitSize)
        }
        Area.RightBottom -> {
            val splitSize = screenHeight / 3
            RectF(screenWidth - areaSize,
                splitSize * 2,
                screenWidth,
                screenHeight)
        }

        Area.TopAll -> RectF()
        Area.TopCenter -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(areaSize + splitSize,
                0f,
                areaSize + splitSize * 2,
                areaSize)
        }
        Area.TopLeft -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(areaSize,
                0f,
                areaSize + splitSize,
                areaSize)
        }
        Area.TopRight -> {
            val splitSize = (screenWidth - 2 * areaSize) / 3
            RectF(areaSize + 2 * splitSize,
                0f,
                screenWidth - areaSize,
                areaSize)
        }
    }
}

val allHotSeats = listOf(
    HotSeat(Area.BottomLeft, Color.RED),
    HotSeat(Area.BottomCenter, Color.GREEN),
    HotSeat(Area.BottomRight, Color.BLUE),

    HotSeat(Area.TopLeft, Color.RED),
    HotSeat(Area.TopCenter, Color.GREEN),
    HotSeat(Area.TopRight, Color.BLUE),

    HotSeat(Area.LeftTop, Color.RED),
    HotSeat(Area.LeftCenter, Color.GREEN),
    HotSeat(Area.LeftBottom, Color.BLUE),

    HotSeat(Area.RightTop, Color.RED),
    HotSeat(Area.RightCenter, Color.GREEN),
    HotSeat(Area.RightBottom, Color.BLUE),
)