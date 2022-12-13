package tornaco.apps.shortx.ui.overlay

import android.content.Context
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
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

@Keep
class ComposeOverlay(context: Context) : FrameLayout(context) {
    private val logger: Logger = Logger("ComposeOverlay")

    init {
        val rootContainer = FrameLayout(context)
        val wrapContent = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )

        val composeView = ComposeView(context).apply {
            setContent {
                OverlayContent()
            }
            isFocusable = false
            isClickable = false
            addToLifecycle()
        }
        rootContainer.addView(composeView, wrapContent)

        val rootLP =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(rootContainer, rootLP)
    }

    fun show() {
        logger.d("show")
        if (!isAttachedToWindow) {
            val lp = createLayoutParams()
            lp.title = "ShortX-ComposeOverlay"
            val windowManager = context.windowManager
            windowManager.addView(this, lp)
        } else {
            logger.d("Already isAttachedToWindow.")
        }
    }

    fun isShow() = isAttachedToWindow

    fun dismiss() {
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
        lp.windowAnimations = android.R.style.Animation_Toast

        val screenSize = context.screenSize()
        lp.x = screenSize.x - 72.dp_
        lp.y = screenSize.y / 4
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

    override fun onFinishInflate() {
        super.onFinishInflate()

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
