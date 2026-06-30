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
import kotlin.math.ceil
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

private class RingLayout(
    val outerRadius: Float,
    val innerRadius: Float,
    val startIndex: Int,
    val itemCount: Int,
    val itemAngle: Float,
    val outerRect: RectF,
    val innerRect: RectF,
)

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
    ringCount: Int,
    labelSizeSp: Int,
) : View(context) {

    companion object {
        private const val ANIMATION_DURATION_MS = 150L
        private const val OUTER_RADIUS_RATIO = 0.93f
        private const val BASE_BAND_WIDTH_RATIO = 0.50f
        private const val BAND_WIDTH_PER_EXTRA_RING = 0.15f
        private const val ICON_SIZE_RATIO = 0.065f
        private const val GAP_DEGREES = 3.5f
        private const val RING_GAP_RATIO = 0.02f
        private const val CORNER_RADIUS_DP = 14f
        private const val DEFAULT_LABEL_SP = 11f
    }

    private val density = resources.displayMetrics.density
    private val scaledDensity = resources.displayMetrics.scaledDensity
    private val totalItemCount = itemDrawables.size.coerceAtLeast(1)
    private val effectiveRingCount = ringCount.coerceIn(1, 3).coerceAtMost(totalItemCount)
    private val startAngle = edge.startAngleDeg

    private val normalColor = pieColor
    private val selectedColor = blendWithWhite(pieColor, 0.35f)

    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        pathEffect = CornerPathEffect(CORNER_RADIUS_DP * density)
    }

    private val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = iconColor
        textAlign = Paint.Align.CENTER
        textSize = (if (labelSizeSp > 0) labelSizeSp.toFloat() else DEFAULT_LABEL_SP) * scaledDensity
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }

    private val fM: Float
    private val iconHalf: Int
    private val rings: List<RingLayout>

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

        val totalOuterRadius = OUTER_RADIUS_RATIO * fM
        val bandWidthRatio = BASE_BAND_WIDTH_RATIO + BAND_WIDTH_PER_EXTRA_RING * (effectiveRingCount - 1)
        val totalBandWidth = bandWidthRatio * fM
        val totalInnerRadius = totalOuterRadius - totalBandWidth
        iconHalf = (ICON_SIZE_RATIO * fM + 0.5f).toInt()

        val ringGap = RING_GAP_RATIO * fM
        val ringBand = (totalBandWidth - ringGap * (effectiveRingCount - 1)) / effectiveRingCount
        val itemsPerRing = ceil(totalItemCount.toFloat() / effectiveRingCount).toInt()

        rings = buildList {
            var offset = 0
            for (r in 0 until effectiveRingCount) {
                val count = min(itemsPerRing, totalItemCount - offset)
                if (count <= 0) break
                val ringOuter = totalOuterRadius - r * (ringBand + ringGap)
                val ringInner = ringOuter - ringBand
                add(RingLayout(
                    outerRadius = ringOuter,
                    innerRadius = ringInner,
                    startIndex = offset,
                    itemCount = count,
                    itemAngle = arcSpanDegrees / count,
                    outerRect = RectF(
                        centerX - ringOuter, centerY - ringOuter,
                        centerX + ringOuter, centerY + ringOuter
                    ),
                    innerRect = RectF(
                        centerX - ringInner, centerY - ringInner,
                        centerX + ringInner, centerY + ringInner
                    ),
                ))
                offset += count
            }
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
        val angle = computeAngle(touchX, touchY)

        for (ring in rings) {
            if (distSq < ring.innerRadius * ring.innerRadius || distSq > ring.outerRadius * ring.outerRadius) {
                continue
            }
            for (i in 0 until ring.itemCount) {
                val itemStart = startAngle + i * ring.itemAngle
                if (angle > itemStart && angle < itemStart + ring.itemAngle) {
                    val visualIndex = i
                    val logicalIndex = if (edge.isReversed) (ring.itemCount - 1) - visualIndex else visualIndex
                    val globalIndex = ring.startIndex + logicalIndex
                    if (selectedItem == globalIndex) return false
                    selectedItem = globalIndex
                    return true
                }
            }
            if (selectedItem == -1) return false
            selectedItem = -1
            return true
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

        for (ring in rings) {
            val effectiveSweep = ring.itemAngle - GAP_DEGREES
            val bandWidth = ring.outerRadius - ring.innerRadius

            for (i in 0 until ring.itemCount) {
                val sectorStart = startAngle + i * ring.itemAngle + GAP_DEGREES / 2f
                val path = createSectorPath(ring.outerRect, ring.innerRect, ring.innerRadius, sectorStart, effectiveSweep)

                val logicalIndex = if (edge.isReversed) (ring.itemCount - 1) - i else i
                val globalIndex = ring.startIndex + logicalIndex
                val isSelected = globalIndex == selectedItem && animProgress >= 1f

                sectorPaint.color = if (isSelected) selectedColor else normalColor
                sectorPaint.alpha = alpha
                canvas.drawPath(path, sectorPaint)

                val midAngle = sectorStart + effectiveSweep / 2f
                val midAngleRad = Math.toRadians(midAngle.toDouble())

                val drawable = itemDrawables.getOrNull(globalIndex)
                val contentRadius = ring.innerRadius + bandWidth * 0.50f

                if (drawable != null) {
                    val iconCx = (centerX + cos(midAngleRad) * contentRadius).toInt()
                    val iconCy = (centerY + sin(midAngleRad) * contentRadius).toInt()
                    drawable.alpha = alpha
                    drawable.setBounds(iconCx - iconHalf, iconCy - iconHalf, iconCx + iconHalf, iconCy + iconHalf)
                    drawable.draw(canvas)
                } else {
                    val label = itemLabels.getOrNull(globalIndex)
                    if (!label.isNullOrEmpty()) {
                        labelPaint.alpha = alpha
                        val labelCx = (centerX + cos(midAngleRad) * contentRadius).toFloat()
                        val labelCy = (centerY + sin(midAngleRad) * contentRadius).toFloat()
                        canvas.drawText(label, labelCx, labelCy + labelPaint.textSize * 0.35f, labelPaint)
                    }
                }
            }
        }

        if (selectedItem != lastSelectedItem) {
            lastSelectedItem = selectedItem
            if (selectedItem >= 0) {
                performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
        }
    }

    private fun createSectorPath(
        outerRect: RectF,
        innerRect: RectF,
        innerRadius: Float,
        startDeg: Float,
        sweepDeg: Float,
    ): Path {
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
