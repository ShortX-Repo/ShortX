package tornaco.apps.shortx.services.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.android.internal.R
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.ScreenCorner
import tornaco.apps.shortx.core.proto.toAndroidRect
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.services.gesture.bpok.gesture.HotSeat
import tornaco.apps.shortx.services.gesture.bpok.gesture.getRect
import util.Reflections

@SuppressLint("ViewConstructor")
class GuideLineView(
    context: Context,
    hotSeats: List<HotSeat>,
    areaSize: Int,
    areaLengthScale: Float,
    customRects: List<Rect>,
    cornerHints: List<ScreenCorner> = emptyList(),
    cornerRegionRadiusRatio: Float = 0.18f
) :
    FrameLayout(context) {

    private val logger = Logger(GuideLineView::class.java.simpleName)
    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private var isAttached = false

    init {
        addView(
            Guideline(
                context,
                hotSeats,
                areaSize,
                areaLengthScale,
                customRects,
                cornerHints,
                cornerRegionRadiusRatio
            ),
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    fun attach() {
        isFocusable = true
        isClickable = true

        val lp = createLayoutParams()
        lp.trySetTrustedOverlay()
        lp.title = "GuideLineView"
        windowManager.addView(this, lp)

        isAttached = true
    }

    fun detach() {
        if (isAttached) {
            windowManager.removeViewImmediate(this)
            isAttached = false
        }
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        val lp = WindowManager.LayoutParams()
        lp.gravity = Gravity.START or Gravity.TOP
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags =
            (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (OsUtils.isROrAbove()) {
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        lp.packageName = mContext.packageName
        lp.windowAnimations = R.style.Animation_Toast
        return lp
    }

    private fun WindowManager.LayoutParams.trySetTrustedOverlay() {
        kotlin.runCatching { Reflections.callMethod(this, "setTrustedOverlay") }.onFailure {
            logger.e(it, "trySetTrustedOverlay")
        }
    }

    class Guideline(
        context: Context,
        private val hotSeats: List<HotSeat>,
        private val areaSize: Int,
        private val areaLengthScale: Float,
        private val customRects: List<Rect>,
        private val cornerHints: List<ScreenCorner>,
        private val cornerRegionRadiusRatio: Float
    ) :
        View(context) {
        private val screenSize by lazy {
            context.screenSize()
        }

        private val currentOrientation by lazy {
            context.resources.configuration.orientation.also {
                Logger.nameless.d("Guideline currentOrientation: $it (ORIENTATION_PORTRAIT = 1, ORIENTATION_LANDSCAPE = 2)")
            }
        }

        private val paint by lazy {
            Paint().apply {
                isAntiAlias = true
                color = Color.GREEN
                alpha = 64
            }
        }

        private val strokePaint by lazy {
            Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 4f * resources.displayMetrics.density
                color = Color.WHITE
                alpha = 180
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val isPortrait = currentOrientation == Configuration.ORIENTATION_PORTRAIT
            hotSeats.forEach {
                val rect = it.area.getRect(
                    isPortrait = isPortrait,
                    screenWidth = screenSize.x.toFloat(),
                    screenHeight = screenSize.y.toFloat(),
                    areaSize = areaSize.toFloat(),
                    areaLengthScale = areaLengthScale
                )
                canvas.drawRect(rect, paint.apply {
                    color = it.color
                })
            }

            customRects.forEach {
                canvas.drawRect(it.toAndroidRect(), paint.apply {
                    color = Color.RED
                })
            }

            val width = screenSize.x.toFloat()
            val height = screenSize.y.toFloat()
            val radius = minOf(width, height) * cornerRegionRadiusRatio
            cornerHints.forEachIndexed { index, corner ->
                val center = when (corner) {
                    ScreenCorner.CornerTopLeft -> 0f to 0f
                    ScreenCorner.CornerTopRight -> width to 0f
                    ScreenCorner.CornerBottomLeft -> 0f to height
                    ScreenCorner.CornerBottomRight -> width to height
                    else -> 0f to 0f
                }
                val cx = center.first
                val cy = center.second
                val fillColor = when (index % 4) {
                    0 -> Color.parseColor("#66EF5350")
                    1 -> Color.parseColor("#6642A5F5")
                    2 -> Color.parseColor("#6666BB6A")
                    else -> Color.parseColor("#66FFA726")
                }
                canvas.drawCircle(cx, cy, radius, paint.apply {
                    style = Paint.Style.FILL
                    color = fillColor
                })
                canvas.drawCircle(cx, cy, radius, strokePaint)
            }
        }
    }
}