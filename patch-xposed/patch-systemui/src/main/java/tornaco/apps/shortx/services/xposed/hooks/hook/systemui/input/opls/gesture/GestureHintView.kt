package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import tornaco.apps.shortx.core.util._dp
import kotlin.math.max

@SuppressLint("ViewConstructor")
class GestureHintView(context: Context) : View(context) {
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(52, 20, 32, 48)
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val circleHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val arrowPath = Path()

    private var currentModel: GestureHintModel? = null
    private var successPulse: Float = 0f
    private var idlePulse: Float = 0f

    init {
        alpha = 0f
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun render(model: GestureHintModel) {
        currentModel = model
        invalidate()
    }

    fun setSuccessPulse(value: Float) {
        successPulse = value
        invalidate()
    }

    fun setIdlePulse(value: Float) {
        idlePulse = value
        invalidate()
    }

    fun clear() {
        currentModel = null
        successPulse = 0f
        idlePulse = 0f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val model = currentModel ?: return
        val progress = model.progress.coerceIn(0f, 1f)
        val dirX = model.directionX
        val dirY = model.directionY
        val perpX = -dirY
        val perpY = dirX
        val dp1 = resources.displayMetrics.density
        val dp2 = 2._dp.toFloat()
        val idleBreathRadius = if (model.showArrow) 0f else idlePulse * 2.2f * dp1
        val circleRadius = lerp(20._dp.toFloat(), 28._dp.toFloat(), progress) + successPulse * 2.5f * dp1 + idleBreathRadius
        val highlightRadius = circleRadius * 0.78f
        val arrowLength = lerp(12._dp.toFloat(), 17._dp.toFloat(), progress) + successPulse * dp2
        val arrowStrokeWidth = lerp(2.4f * dp1, 3.4f * dp1, progress)
        val chevronHalfLength = lerp(5.5f * dp1, 8.5f * dp1, progress)
        val headSpread = when (model.state) {
            GestureHintState.Ready -> lerp(0.96f, 0.88f, progress)
            GestureHintState.Success -> 0.84f
            else -> 0f
        }
        val effectiveHeadSpread = if (headSpread == 0f) {
            lerp(1.02f, 0.94f, progress)
        } else {
            headSpread
        }
        val circleOffset = if (!model.showArrow) {
            0f
        } else {
            when (model.state) {
                GestureHintState.Candidate -> 0f
                GestureHintState.Showing -> lerp(4._dp.toFloat(), 10._dp.toFloat(), progress)
                GestureHintState.Ready -> lerp(8._dp.toFloat(), 14._dp.toFloat(), progress)
                GestureHintState.Success -> lerp(10._dp.toFloat(), 16._dp.toFloat(), progress) - successPulse * 3._dp.toFloat()
            }
        }
        val alpha = when (model.state) {
            GestureHintState.Candidate -> 216
            GestureHintState.Showing -> lerp(156f, 228f, progress).toInt()
            GestureHintState.Ready -> lerp(228f, 255f, progress).toInt()
            GestureHintState.Success -> 255
        }
        val effectiveAlpha = if (model.showArrow) alpha else (alpha + idlePulse * 20f).toInt().coerceAtMost(255)

        val baseCircleColor = when (model.state) {
            GestureHintState.Candidate -> 0xFFDCE8FB.toInt()
            GestureHintState.Showing -> blendColor(0xFFE8F0FE.toInt(), 0xFFDCE8FB.toInt(), progress)
            GestureHintState.Ready -> blendColor(0xFFD7E6FF.toInt(), 0xFFC9DEFF.toInt(), progress)
            GestureHintState.Success -> 0xFFBED8FF.toInt()
        }
        val baseHighlightColor = when (model.state) {
            GestureHintState.Candidate -> 0xFFF4F8FF.toInt()
            GestureHintState.Showing -> blendColor(0xFFF6F9FF.toInt(), 0xFFF1F6FF.toInt(), progress)
            GestureHintState.Ready -> blendColor(0xFFF8FBFF.toInt(), 0xFFF2F7FF.toInt(), progress)
            GestureHintState.Success -> 0xFFF7FAFF.toInt()
        }
        val baseArrowColor = when (model.state) {
            GestureHintState.Candidate -> Color.TRANSPARENT
            GestureHintState.Showing -> blendColor(0xFF35506E.toInt(), 0xFF2B4D73.toInt(), progress)
            GestureHintState.Ready -> blendColor(0xFF23486F.toInt(), 0xFF1D4F86.toInt(), progress)
            GestureHintState.Success -> 0xFF1D4F86.toInt()
        }

        arrowPaint.strokeWidth = arrowStrokeWidth
        shadowPaint.color = withAlpha(Color.argb(52, 20, 32, 48), max(24, effectiveAlpha / 4))
        circlePaint.color = withAlpha(baseCircleColor, effectiveAlpha)
        circleHighlightPaint.color = withAlpha(baseHighlightColor, max(100, effectiveAlpha - 28))
        arrowPaint.color = withAlpha(baseArrowColor, effectiveAlpha)

        val centerX = model.anchorX + dirX * circleOffset
        val centerY = model.anchorY + dirY * circleOffset
        val chevronTipX = centerX + dirX * (arrowLength * 0.18f)
        val chevronTipY = centerY + dirY * (arrowLength * 0.18f)
        val leftTailX = chevronTipX - dirX * chevronHalfLength + perpX * chevronHalfLength * effectiveHeadSpread
        val leftTailY = chevronTipY - dirY * chevronHalfLength + perpY * chevronHalfLength * effectiveHeadSpread
        val rightTailX = chevronTipX - dirX * chevronHalfLength - perpX * chevronHalfLength * effectiveHeadSpread
        val rightTailY = chevronTipY - dirY * chevronHalfLength - perpY * chevronHalfLength * effectiveHeadSpread

        arrowPath.reset()
        arrowPath.moveTo(leftTailX, leftTailY)
        arrowPath.lineTo(chevronTipX, chevronTipY)
        arrowPath.lineTo(rightTailX, rightTailY)

        canvas.drawCircle(centerX + dp2, centerY + dp2, circleRadius, shadowPaint)
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint)
        canvas.drawCircle(centerX, centerY, highlightRadius, circleHighlightPaint)
        if (model.showArrow && model.state != GestureHintState.Candidate) {
            canvas.drawPath(arrowPath, arrowPaint)
        }
    }

    private fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + (end - start) * fraction.coerceIn(0f, 1f)
    }

    private fun withAlpha(color: Int, alpha: Int): Int {
        return Color.argb(
            alpha.coerceIn(0, 255),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    private fun blendColor(start: Int, end: Int, fraction: Float): Int {
        val t = fraction.coerceIn(0f, 1f)
        val a = lerp(Color.alpha(start).toFloat(), Color.alpha(end).toFloat(), t).toInt()
        val r = lerp(Color.red(start).toFloat(), Color.red(end).toFloat(), t).toInt()
        val g = lerp(Color.green(start).toFloat(), Color.green(end).toFloat(), t).toInt()
        val b = lerp(Color.blue(start).toFloat(), Color.blue(end).toFloat(), t).toInt()
        return Color.argb(a, r, g, b)
    }
}