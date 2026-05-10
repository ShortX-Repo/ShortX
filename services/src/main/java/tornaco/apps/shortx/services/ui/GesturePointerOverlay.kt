package tornaco.apps.shortx.services.ui

import android.app.ActivityManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import tornaco.apps.shortx.core.util.OsUtils
import kotlin.math.hypot

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
            it.updatePointer(anchorX = x, anchorY = y, currentX = x, currentY = y)
        }
        windowManager.addView(view, createLayoutParams())
        overlayView = view
    }

    fun update(edgeGroup: Int, anchorX: Float, anchorY: Float, currentX: Float, currentY: Float) {
        overlayView?.updatePointer(
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

    private val accentColor = Color.parseColor("#D40000")

    private val connectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = accentColor
        style = Paint.Style.STROKE
        strokeWidth = 1.6f * density
        strokeCap = Paint.Cap.ROUND
    }

    private val anchorRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = accentColor
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * density
    }

    private val anchorCrossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = accentColor
        style = Paint.Style.STROKE
        strokeWidth = 1.2f * density
        strokeCap = Paint.Cap.ROUND
    }

    private val targetRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = accentColor
        style = Paint.Style.STROKE
        strokeWidth = 1.9f * density
    }

    private var anchorX = 0f
    private var anchorY = 0f
    private var currentX = 0f
    private var currentY = 0f

    fun updatePointer(anchorX: Float, anchorY: Float, currentX: Float, currentY: Float) {
        this.anchorX = anchorX
        this.anchorY = anchorY
        this.currentX = currentX
        this.currentY = currentY
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val distance = hypot((currentX - anchorX).toDouble(), (currentY - anchorY).toDouble()).toFloat()
        if (distance > 1f) {
            canvas.drawLine(
                currentX,
                currentY,
                anchorX,
                anchorY,
                connectorPaint,
            )
        }
        drawStartAnchor(canvas, distance)
        drawCurrentPointer(canvas)
    }

    private fun drawCurrentPointer(canvas: Canvas) {
        val ringRadius = 9f * density
        val crossHalf = 13f * density
        val gap = 4f * density

        canvas.drawCircle(currentX, currentY, ringRadius, anchorRingPaint)
        canvas.drawLine(currentX - crossHalf, currentY, currentX - gap, currentY, anchorCrossPaint)
        canvas.drawLine(currentX + gap, currentY, currentX + crossHalf, currentY, anchorCrossPaint)
        canvas.drawLine(currentX, currentY - crossHalf, currentX, currentY - gap, anchorCrossPaint)
        canvas.drawLine(currentX, currentY + gap, currentX, currentY + crossHalf, anchorCrossPaint)
    }

    private fun drawStartAnchor(canvas: Canvas, distance: Float) {
        if (distance > 1f) {
            canvas.drawCircle(anchorX, anchorY, distance, targetRingPaint)
        }
    }

}