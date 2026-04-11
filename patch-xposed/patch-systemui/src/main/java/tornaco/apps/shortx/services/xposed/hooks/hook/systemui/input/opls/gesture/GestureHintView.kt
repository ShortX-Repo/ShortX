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
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = Color.argb(88, 19, 28, 38)
    }

    private val containerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val leadingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val leadingHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val anchorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val arrowPath = Path()

    private var currentModel: GestureHintModel? = null
    private var successPulse: Float = 0f

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

    fun clear() {
        currentModel = null
        successPulse = 0f
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
        val dp2 = 2._dp.toFloat()
        val dp4 = 4._dp.toFloat()
        val dp6 = 6._dp.toFloat()
        val dp10 = 10._dp.toFloat()
        val dp11 = 11._dp.toFloat()
        val dp12 = 12._dp.toFloat()
        val dp18 = 18._dp.toFloat()
        val dp20 = 20._dp.toFloat()
        val dp22 = 22._dp.toFloat()
        val dp28 = 28._dp.toFloat()
        val dp84 = 84._dp.toFloat()

        val pullBack = lerp(dp4, dp10, progress)
        val length = lerp(dp28, dp84, progress)
        val arrowHead = lerp(9._dp.toFloat(), 15._dp.toFloat(), progress)
        val containerWidth = lerp(dp20, 28._dp.toFloat(), progress) + successPulse * dp2
        val highlightWidth = containerWidth * 0.52f
        val arrowStrokeWidth = lerp(2.2f * resources.displayMetrics.density, 3.6f * resources.displayMetrics.density, progress) + successPulse * 0.8f * resources.displayMetrics.density
        val anchorRadius = lerp(4._dp.toFloat(), 7._dp.toFloat(), progress) + successPulse * 2._dp.toFloat()
        val leadingRadius = containerWidth * lerp(0.34f, 0.46f, progress)
        val leadingHighlightRadius = leadingRadius * 0.62f
        val alpha = when (model.state) {
            GestureHintState.Candidate -> 0
            GestureHintState.Showing -> lerp(144f, 228f, progress).toInt()
            GestureHintState.Ready -> lerp(224f, 255f, progress).toInt()
            GestureHintState.Success -> 255
        }

        val baseContainerColor = when (model.state) {
            GestureHintState.Candidate -> Color.argb(0, 0, 0, 0)
            GestureHintState.Showing -> blendColor(0xFFF4F7FB.toInt(), 0xFFE6EEF9.toInt(), progress)
            GestureHintState.Ready -> blendColor(0xFFE8F0FE.toInt(), 0xFFD3E3FD.toInt(), progress)
            GestureHintState.Success -> 0xFFC2E7FF.toInt()
        }
        val baseHighlightColor = when (model.state) {
            GestureHintState.Candidate -> Color.TRANSPARENT
            GestureHintState.Showing -> blendColor(0xFFFFFFFF.toInt(), 0xFFF8FBFF.toInt(), progress)
            GestureHintState.Ready -> blendColor(0xFFFFFFFF.toInt(), 0xFFEEF4FF.toInt(), progress)
            GestureHintState.Success -> 0xFFF7FBFF.toInt()
        }
        val baseArrowColor = when (model.state) {
            GestureHintState.Candidate -> Color.TRANSPARENT
            GestureHintState.Showing -> blendColor(0xFF536579.toInt(), 0xFF43627F.toInt(), progress)
            GestureHintState.Ready -> blendColor(0xFF395977.toInt(), 0xFF2B5D92.toInt(), progress)
            GestureHintState.Success -> 0xFF2F6298.toInt()
        }

        shadowPaint.strokeWidth = containerWidth + dp4
        containerPaint.strokeWidth = containerWidth
        highlightPaint.strokeWidth = highlightWidth
        arrowPaint.strokeWidth = arrowStrokeWidth
        shadowPaint.alpha = max(44, alpha / 3)
        containerPaint.color = withAlpha(baseContainerColor, alpha)
        highlightPaint.color = withAlpha(baseHighlightColor, max(120, alpha - 20))
        arrowPaint.color = withAlpha(baseArrowColor, alpha)
        leadingPaint.color = withAlpha(baseContainerColor, alpha)
        leadingHighlightPaint.color = withAlpha(baseHighlightColor, max(120, alpha - 12))
        anchorPaint.color = withAlpha(blendColor(0xFFEEF3F8.toInt(), 0xFFE3EBF6.toInt(), progress), max(72, alpha - 80))
        shadowPaint.alpha = max(50, alpha / 2)

        val anchorX = model.anchorX
        val anchorY = model.anchorY
        val lineStartX = anchorX - dirX * pullBack
        val lineStartY = anchorY - dirY * pullBack
        val lineEndX = anchorX + dirX * length
        val lineEndY = anchorY + dirY * length
        val innerEndX = anchorX + dirX * (length - containerWidth * 0.62f)
        val innerEndY = anchorY + dirY * (length - containerWidth * 0.62f)

        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, shadowPaint)
        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, containerPaint)
        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, highlightPaint)

        val leftHeadX = innerEndX - dirX * arrowHead + perpX * arrowHead * 0.48f
        val leftHeadY = innerEndY - dirY * arrowHead + perpY * arrowHead * 0.48f
        val rightHeadX = innerEndX - dirX * arrowHead - perpX * arrowHead * 0.48f
        val rightHeadY = innerEndY - dirY * arrowHead - perpY * arrowHead * 0.48f

        arrowPath.reset()
        arrowPath.moveTo(anchorX + dirX * (containerWidth * 0.08f), anchorY + dirY * (containerWidth * 0.08f))
        arrowPath.lineTo(innerEndX, innerEndY)
        arrowPath.moveTo(innerEndX, innerEndY)
        arrowPath.lineTo(leftHeadX, leftHeadY)
        arrowPath.moveTo(innerEndX, innerEndY)
        arrowPath.lineTo(rightHeadX, rightHeadY)

        canvas.drawCircle(innerEndX, innerEndY, leadingRadius, leadingPaint)
        canvas.drawCircle(innerEndX, innerEndY, leadingHighlightRadius, leadingHighlightPaint)
        canvas.drawPath(arrowPath, arrowPaint)
        canvas.drawCircle(anchorX, anchorY, anchorRadius, anchorPaint)
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