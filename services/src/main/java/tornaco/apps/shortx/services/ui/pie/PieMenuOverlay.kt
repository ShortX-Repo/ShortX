package tornaco.apps.shortx.services.ui.pie

import android.app.ActivityManager
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.graphics.drawable.Drawable
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import util.Reflections

class PieMenuOverlay(private val context: Context) {
    private val logger = Logger("PieMenuOverlay")
    private val windowManager by lazy {
        context.getSystemService(WindowManager::class.java)
    }

    private var overlayView: PieMenuArcView? = null

    fun show(
        edge: PieEdge,
        x: Float,
        y: Float,
        itemDrawables: List<Drawable?>,
        itemLabels: List<String>,
        pieColor: Int,
        iconColor: Int,
        scalePercent: Int,
        arcSpanDegrees: Float,
        ringCount: Int,
        labelSizeSp: Int,
    ) {
        hide()
        val view = PieMenuArcView(
            context = context,
            edge = edge,
            centerX = x,
            centerY = y,
            itemDrawables = itemDrawables,
            itemLabels = itemLabels,
            pieColor = pieColor,
            iconColor = iconColor,
            scalePercent = scalePercent,
            arcSpanDegrees = arcSpanDegrees,
            ringCount = ringCount,
            labelSizeSp = labelSizeSp,
        )
        try {
            windowManager.addView(view, createLayoutParams())
            overlayView = view
        } catch (e: Exception) {
            logger.e(e, "Failed to show PieMenuOverlay")
        }
    }

    fun updateSelection(x: Float, y: Float): Boolean {
        val view = overlayView ?: return false
        val changed = view.hitTest(x, y)
        if (changed) {
            view.invalidate()
        }
        return changed
    }

    fun getSelectedItemIndex(): Int {
        return overlayView?.getSelectedItemIndex() ?: -1
    }

    fun hide() {
        val view = overlayView ?: return
        view.markCancelled()
        try {
            windowManager.removeViewImmediate(view)
        } catch (e: Exception) {
            logger.e(e, "Failed to remove PieMenuOverlay")
        }
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
            type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            packageName = context.packageName
            title = "PieMenuOverlay-ShortX"
            inputFeatures =
                inputFeatures or WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL
            trySetTrustedOverlay()
        }
    }

    private fun WindowManager.LayoutParams.trySetTrustedOverlay() {
        runCatching { Reflections.callMethod(this, "setTrustedOverlay") }.onFailure {
            logger.e(it, "trySetTrustedOverlay")
        }
    }
}
