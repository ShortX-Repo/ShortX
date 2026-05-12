package tornaco.apps.shortx.services.overlay.flash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.ColorInt
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.util.OsUtils
import kotlin.math.sin

class PanoramicLightEffect(private val context: Context) {
    companion object {
        private const val SCREEN_FADE_DURATION_MS = 240L
        private const val SCREEN_HOLD_DURATION_MS = 540L
    }

    private var overlayView: EdgeLightOverlayView? = null

    suspend fun flash(
        @ColorInt color: Int,
        speedScale: Float = 1f,
        glowWidthScale: Float = 1f,
        intensityScale: Float = 1f,
        flameJitterScale: Float = 1f,
    ) {
        val safeSpeedScale = speedScale.coerceIn(0.2f, 4f)
        showOverlayViewMainThread(
            color = color,
            speedScale = safeSpeedScale,
            glowWidthScale = glowWidthScale.coerceIn(0.35f, 3f),
            intensityScale = intensityScale.coerceIn(0.2f, 2.5f),
            flameJitterScale = flameJitterScale.coerceIn(0f, 3f),
        )
        delay((SCREEN_HOLD_DURATION_MS / safeSpeedScale).toLong().coerceAtLeast(180L))
        fadeOutOverlayViewMainThread()
        delay(SCREEN_FADE_DURATION_MS)
        hideOverlayViewMainThread()
    }

