package tornaco.apps.shortx.services.overlay.flash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.ColorInt
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.util.OsUtils
import kotlin.math.max
import kotlin.math.min

class PanoramicLightEffect(private val context: Context) {
    companion object {
        private const val SCREEN_FADE_DURATION_MS = 240L
        private const val SCREEN_HOLD_DURATION_MS = 540L
    }

    private var overlayView: EdgeLightOverlayView? = null

    suspend fun flash(@ColorInt color: Int) {
        showOverlayViewMainThread(color)
        delay(SCREEN_HOLD_DURATION_MS)
        fadeOutOverlayViewMainThread()
        delay(SCREEN_FADE_DURATION_MS)
        hideOverlayViewMainThread()
    }

    private fun showOverlayViewMainThread(@ColorInt color: Int) {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.privateFlags =
            params.privateFlags or WindowManager.LayoutParams.SYSTEM_FLAG_SHOW_FOR_ALL_USERS
        if (OsUtils.isQOrAbove()) {
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        params.inputFeatures =
            params.inputFeatures or WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL

        if (overlayView == null) {
            overlayView = EdgeLightOverlayView(context).apply {
                setLightColor(color)
                alpha = 0f
                context.getSystemService(WindowManager::class.java).addView(this, params)
                startPulse()
                fadeOverlayViewMainThread(this, true)
            }
        } else {
            overlayView?.apply {
                visibility = View.VISIBLE
                setLightColor(color)
                startPulse()
                fadeOverlayViewMainThread(this, true)
            }
        }
    }

    private fun fadeOutOverlayViewMainThread() {
        overlayView?.let {
            it.stopPulse()
            fadeOverlayViewMainThread(it, false)
        }
    }

    private fun fadeOverlayViewMainThread(view: View, fadeIn: Boolean) {
        ObjectAnimator.ofFloat(
            view,
            "alpha",
            if (fadeIn) 0f else 1f,
            if (fadeIn) 1f else 0f
        ).apply {
            interpolator = AccelerateDecelerateInterpolator()
            setAutoCancel(true)
            duration = SCREEN_FADE_DURATION_MS
            start()
        }
    }

    private fun hideOverlayViewMainThread() {
        overlayView?.let {
            it.visibility = View.GONE
            context.getSystemService(WindowManager::class.java).removeView(it)
            overlayView = null
        }
    }
}

private class EdgeLightOverlayView(context: Context) : FrameLayout(context) {
    private val lightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lightRect = RectF()
    private var pulseProgressValue: Float = 0f

    @ColorInt
    private var lightColor: Int = 0x55FF4018

    private var pulseAnimator: ObjectAnimator? = null

    init {
        setWillNotDraw(false)
        clipChildren = false
        clipToPadding = false
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setLightColor(@ColorInt color: Int) {
        lightColor = color
        invalidate()
    }

    fun startPulse() {
        pulseAnimator?.cancel()
        pulseAnimator = ObjectAnimator.ofFloat(this, "pulseProgress", 0f, 1f).apply {
            duration = 700L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    fun stopPulse() {
        pulseAnimator?.cancel()
        pulseAnimator = null
    }

    @Suppress("unused")
    fun setPulseProgress(value: Float) {
        pulseProgressValue = value
        invalidate()
    }

    @Suppress("unused")
    fun getPulseProgress(): Float = pulseProgressValue

    override fun onDetachedFromWindow() {
        stopPulse()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val widthF = width.toFloat()
        val heightF = height.toFloat()
        if (widthF <= 0f || heightF <= 0f) return

        drawEdge(canvas, widthF, heightF, isLeft = true)
        drawEdge(canvas, widthF, heightF, isLeft = false)
    }

    private fun drawEdge(canvas: Canvas, widthF: Float, heightF: Float, isLeft: Boolean) {
        val widthPulse = 0.94f + pulseProgressValue * 0.16f
        val heightPulse = 0.96f + pulseProgressValue * 0.08f
        val alphaPulse = 0.75f + pulseProgressValue * 0.25f
        val layers = listOf(
            Triple(0.035f, 0.33f, 0.67f),
            Triple(0.05f, 0.27f, 0.73f),
            Triple(0.07f, 0.21f, 0.79f),
            Triple(0.095f, 0.18f, 0.82f),
            Triple(0.12f, 0.14f, 0.86f),
        )
        val baseAlphas = listOf(210, 136, 84, 48, 24)

        layers.forEachIndexed { index, (widthRatio, topRatio, bottomRatio) ->
            val bandWidth = widthF * widthRatio * widthPulse
            val stretch = (heightF * 0.02f) * heightPulse * (index + 1) / layers.size.toFloat()
            val top = max(0f, heightF * topRatio - stretch)
            val bottom = min(heightF, heightF * bottomRatio + stretch)
            val left = if (isLeft) 0f else widthF - bandWidth
            val right = if (isLeft) bandWidth else widthF
            val outerAlpha = (baseAlphas[index] * alphaPulse).toInt().coerceIn(0, 255)
            val midAlpha = (outerAlpha * 0.55f).toInt().coerceIn(0, 255)
            val colors = if (isLeft) {
                intArrayOf(
                    withAlpha(lightColor, outerAlpha),
                    withAlpha(lightColor, midAlpha),
                    Color.TRANSPARENT
                )
            } else {
                intArrayOf(
                    Color.TRANSPARENT,
                    withAlpha(lightColor, midAlpha),
                    withAlpha(lightColor, outerAlpha)
                )
            }
            lightPaint.shader = LinearGradient(
                left,
                0f,
                right,
                0f,
                colors,
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
            lightRect.set(left, top, right, bottom)
            canvas.drawRoundRect(lightRect, bandWidth, bandWidth, lightPaint)
        }
        lightPaint.shader = null
    }

    private fun withAlpha(@ColorInt color: Int, alpha: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }
}