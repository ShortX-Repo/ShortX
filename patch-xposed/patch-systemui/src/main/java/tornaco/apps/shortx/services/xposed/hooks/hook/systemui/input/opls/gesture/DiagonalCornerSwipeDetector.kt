import android.view.MotionEvent
import kotlin.math.abs
import kotlin.math.atan2

class SystemCornerSwipeDetector(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val edgeSizePx: Float = 80f,
    private val thresholdPx: Float = 120f,
    private val angleToleranceDeg: Float = 30f,
    private val listener: (Corner) -> Boolean  // 返回 true 表示拦截
) {
    enum class Corner { LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM }

    private var startX = 0f
    private var startY = 0f
    private var activeCorner: Corner? = null
    private var tracking = false
    private var consumed = false
    private var triggered = false  // 避免重复触发

    /**
     * @return true 表示事件已消费
     */
    fun onMotionEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                reset()
                startX = ev.x
                startY = ev.y
                activeCorner = detectCorner(startX, startY)
                tracking = activeCorner != null
            }

            MotionEvent.ACTION_MOVE -> {
                if (tracking && !triggered && activeCorner != null) {
                    val dx = ev.x - startX
                    val dy = ev.y - startY
                    val dist = kotlin.math.hypot(dx.toDouble(), dy.toDouble())
                    if (dist >= thresholdPx) {
                        val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                        if (isValidDiagonal(activeCorner!!, angle)) {
                            // 第一次触发
                            consumed = listener(activeCorner!!)
                            triggered = true
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                reset()
            }
        }
        return consumed
    }

    private fun detectCorner(x: Float, y: Float): Corner? {
        return when {
            x <= edgeSizePx && y <= edgeSizePx -> Corner.LEFT_TOP
            x >= screenWidth - edgeSizePx && y <= edgeSizePx -> Corner.RIGHT_TOP
            x <= edgeSizePx && y >= screenHeight - edgeSizePx -> Corner.LEFT_BOTTOM
            x >= screenWidth - edgeSizePx && y >= screenHeight - edgeSizePx -> Corner.RIGHT_BOTTOM
            else -> null
        }
    }

    private fun isValidDiagonal(corner: Corner, angle: Float): Boolean {
        val target = when (corner) {
            Corner.LEFT_TOP -> 45f
            Corner.RIGHT_TOP -> 135f
            Corner.RIGHT_BOTTOM -> -135f
            Corner.LEFT_BOTTOM -> -45f
        }
        val diff = angleDiff(angle, target)
        return diff <= angleToleranceDeg
    }

    private fun angleDiff(a: Float, b: Float): Float {
        var d = abs(a - b) % 360f
        if (d > 180f) d = 360f - d
        return d
    }

    private fun reset() {
        tracking = false
        activeCorner = null
        consumed = false
        triggered = false
    }
}
