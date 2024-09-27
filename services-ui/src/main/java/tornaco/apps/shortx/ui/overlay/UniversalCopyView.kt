package tornaco.apps.shortx.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.PixelFormat
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils

@SuppressLint("ViewConstructor", "ClickableViewAccessibility")
class UniversalCopyView(
    context: Context,
    onClick: (Float, Float) -> Unit,
    private val onDetach: (UniversalCopyView) -> Unit
) :
    LinearLayout(context) {

    private val logger = Logger(UniversalCopyView::class.java.simpleName)
    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private var isAttached = false
    private var isLoading by mutableStateOf(true)

    private val gestureDetector: GestureDetector =
        GestureDetector(context, object : OnGestureListenerAdapter() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                logger.d("onDoubleTap")
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                logger.d("onSingleTapUp")
                if (!isLoading) {
                    onClick(e.x, e.y)
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                logger.d("onLongPress")
            }
        }, handler)

    fun attach() {
        val wrapContent = FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )

        val appResources = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
        val appContext = appResources.appContext

        val contextWrapper = object : ContextWrapper(context) {
            override fun getResources(): Resources {
                return appContext.resources
            }
        }

        val composeView = ComposeView(contextWrapper).apply {
            setContent {
                CompositionLocalProvider(LocalContext provides appContext) {
                    UCVContent(isLoading) {
                        detach()
                    }
                }
            }
            isFocusable = false
            isClickable = false
            addToLifecycle()
        }
        addView(composeView, wrapContent)

        setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }

        isFocusable = true
        isClickable = true

        val lp = createLayoutParams()
        lp.title = "UniversalCopyView"
        windowManager.addView(this, lp)

        isAttached = true
        isLoading = true
    }

    fun loadComplete() {
        isLoading = false
    }

    fun detach() {
        if (isAttached) {
            windowManager.removeViewImmediate(this)
            isAttached = false
            onDetach(this)
        }
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        val lp = WindowManager.LayoutParams()
        lp.gravity = Gravity.START or Gravity.TOP
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        lp.format = PixelFormat.RGBA_8888
        lp.flags =
            (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        if (OsUtils.isROrAbove()) {
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        lp.type = WindowManager.LayoutParams.TYPE_PHONE
        lp.packageName = context.packageName
        lp.windowAnimations = android.R.style.Animation_Toast
        return lp
    }
}

open class OnGestureListenerAdapter : GestureDetector.SimpleOnGestureListener()

@Composable
private fun UCVContent(isLoading: Boolean, onBack: () -> Unit) {
    val requester = remember { FocusRequester() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(4.dp, color = MaterialTheme.colorScheme.primary)
            .onKeyEvent {
                if (it.key == Key.VolumeDown || it.key == Key.VolumeUp) {
                }
                if (it.key == Key.Back) {
                    onBack()
                }
                true
            }
            .focusRequester(requester)
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Loading(Modifier.size(48.dp))
        }
    }
}