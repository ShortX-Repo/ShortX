package tornaco.apps.shortx.services.overlay.flash

import android.animation.ObjectAnimator
import android.annotation.ColorInt
import android.content.Context
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.util.OsUtils

class ScreenFlash(private val context: Context) {
    companion object {
        private const val SCREEN_FADE_DURATION_MS = 200L
    }

    private var mScreenFlashNotificationOverlayView: View? = null

    suspend fun flash(@ColorInt color: Int) {
        showScreenNotificationOverlayViewMainThread(color)
        delay(SCREEN_FADE_DURATION_MS)
        fadeOutScreenNotificationOverlayViewMainThread()
        delay(SCREEN_FADE_DURATION_MS)
        hideScreenNotificationOverlayViewMainThread()
    }


    private fun showScreenNotificationOverlayViewMainThread(@ColorInt color: Int) {
        val params: WindowManager.LayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
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

        // Main display
        if (mScreenFlashNotificationOverlayView == null) {
            mScreenFlashNotificationOverlayView = getScreenNotificationOverlayView(color).apply {
                context.getSystemService(WindowManager::class.java).addView(this, params)
                fadeScreenNotificationOverlayViewMainThread(this, true)
            }
        }
    }

    private fun fadeOutScreenNotificationOverlayViewMainThread() {
        if (mScreenFlashNotificationOverlayView != null) {
            fadeScreenNotificationOverlayViewMainThread(
                mScreenFlashNotificationOverlayView!!,
                false
            )
        }
    }

    private fun fadeScreenNotificationOverlayViewMainThread(view: View, `in`: Boolean) {
        val fade = ObjectAnimator.ofFloat(
            view, "alpha", if (`in`) 0.0f else 1.0f,
            if (`in`) 1.0f else 0.0f
        )
        fade.interpolator = AccelerateInterpolator()
        fade.setAutoCancel(true)
        fade.setDuration(SCREEN_FADE_DURATION_MS)
        fade.start()
    }

    private fun hideScreenNotificationOverlayViewMainThread() {
        if (mScreenFlashNotificationOverlayView != null) {
            mScreenFlashNotificationOverlayView!!.visibility = View.GONE
            context.getSystemService(WindowManager::class.java).removeView(
                mScreenFlashNotificationOverlayView
            )
            mScreenFlashNotificationOverlayView = null
        }
    }

    private fun getScreenNotificationOverlayView(@ColorInt color: Int): View {
        val screenNotificationOverlayView: View = FrameLayout(context)
        screenNotificationOverlayView.setBackgroundColor(color)
        screenNotificationOverlayView.setAlpha(0.0f)
        return screenNotificationOverlayView
    }
}