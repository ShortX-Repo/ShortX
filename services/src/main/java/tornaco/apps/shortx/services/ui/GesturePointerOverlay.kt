package tornaco.apps.shortx.services.ui

import android.app.ActivityManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import tornaco.apps.shortx.core.util.OsUtils
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class GesturePointerOverlay(private val context: Context) {
    private val windowManager by lazy {
        context.getSystemService(WindowManager::class.java)
    }

    private var overlayView: GesturePointerOverlayView? = null

    fun show(edgeGroup: Int, x: Float, y: Float) {
        if (overlayView != null) {
            update(edgeGroup = edgeGroup, anchorX = x, anchorY = y, currentX = x, currentY = y)
            return
        }
        val view = GesturePointerOverlayView(context).also {
            it.updatePointer(edgeGroup = edgeGroup, anchorX = x, anchorY = y, currentX = x, currentY = y)
        }
        windowManager.addView(view, createLayoutParams())
        overlayView = view
    }

    fun update(edgeGroup: Int, anchorX: Float, anchorY: Float, currentX: Float, currentY: Float) {
        overlayView?.updatePointer(
            edgeGroup = edgeGroup,
            anchorX = anchorX,
            anchorY = anchorY,
            currentX = currentX,
            currentY = currentY,
        )
    }

    fun hide() {
        val view = overlayView ?: return
        windowManager.removeViewImmediate(view)
        overlayView = null
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams().apply {
            gravity = Gravity.START or Gravity.TOP
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
            format = PixelFormat.TRANSLUCENT
            flags = (
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            if (OsUtils.isROrAbove()) {
                fitInsetsTypes = 0
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }
            if (ActivityManager.isHighEndGfx()) {
                flags = flags or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                privateFlags =
                    privateFlags or WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED
            }
            type = WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY
            title = "GesturePointerOverlay-ShortX"
            inputFeatures =
                inputFeatures or WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL
        }
    }
}

private class GesturePointerOverlayView(context: Context) : View(context) {
    private val density = resources.displayMetrics.density
    private val trailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#66009D9D")
        style = Paint.Style.STROKE
        strokeWidth = 2f * density
        strokeCap = Paint.Cap.ROUND
    }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF009D9D")
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 3f * density
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#26009D9D")
        style = Paint.Style.FILL
    }
    private val arrowPath = Path()

    private var edgeGroup: Int = -1
    private var anchorX = 0f
    private var anchorY = 0f
    private var currentX = 0f
    private var currentY = 0f

    fun updatePointer(edgeGroup: Int, anchorX: Float, anchorY: Float, currentX: Float, currentY: Float) {
        this.edgeGroup = edgeGroup
        this.anchorX = anchorX
        this.anchorY = anchorY
        this.currentX = currentX
        this.currentY = currentY
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val dx = currentX - anchorX
        val dy = currentY - anchorY
        val movement = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val minArrowLength = 28f * density
        val shaftLength = 20f * density + movement.coerceAtMost(28f * density)
        val arrowLength = 14f * density
        val arrowWidth = 9f * density
        val angle = if (movement >= 4f * density) {
            atan2(dy, dx)
        } else {
            defaultAngleForEdge(edgeGroup)
        }
        val unitX = cos(angle)
        val unitY = sin(angle)

        val trailEndX = if (movement >= minArrowLength) currentX - unitX * arrowLength else anchorX
        val trailEndY = if (movement >= minArrowLength) currentY - unitY * arrowLength else anchorY
        if (movement >= 2f * density) {
            canvas.drawLine(anchorX, anchorY, trailEndX, trailEndY, trailPaint)
        }

        canvas.drawCircle(currentX, currentY, 12f * density, haloPaint)

        val tailX = currentX - unitX * shaftLength
        val tailY = currentY - unitY * shaftLength
        val leftX = tailX + sin(angle) * arrowWidth
        val leftY = tailY - cos(angle) * arrowWidth
        val rightX = tailX - sin(angle) * arrowWidth
        val rightY = tailY + cos(angle) * arrowWidth

        arrowPath.reset()
        arrowPath.moveTo(currentX, currentY)
        arrowPath.lineTo(leftX, leftY)
        arrowPath.lineTo(
            tailX + unitX * (shaftLength - arrowLength),
            tailY + unitY * (shaftLength - arrowLength),
        )
        arrowPath.lineTo(
            tailX - unitX * (arrowLength * 0.45f),
            tailY - unitY * (arrowLength * 0.45f),
        )
        arrowPath.lineTo(rightX, rightY)
        arrowPath.close()
        canvas.drawPath(arrowPath, arrowPaint)
    }

    private fun defaultAngleForEdge(edgeGroup: Int): Float {
        return when (edgeGroup) {
            0 -> 0f
            1 -> Math.PI.toFloat()
            2 -> (Math.PI / 2f).toFloat()
            3 -> (-Math.PI / 2f).toFloat()
            else -> (-Math.PI / 4f).toFloat()
        }
    }
}