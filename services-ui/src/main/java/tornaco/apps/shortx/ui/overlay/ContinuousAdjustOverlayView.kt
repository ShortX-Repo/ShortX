package tornaco.apps.shortx.ui.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

class ContinuousAdjustOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    enum class AdjustType {
        BRIGHTNESS, VOLUME
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(180, 0, 0, 0)
        style = Paint.Style.FILL
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 255, 255, 255)
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 48f
        textAlign = Paint.Align.CENTER
    }

    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 64f
        textAlign = Paint.Align.CENTER
    }

    private var adjustType: AdjustType = AdjustType.BRIGHTNESS
    private var currentValue: Int = 0
    private var maxValue: Int = 100
    private var progress: Float = 0f

    private val barWidth = 40f
    private val barHeight = 300f
    private val cornerRadius = 20f

    fun show(type: AdjustType, initialValue: Int, max: Int) {
        adjustType = type
        currentValue = initialValue
        maxValue = max
        progress = currentValue.toFloat() / maxValue
        visibility = VISIBLE
        invalidate()
    }

    fun updateValue(value: Int) {
        currentValue = value.coerceIn(0, maxValue)
        progress = currentValue.toFloat() / maxValue
        invalidate()
    }

    fun dismiss() {
        visibility = GONE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        // Background rounded rect
        val bgRect = RectF(
            centerX - barWidth / 2 - 16,
            centerY - barHeight / 2 - 16,
            centerX + barWidth / 2 + 16,
            centerY + barHeight / 2 + 16
        )
        canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, backgroundPaint)

        // Progress bar (from bottom)
        val progressHeight = barHeight * progress
        val progressRect = RectF(
            centerX - barWidth / 2,
            centerY + barHeight / 2 - progressHeight,
            centerX + barWidth / 2,
            centerY + barHeight / 2
        )
        canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint)

        // Icon
        val icon = when (adjustType) {
            AdjustType.BRIGHTNESS -> "\u2600" // Sun
            AdjustType.VOLUME -> "\uD83D\uDD0A" // Speaker
        }
        canvas.drawText(icon, centerX, centerY - barHeight / 2 - 40, iconPaint)

        // Value text
        val valueText = when (adjustType) {
            AdjustType.BRIGHTNESS -> "${(progress * 100).roundToInt()}%"
            AdjustType.VOLUME -> "${(progress * 100).roundToInt()}%"
        }
        canvas.drawText(valueText, centerX, centerY + barHeight / 2 + 60, textPaint)
    }
}