    private fun showOverlayViewMainThread(
        @ColorInt color: Int,
        speedScale: Float,
        glowWidthScale: Float,
        intensityScale: Float,
        flameJitterScale: Float,
    ) {
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
                setEffectParams(speedScale, glowWidthScale, intensityScale, flameJitterScale)
                alpha = 0f
                context.getSystemService(WindowManager::class.java).addView(this, params)
                startSweep()
                fadeOverlayViewMainThread(this, true)
            }
        } else {
            overlayView?.apply {
                visibility = View.VISIBLE
                setLightColor(color)
                setEffectParams(speedScale, glowWidthScale, intensityScale, flameJitterScale)
                startSweep()
                fadeOverlayViewMainThread(this, true)
            }
        }
    }

    private fun fadeOutOverlayViewMainThread() {
        overlayView?.let {
            it.stopSweep()
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
    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lightRect = RectF()
    private val flamePath = Path()
    private var sweepProgressValue: Float = 0f
    private var speedScale: Float = 1f
    private var glowWidthScale: Float = 1f
    private var intensityScale: Float = 1f
    private var flameJitterScale: Float = 1f

    @ColorInt
    private var lightColor: Int = 0x55FF4018

    private var sweepAnimator: ObjectAnimator? = null

    init {
        setWillNotDraw(false)
        clipChildren = false
        clipToPadding = false
        setBackgroundColor(Color.TRANSPARENT)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    fun setLightColor(@ColorInt color: Int) {
        lightColor = color
        invalidate()
    }

    fun setEffectParams(speedScale: Float, glowWidthScale: Float, intensityScale: Float, flameJitterScale: Float) {
        this.speedScale = speedScale.coerceIn(0.2f, 4f)
        this.glowWidthScale = glowWidthScale.coerceIn(0.35f, 3f)
        this.intensityScale = intensityScale.coerceIn(0.2f, 2.5f)
        this.flameJitterScale = flameJitterScale.coerceIn(0f, 3f)
        invalidate()
    }

    fun startSweep() {
        sweepAnimator?.cancel()
        sweepAnimator = ObjectAnimator.ofFloat(this, "sweepProgress", 0f, 1f).apply {
            duration = (1180L / speedScale).toLong().coerceAtLeast(280L)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            start()
        }
    }

    fun stopSweep() {
        sweepAnimator?.cancel()
        sweepAnimator = null
    }

    @Suppress("unused")
    fun setSweepProgress(value: Float) {
        sweepProgressValue = value
        invalidate()
    }

    @Suppress("unused")
    fun getSweepProgress(): Float = sweepProgressValue

    override fun onDetachedFromWindow() {
        stopSweep()
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
        val maxGlowWidth = widthF * 0.15f * glowWidthScale
        val sweepCenter = 0.28f + sweepProgressValue * 0.44f
        val flicker = sin((sweepProgressValue * Math.PI * 2.0)).toFloat() * 0.035f * flameJitterScale
        val top = heightF * 0.045f
        val bottom = heightF * 0.955f
        val left = if (isLeft) 0f else widthF - maxGlowWidth
        val right = if (isLeft) maxGlowWidth else widthF

        buildFlamePath(widthF, heightF, maxGlowWidth, flicker, isLeft)
        lightRect.set(left, top, right, bottom)

        val checkpoint = canvas.saveLayer(lightRect, null)
        lightPaint.shader = horizontalGlowShader(left, right, isLeft)
        canvas.drawPath(flamePath, lightPaint)

        maskPaint.shader = LinearGradient(
            0f,
            top,
            0f,
            bottom,
            intArrayOf(
                Color.TRANSPARENT,
                withAlpha(Color.WHITE, scaledAlpha(28)),
                withAlpha(Color.WHITE, scaledAlpha(116)),
                withAlpha(Color.WHITE, scaledAlpha(255)),
                withAlpha(Color.WHITE, scaledAlpha(130)),
                withAlpha(Color.WHITE, scaledAlpha(36)),
                Color.TRANSPARENT,
            ),
            floatArrayOf(
                0f,
                0.12f,
                (sweepCenter - 0.16f).coerceIn(0.13f, 0.70f),
                sweepCenter,
                (sweepCenter + 0.14f).coerceIn(0.30f, 0.87f),
                0.88f,
                1f,
            ),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(lightRect, maskPaint)
        canvas.restoreToCount(checkpoint)

        lightPaint.shader = null
        maskPaint.shader = null
    }

    private fun buildFlamePath(widthF: Float, heightF: Float, maxGlowWidth: Float, flicker: Float, isLeft: Boolean) {
        flamePath.reset()
        val top = heightF * 0.045f
        val bottom = heightF * 0.955f
        val outerEdge = if (isLeft) 0f else widthF
        fun x(ratio: Float): Float {
            val width = maxGlowWidth * ratio.coerceAtLeast(0f)
            return if (isLeft) width else widthF - width
        }

        flamePath.moveTo(outerEdge, top)
        flamePath.lineTo(outerEdge, bottom)
        flamePath.cubicTo(
            x(0.62f + flicker), heightF * 0.90f,
            x(0.96f - flicker), heightF * 0.74f,
            x(0.78f + flicker), heightF * 0.58f,
        )
        flamePath.cubicTo(
            x(1.05f - flicker), heightF * 0.48f,
            x(0.88f + flicker), heightF * 0.34f,
            x(0.52f - flicker), heightF * 0.22f,
        )
        flamePath.cubicTo(
            x(0.34f + flicker), heightF * 0.15f,
            x(0.18f), heightF * 0.09f,
            outerEdge, top,
        )
        flamePath.close()
    }

    private fun horizontalGlowShader(left: Float, right: Float, isLeft: Boolean): LinearGradient {
        val edgeAlpha = scaledAlpha(246)
        val midAlpha = scaledAlpha(118)
        val outerAlpha = scaledAlpha(18)
        val colors = if (isLeft) {
            intArrayOf(
                withAlpha(lightColor, edgeAlpha),
                withAlpha(lightColor, midAlpha),
                withAlpha(lightColor, outerAlpha),
                Color.TRANSPARENT,
            )
        } else {
            intArrayOf(
                Color.TRANSPARENT,
                withAlpha(lightColor, outerAlpha),
                withAlpha(lightColor, midAlpha),
                withAlpha(lightColor, edgeAlpha),
            )
        }
        return LinearGradient(
            left,
            0f,
            right,
            0f,
            colors,
            floatArrayOf(0f, 0.34f, 0.74f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    private fun withAlpha(@ColorInt color: Int, alpha: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    private fun scaledAlpha(alpha: Int): Int {
        return (alpha * intensityScale).toInt().coerceIn(0, 255)
    }

}
