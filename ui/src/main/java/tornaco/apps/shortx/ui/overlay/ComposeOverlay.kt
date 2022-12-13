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
import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.windowManager

@Keep
class ComposeOverlay(context: Context) : FrameLayout(context) {
    private val logger: Logger = Logger("ComposeOverlay")

    init {
        isClickable = false
        isFocusable = false
    }

    fun show() {
        val rootContainer = FrameLayout(context)
        val wrapContent = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        val composeView = ComposeView(context).apply {
            setContent {
                val vm = viewModel<OverlayViewModel>()
                val state = vm.state.collectAsState()
                LaunchedEffect(vm) {
                    vm.start()
                }
                Column(modifier = Modifier.wrapContentSize()) {
                    FilledTonalButton(onClick = {
                        Toast.makeText(context, "Stadium", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Filled.Stadium, contentDescription = "Stadium")
                    }

                    TextButton(onClick = {}) {
                        Text(text = state.value)
                    }
                }
            }
            isFocusable = false
            isClickable = false
            addToLifecycle()
        }
        rootContainer.addView(composeView, wrapContent)

        val windowManager = context.windowManager
        val rootLP =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(rootContainer, rootLP)

        val lp = createLayoutParams()
        lp.title = "ShortX-ComposeOverlay"
        windowManager.addView(this, lp)
    }

    private fun dismiss() {
        kotlin.runCatching { context.windowManager.removeViewImmediate(this) }.onFailure {
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
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
