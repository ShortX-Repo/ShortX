package tornaco.apps.shortx.services.gesture.bpok.gesture

import tornaco.apps.shortx.core.util.Logger
import kotlin.math.abs

class GestureDirectionResolver {
    private val logger = Logger("GestureDirectionResolver")

    fun resolve(
        startX: Float,
        startY: Float,
        currentX: Float,
        currentY: Float,
        minThresholdPx: Float
    ): Direction {
        val dx = currentX - startX
        val dy = currentY - startY
        val distSq = dx * dx + dy * dy
        if (distSq < minThresholdPx * minThresholdPx) {
            return Direction.UNKNOWN
        }
        return if (abs(dx) > abs(dy)) {
            if (dx < 0) Direction.LEFT else Direction.RIGHT
        } else {
            if (dy < 0) Direction.UP else Direction.DOWN
        }
    }
}
