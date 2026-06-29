package tornaco.apps.shortx.services.ui.pie

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.Property
import android.view.HapticFeedbackConstants
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

enum class PieEdge(val startAngleDeg: Float) {
    LEFT(-80f),
    RIGHT(100f),
    TOP(10f),
    BOTTOM(190f);

    val isReversed: Boolean get() = this == RIGHT || this == TOP
}

class PieMenuArcView(
    context: Context,
    private val edge: PieEdge,
    private val centerX: Float,
    private val centerY: Float,
    private val itemDrawables: List<Drawable?>,
    private val itemLabels: List<String>,
    pieColor: Int,
    private val iconColor: Int,
    scalePercent: Int,
    private val arcSpanDegrees: Float,
) : View(context) {

    companion object {
        private const val ANIMATION_DURATION_MS = 100L
        private const val BAND_WIDTH_RATIO = 0.4f
        private const val ICON_SIZE_RATIO = 0.092f
        private const val GAP_WIDTH_RATIO = 0.02f
        private const val OUTER_RADIUS_RATIO = 0.93f
    }

    private val density = resources.displayMetrics.density

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = pieColor
    }

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = adjustBrightness(pieColor)
    }

    private val gapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }

    private val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 11f * density
        typeface = Typeface.DEFAULT_BOLD
    }

    private val itemCount: Int = itemDrawables.size.coerceAtLeast(1)
    private val itemAngle: Float = arcSpanDegrees / itemCount
    private val startAngle: Float = edge.startAngleDeg

    private val fM: Float
    private val bandWidth: Int
    private val iconHalf: Int
    private val gapWidth: Int
    private val outerRadius: Int
    private val innerRadius: Int
    private val midRadius: Int

    private val arcRect: RectF
    private val iconCenters: FloatArray

    private var selectedItem: Int = -1
    private var animProgress: Float = 1f
    private var cancelled: Boolean = false
    private var lastSelectedItem: Int = -1

    private val animProperty = object : Property<PieMenuArcView, Float>(Float::class.java, "animProgress") {
        override fun get(obj: PieMenuArcView): Float = obj.animProgress
        override fun set(obj: PieMenuArcView, value: Float) {
            obj.animProgress = value
            obj.invalidate()
        }
    }

    init {
        val displayMetric = min(
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels,
        ).toFloat()
        fM = displayMetric * scalePercent.coerceIn(75, 200) / 200f

        bandWidth = (BAND_WIDTH_RATIO * fM + 0.5f).toInt()
        iconHalf = (ICON_SIZE_RATIO * fM + 0.5f).toInt()
        gapWidth = (GAP_WIDTH_RATIO * fM + 0.5f).toInt()
        outerRadius = (OUTER_RADIUS_RATIO * fM + 0.5f).toInt()
        innerRadius = outerRadius - bandWidth
        midRadius = outerRadius - bandWidth / 2

        val strokeWidth = (bandWidth - gapWidth).toFloat()
        arcPaint.strokeWidth = strokeWidth
        highlightPaint.strokeWidth = strokeWidth
        gapPaint.strokeWidth = gapWidth.toFloat()

        val mid = midRadius.toFloat()
        arcRect = RectF(centerX - mid, centerY - mid, centerX + mid, centerY + mid)

        iconCenters = FloatArray(itemCount * 2)
        val firstCenter = startAngle + itemAngle / 2f
        for (i in 0 until itemCount) {
            val angleDeg = firstCenter + itemAngle * i
            val angleRad = Math.toRadians(angleDeg.toDouble())
            iconCenters[i * 2] = centerX + (cos(angleRad) * midRadius).toFloat()
            iconCenters[i * 2 + 1] = centerY + (sin(angleRad) * midRadius).toFloat()
        }

        if (iconColor != 0) {
            itemDrawables.forEach { it?.setTint(iconColor) }
        }

        setLayerType(LAYER_TYPE_HARDWARE, null)
        setKeepScreenOn(true)

        animProgress = 0f
        ObjectAnimator.ofFloat(this, animProperty, 0f, 1f).apply {
            duration = ANIMATION_DURATION_MS
            start()
        }
    }

    fun hitTest(touchX: Float, touchY: Float): Boolean {
        val dx = touchX - centerX
        val dy = touchY - centerY
        val distSq = dx * dx + dy * dy

        val inner = innerRadius.toFloat()
        val outer = outerRadius.toFloat()

        if (distSq < inner * inner || distSq > outer * outer) {
            if (selectedItem == -1) return false
            selectedItem = -1
            return true
        }

        val angle = computeAngle(touchX, touchY)
        for (i in 0 until itemCount) {
            val itemStart = startAngle + i * itemAngle
            if (angle > itemStart && angle < itemStart + itemAngle) {
                val logicalIndex = if (edge.isReversed) (itemCount - 1) - i else i
                if (selectedItem == logicalIndex) return false
                selectedItem = logicalIndex
                return true
            }
        }

        if (selectedItem == -1) return false
        selectedItem = -1
        return true
    }

    fun getSelectedItemIndex(): Int = selectedItem

    fun markCancelled() {
        cancelled = true
    }

    private fun computeAngle(x: Float, y: Float): Float {
        val dx = x - centerX
        val dy = y - centerY
        var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()).toDouble()).toFloat()
        if (edge == PieEdge.LEFT && deg < -80f) {
            // no wrap needed
        }
        // Normalize to match startAngle range
        while (deg < startAngle) deg += 360f
        while (deg > startAngle + 360f) deg -= 360f
        return deg
    }

    override fun onDraw(canvas: Canvas) {
        if (animProgress <= 0f) return

        val alpha = (255 * animProgress).toInt()
        arcPaint.alpha = alpha
        highlightPaint.alpha = alpha
        gapPaint.alpha = 255

        // Draw main arc
        canvas.drawArc(arcRect, startAngle, arcSpanDegrees, false, arcPaint)

        // Draw highlight for selected item
        if (selectedItem >= 0 && animProgress >= 1f) {
            val visualIndex = if (edge.isReversed) (itemCount - 1) - selectedItem else selectedItem
            val highlightStart = startAngle + visualIndex * itemAngle
            canvas.drawArc(arcRect, highlightStart, itemAngle, false, highlightPaint)
        }

        // Draw gap separators between items
        if (itemCount > 1) {
            for (i in 1 until itemCount) {
                val gapAngle = startAngle + i * itemAngle
                canvas.drawArc(arcRect, gapAngle - 0.5f, 1f, false, gapPaint)
            }
        }

        // Draw icons
        for (i in 0 until itemCount) {
            val drawable = if (edge.isReversed) {
                itemDrawables.getOrNull((itemCount - 1) - i)
            } else {
                itemDrawables.getOrNull(i)
            } ?: continue

            val cx = iconCenters[i * 2].toInt()
            val cy = iconCenters[i * 2 + 1].toInt()
            drawable.alpha = alpha
            drawable.setBounds(cx - iconHalf, cy - iconHalf, cx + iconHalf, cy + iconHalf)
            drawable.draw(canvas)
        }

        // Draw selected item label
        if (selectedItem >= 0 && animProgress >= 1f) {
            val label = itemLabels.getOrNull(selectedItem)
            if (!label.isNullOrEmpty()) {
                labelPaint.alpha = alpha
                val labelY = centerY + outerRadius + 24 * density
                canvas.drawText(label, centerX, labelY, labelPaint)
            }
        }

        // Haptic on selection change
        if (selectedItem != lastSelectedItem) {
            lastSelectedItem = selectedItem
            if (selectedItem >= 0) {
                performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
        }
    }

    private fun adjustBrightness(color: Int): Int {
        val a = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val shift = if (r + g + b >= 384) -48 else 48
        return Color.argb(
            a,
            (r + shift).coerceIn(0, 255),
            (g + shift).coerceIn(0, 255),
            (b + shift).coerceIn(0, 255),
        )
    }
}
