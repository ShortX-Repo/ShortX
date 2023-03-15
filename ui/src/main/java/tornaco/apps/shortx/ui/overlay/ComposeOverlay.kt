package tornaco.apps.shortx.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.core.util.windowManager
import java.util.function.Consumer

@SuppressLint("ViewConstructor")
@Keep
class ComposeOverlay(
    context: Context,
    private val initialPos: android.util.Pair<Int, Int>,
    private val onPosChange: Consumer<android.util.Pair<Int, Int>>,
    private val icons: List<String>,
    private val labels: List<String>,
    private val autoCollapse: Boolean,
    private val onButtonClick: Consumer<Int>
) : FrameLayout(context), java.util.function.Function<String, Boolean> {
    private val logger: Logger = Logger("ComposeOverlay")
    private val overlayLP = createLayoutParams()

    private var isDragging = false
    private val touchSlop by lazy {
        ViewConfiguration.get(context).scaledTouchSlop.let {
            it * it
        }
    }

    private val callPosUpdateRunnable =
        Runnable {
            val out = IntArray(2)
            getLocationOnScreen(out)
            onPosChange.accept(android.util.Pair.create(out[0], out[1]))
        }

    private val touchListener: OnTouchListener = object : OnTouchListener {
        private var touchX = 0f
        private var touchY = 0f
        private var startX = 0f
        private var startY = 0f

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchX = event.x + left
                    touchY = event.y + top
                    startX = event.rawX
                    startY = event.rawY
                    isDragging = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - startX).toInt()
                    val dy = (event.rawY - startY).toInt()
                    if (dx * dx + dy * dy > touchSlop) {
                        isDragging = true
                        overlayLP.x = (event.rawX - touchX).toInt()
                        overlayLP.y = (event.rawY - touchY).toInt()
                        context.windowManager.updateViewLayout(this@ComposeOverlay, overlayLP)

                        removeCallbacks(callPosUpdateRunnable)
                        postDelayed(callPosUpdateRunnable, 300)

                        return true
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    run {
                        touchY = 0.0f
                        touchX = touchY
                    }
                    if (isDragging) {
                        isDragging = false
                        return true
                    }
                }
                else -> {}
            }
            return false
        }
    }

    init {
        val rootContainer = FrameLayout(context)
        val wrapContent = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )

        val composeView = ComposeView(context).apply {
            setContent {
                OverlayContent(
                    autoCollapse = autoCollapse,
                    icons = icons,
                    labels = labels,
                    motionEventSpy = {
                        touchListener.onTouch(
                            this@ComposeOverlay, it
                        )
                    },
                    onButtonClick = { onButtonClick.accept(it) })
            }
            isFocusable = false
            isClickable = false
            addToLifecycle()
        }
        rootContainer.addView(composeView, wrapContent)

        val rootLP =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(rootContainer, rootLP)

        setOnTouchListener(touchListener)
    }

    fun show() {
        logger.d("show: $icons, initialPos: $initialPos")
        if (!isAttachedToWindow) {
            overlayLP.title = "ShortX-ComposeOverlay"
            val windowManager = context.windowManager
            windowManager.addView(this, overlayLP)
            initPos()
        } else {
            logger.d("Already isAttachedToWindow.")
        }
    }

    fun isShow() = isAttachedToWindow

    fun dismiss() {
        logger.d("dismiss")
        kotlin.runCatching {
            if (isAttachedToWindow) {
                context.windowManager.removeViewImmediate(this)
            }
        }.onFailure {
            logger.e(it, "dismiss")
        }
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        val lp = WindowManager.LayoutParams()
        lp.gravity = Gravity.START or Gravity.TOP
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.format = PixelFormat.RGBA_8888
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.packageName = context.packageName
        lp.windowAnimations = android.R.style.Animation_Dialog
        return lp
    }

    private fun AbstractComposeView.addToLifecycle() {
        val viewModelStore = ViewModelStore()
        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        ViewTreeLifecycleOwner.set(this, lifecycleOwner)
        ViewTreeViewModelStoreOwner.set(this) { viewModelStore }
        setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val runRecomposeScope = CoroutineScope(coroutineContext)
        val reComposer = Recomposer(coroutineContext)
        this.compositionContext = reComposer
        runRecomposeScope.launch {
            reComposer.runRecomposeAndApplyChanges()
        }
    }

    private fun initPos() {
        logger.d("initPos: $width $height")
        if (initialPos.first >= 0 && initialPos.second >= 0) {
            overlayLP.x = initialPos.first
            overlayLP.y = initialPos.second
        } else {
            context.screenSize().apply {
                overlayLP.x = x - width
                overlayLP.y = (y - height) / 2
            }
        }
        context.windowManager.updateViewLayout(this, overlayLP)
    }

    private fun updatePos(x: Int, y: Int) {
        logger.d("updatePos: $x $y")
        overlayLP.x = x
        overlayLP.y = y
        context.windowManager.updateViewLayout(this, overlayLP)
    }

    override fun apply(t: String): Boolean {
        logger.d("apply: $t")
        return when (t) {
            "show" -> {
                show()
                true
            }
            "dismiss" -> {
                dismiss()
                true
            }
            "isShow" -> {
                isShow()
            }
            else -> {
                false
            }
        }
    }
}

internal class MyLifecycleOwner : SavedStateRegistryOwner {
    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)

    /**
     * @return True if the Lifecycle has been initialized.
     */
    val isInitialized: Boolean
        get() = true
    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    fun setCurrentState(state: Lifecycle.State) {
        mLifecycleRegistry.currentState = state
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: Bundle?) {
        mSavedStateRegistryController.performRestore(savedState)
    }

    fun performSave(outBundle: Bundle) {
        mSavedStateRegistryController.performSave(outBundle)
    }
}

internal val Int.dp_: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()
