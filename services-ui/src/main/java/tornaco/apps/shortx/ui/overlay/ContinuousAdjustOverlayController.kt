package tornaco.apps.shortx.ui.overlay

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import util.Reflections

class ContinuousAdjustOverlayController(private val context: Context) {
    private val logger = Logger("ContinuousAdjustOverlay")
    private val mainHandler = Handler(Looper.getMainLooper())
    private val windowManager by lazy {
        context.getSystemService(WindowManager::class.java)
    }

    private var view: ContinuousAdjustOverlayView? = null
    private var isAttached = false
    private var dismissAnimator: ValueAnimator? = null

    fun show(type: ContinuousAdjustOverlayView.AdjustType, initialValue: Int, maxValue: Int) {
        runOnMain {
            dismissAnimator?.cancel()
            ensureAttached()
            view?.show(type, initialValue, maxValue)
            if (view?.alpha != 1f) {
                view?.animate()?.cancel()
                view?.animate()?.alpha(1f)?.setDuration(120L)?.start()
            }
        }
    }

    fun updateValue(value: Int) {
        runOnMain {
            view?.updateValue(value)
        }
    }

    fun dismiss() {
        runOnMain {
            dismissInternal()
        }
    }

    private fun dismissInternal() {
        if (!isAttached) {
            clearState()
            return
        }
        dismissAnimator?.cancel()
        dismissAnimator = ValueAnimator.ofFloat(view?.alpha ?: 1f, 0f).apply {
            duration = 140L
            addUpdateListener {
                view?.alpha = it.animatedValue as Float
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    dismissAnimator = null
                    removeViewNow()
                }
            })
            start()
        }
    }

    private fun ensureAttached() {
        if (isAttached) return
        val overlayView = ContinuousAdjustOverlayView(context)
        val lp = createLayoutParams().apply {
            trySetTrustedOverlay()
            title = "ShortX-ContinuousAdjust"
        }
        windowManager.addView(overlayView, lp)
        view = overlayView
        isAttached = true
    }

    private fun removeViewNow() {
        runCatching {
            if (isAttached && view != null) {
                windowManager.removeViewImmediate(view)
            }
        }.onFailure {
            logger.e(it, "removeViewNow")
        }
        clearState()
    }

    private fun clearState() {
        view = null
        isAttached = false
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }
    }

    private fun WindowManager.LayoutParams.trySetTrustedOverlay() {
        if (OsUtils.isSOrAbove()) {
            runCatching {
                Reflections.callMethod(this, "setTrustedOverlay", true)
            }
        }
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }
}
