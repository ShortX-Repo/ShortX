package tornaco.apps.shortx

import android.content.Context
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
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.services.allHotSeats
import tornaco.apps.shortx.services.getRect
import util.Reflections

class GuideLineView(context: Context) : FrameLayout(context) {

    private val logger = Logger(GuideLineView::class.java.simpleName)
    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private var isAttached = false

    init {
        addView(Guideline(context), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
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
        lp.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
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

    class Guideline(context: Context) : View(context) {
        private val screenSize by lazy {
            context.screenSize()
        }

        private val paint by lazy {
            Paint().apply {
                isAntiAlias = true
                color = Color.GREEN
                alpha = 64
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            allHotSeats.forEach {
                val rect = it.getRect(screenSize.x.toFloat(), screenSize.y.toFloat())
                canvas.drawRect(rect, paint.apply {
                    color = it.color
                })
            }
        }
    }
}