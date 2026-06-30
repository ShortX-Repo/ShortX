package tornaco.apps.shortx.services.ui.pie

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.Property
import android.view.HapticFeedbackConstants
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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
        private const val ANIMATION_DURATION_MS = 150L
        private const val OUTER_RADIUS_RATIO = 0.93f
        private const val BAND_WIDTH_RATIO = 0.50f
        private const val ICON_SIZE_RATIO = 0.065f
        private const val GAP_DEGREES = 3.5f
        private const val CORNER_RADIUS_DP = 14f
        private const val LABEL_TEXT_SP = 11f
    }

    private val density = resources.displayMetrics.density

    private val normalColor = pieColor
    private val selectedColor = blendWithWhite(pieColor, 0.35f)

    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        pathEffect = CornerPathEffect(CORNER_RADIUS_DP * density)
    }

    private val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = iconColor
        textAlign = Paint.Align.CENTER
        textSize = LABEL_TEXT_SP * density
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }

    private val itemCount: Int = itemDrawables.size.coerceAtLeast(1)
    private val itemAngle: Float = arcSpanDegrees / itemCount
    private val startAngle: Float = edge.startAngleDeg

    private val fM: Float
    private val outerRadius: Float
    private val innerRadius: Float
    private val bandWidth: Float
    private val iconHalf: Int

    private val outerRect: RectF
    private val innerRect: RectF

    private var selectedItem: Int = -1
    private var lastSelectedItem: Int = -1
    private var animProgress: Float = 1f
    private var cancelled: Boolean = false

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

        outerRadius = OUTER_RADIUS_RATIO * fM
        bandWidth = BAND_WIDTH_RATIO * fM
        innerRadius = outerRadius - bandWidth
        iconHalf = (ICON_SIZE_RATIO * fM + 0.5f).toInt()

        outerRect = RectF(
            centerX - outerRadius, centerY - outerRadius,
            centerX + outerRadius, centerY + outerRadius
        )
        innerRect = RectF(
            centerX - innerRadius, centerY - innerRadius,
            centerX + innerRadius, centerY + innerRadius
        )

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

        if (distSq < innerRadius * innerRadius || distSq > outerRadius * outerRadius) {
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
        var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        while (deg < startAngle) deg += 360f
        while (deg > startAngle + 360f) deg -= 360f
        return deg
    }

    override fun onDraw(canvas: Canvas) {
        if (animProgress <= 0f) return
        val alpha = (255 * animProgress).toInt()
        val effectiveSweep = itemAngle - GAP_DEGREES

        for (i in 0 until itemCount) {
            val sectorStart = startAngle + i * itemAngle + GAP_DEGREES / 2f
            val path = createSectorPath(sectorStart, effectiveSweep)

            val logicalIndex = if (edge.isReversed) (itemCount - 1) - i else i
            val isSelected = logicalIndex == selectedItem && animProgress >= 1f

            sectorPaint.color = if (isSelected) selectedColor else normalColor
            sectorPaint.alpha = alpha
            canvas.drawPath(path, sectorPaint)

            val midAngle = sectorStart + effectiveSweep / 2f
            val midAngleRad = Math.toRadians(midAngle.toDouble())

            val drawable = if (edge.isReversed) {
                itemDrawables.getOrNull((itemCount - 1) - i)
            } else {
                itemDrawables.getOrNull(i)
            }
            val iconRadius = innerRadius + bandWidth * 0.58f
            val iconCx = (centerX + cos(midAngleRad) * iconRadius).toInt()
            val iconCy = (centerY + sin(midAngleRad) * iconRadius).toInt()
            drawable?.let {
                it.alpha = alpha
                it.setBounds(iconCx - iconHalf, iconCy - iconHalf, iconCx + iconHalf, iconCy + iconHalf)
                it.draw(canvas)
            }

            val label = if (edge.isReversed) {
                itemLabels.getOrNull((itemCount - 1) - i)
            } else {
                itemLabels.getOrNull(i)
            }
            if (!label.isNullOrEmpty()) {
                labelPaint.alpha = alpha
                val labelRadius = innerRadius + bandWidth * 0.30f
                val labelCx = (centerX + cos(midAngleRad) * labelRadius).toFloat()
                val labelCy = (centerY + sin(midAngleRad) * labelRadius).toFloat()
                canvas.drawText(label, labelCx, labelCy + labelPaint.textSize * 0.35f, labelPaint)
            }
        }

        if (selectedItem != lastSelectedItem) {
            lastSelectedItem = selectedItem
            if (selectedItem >= 0) {
                performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
        }
    }

    private fun createSectorPath(startDeg: Float, sweepDeg: Float): Path {
        val path = Path()
        path.arcTo(outerRect, startDeg, sweepDeg)
        val endDeg = startDeg + sweepDeg
        val endRad = Math.toRadians(endDeg.toDouble())
        path.lineTo(
            centerX + cos(endRad).toFloat() * innerRadius,
            centerY + sin(endRad).toFloat() * innerRadius
        )
        path.arcTo(innerRect, endDeg, -sweepDeg)
        path.close()
        return path
    }

    private fun blendWithWhite(color: Int, ratio: Float): Int {
        val a = Color.alpha(color)
        val r = (Color.red(color) * (1 - ratio) + 255 * ratio).toInt()
        val g = (Color.green(color) * (1 - ratio) + 255 * ratio).toInt()
        val b = (Color.blue(color) * (1 - ratio) + 255 * ratio).toInt()
        return Color.argb(a, r.coerceAtMost(255), g.coerceAtMost(255), b.coerceAtMost(255))
    }
}
