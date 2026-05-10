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

    private val arrowFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val arrowStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 1.2f * density
        strokeJoin = Paint.Join.MITER
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40000000")
        style = Paint.Style.FILL
    }

    private val arrowPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(0f, 18f * density)
        lineTo(4.5f * density, 14.5f * density)
        lineTo(7.5f * density, 21.5f * density)
        lineTo(10f * density, 20.5f * density)
        lineTo(7f * density, 13.5f * density)
        lineTo(12.5f * density, 13.5f * density)
        close()
    }

    private val trailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 3f * density
        strokeCap = Paint.Cap.ROUND
        alpha = 160 // Semi-transparent black
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

        // Calculate tail position of the rotated cursor
        // Local tail center is approx (8.75, 21) * density
        // Rotation is -15 degrees
        val rad = Math.toRadians(-15.0)
        val cos = Math.cos(rad)
        val sin = Math.sin(rad)
        val localTailX = 8.75f * density
        val localTailY = 21f * density
        val tailX = currentX + (localTailX * cos - localTailY * sin).toFloat()
        val tailY = currentY + (localTailX * sin + localTailY * cos).toFloat()

        // Draw connecting line to tail
        canvas.drawLine(anchorX, anchorY, tailX, tailY, trailPaint)

        canvas.save()
        canvas.translate(currentX, currentY)
        // Standard mouse cursor is usually tilted.
        canvas.rotate(-15f)

        // Draw shadow
        canvas.save()
        canvas.translate(1f * density, 1f * density)
        canvas.drawPath(arrowPath, shadowPaint)
        canvas.restore()

        // Draw cursor
        canvas.drawPath(arrowPath, arrowFillPaint)
        canvas.drawPath(arrowPath, arrowStrokePaint)

        canvas.restore()
    }
}