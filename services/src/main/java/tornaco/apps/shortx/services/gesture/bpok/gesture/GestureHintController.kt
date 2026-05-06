package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import util.Reflections

class GestureHintController(private val context: Context) {
    private val logger = Logger("GestureHintController")
    private val mainHandler = Handler(Looper.getMainLooper())
    private val windowManager by lazy {
        context.getSystemService(WindowManager::class.java)
    }

    private var view: GestureHintView? = null
    private var isAttached = false
    private var activeToken: Long? = null
    private var currentModel: GestureHintModel? = null
    private var dismissAnimator: ValueAnimator? = null
    private var successAnimator: ValueAnimator? = null
    private var idleBreathAnimator: ValueAnimator? = null

    fun onGestureProgress(model: GestureHintModel) {
        runOnMain {
            dismissAnimator?.cancel()
            ensureAttached()
            activeToken = model.token
            currentModel = model
            updateIdleBreathing(model)
            view?.render(model)
            if (view?.alpha != 1f) {
                view?.animate()?.cancel()
                view?.animate()?.alpha(1f)?.setDuration(120L)?.start()
            }
        }
    }

    fun onGestureSuccess(token: Long) {
        runOnMain {
            if (activeToken != token) return@runOnMain
            val successModel = currentModel?.copy(
                progress = 1f,
                state = GestureHintState.Success,
            ) ?: return@runOnMain
            currentModel = successModel
            view?.render(successModel)

            successAnimator?.cancel()
            successAnimator = ValueAnimator.ofFloat(0f, 1f, 0f).apply {
                duration = 220L
                addUpdateListener {
                    view?.setSuccessPulse(it.animatedValue as Float)
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        successAnimator = null
                        dismissInternal()
                    }
                })
                start()
            }
        }
    }

    fun onGestureCancel(token: Long? = null) {
        runOnMain {
            if (token != null && activeToken != token) return@runOnMain
            dismissInternal()
        }
    }

    fun onConfigurationChanged() {
        onGestureCancel()
    }

    private fun dismissInternal() {
        if (!isAttached) {
            clearState()
            return
        }
        stopIdleBreathing()
        successAnimator?.cancel()
        dismissAnimator?.cancel()
        dismissAnimator = ValueAnimator.ofFloat(view?.alpha ?: 1f, 0f).apply {
            duration = 140L
            addUpdateListener {
                view?.alpha = it.animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dismissAnimator = null
                    removeViewNow()
                }
            })
            start()
        }
    }

    private fun ensureAttached() {
        if (isAttached) return
        val hintView = GestureHintView(context)
        val lp = createLayoutParams().apply {
            trySetTrustedOverlay()
            title = "ShortX-GestureHint"
        }
        windowManager.addView(hintView, lp)
        view = hintView
        isAttached = true
    }

    private fun removeViewNow() {
        runCatching {
            view?.clear()
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
        activeToken = null
        currentModel = null
    }

    private fun updateIdleBreathing(model: GestureHintModel) {
        if (model.showArrow) {
            stopIdleBreathing()
            return
        }
        if (idleBreathAnimator != null) return
        idleBreathAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1100L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                view?.setIdlePulse(it.animatedValue as Float)
            }
            start()
        }
    }

    private fun stopIdleBreathing() {
        idleBreathAnimator?.cancel()
        idleBreathAnimator = null
        view?.setIdlePulse(0f)
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        val lp = WindowManager.LayoutParams()
        lp.gravity = Gravity.START or Gravity.TOP
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        if (OsUtils.isROrAbove()) {
            lp.fitInsetsTypes = 0
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        lp.privateFlags =
            lp.privateFlags or WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED
        lp.inputFeatures = lp.inputFeatures or WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL
        lp.type = WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY
        lp.packageName = context.packageName
        return lp
    }

    private fun WindowManager.LayoutParams.trySetTrustedOverlay() {
        runCatching { Reflections.callMethod(this, "setTrustedOverlay") }.onFailure {
            logger.e(it, "trySetTrustedOverlay")
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