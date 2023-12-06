package tornaco.apps.shortx.ui.draw

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.overlay.addToLifecycle

@SuppressLint("ViewConstructor")
class DrawBoardView(
    context: Context,
) :
    LinearLayout(context) {

    private val logger = Logger(DrawBoardView::class.java.simpleName)
    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private var isAttached = false

    fun attach() {
        val wrapContent = FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )

        val appResources = AppResources(context, BuildProp.APP_PACKAGE_NAME)
        val appContext = appResources.appContext

        // WTF?
        // ERROR: Attempt to invoke virtual method 'void android.content.Context.registerComponentCallbacks(android.content.ComponentCallbacks)'
        // on a null object reference
        val localContextWrapper = object : ContextWrapper(appContext) {
            override fun getApplicationContext(): Context {
                return context
            }
        }

        val contextWrapper = object : ContextWrapper(context) {
            override fun getResources(): Resources {
                return appContext.resources
            }
        }

        val composeView = ComposeView(contextWrapper).apply {
            addToLifecycle()
            setContent {
                CompositionLocalProvider(LocalContext provides localContextWrapper) {
                    DrawBoardContent {
                        detach()
                    }
                }
            }
            isFocusable = true
            isClickable = true
        }
        addView(composeView, wrapContent)

        isFocusable = true
        isClickable = true

        val lp = createLayoutParams()
        lp.title = "DrawBoardView"
        windowManager.addView(this, lp)

        isAttached = true
        logger.d("attach")
    }

    fun detach() {
        logger.d("detach")
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

@Composable
private fun DrawBoardContent(onBack: () -> Unit) {
    MaterialTheme {
        val controlBarState = rememberCommonDialogState()
        LaunchedEffect(controlBarState) {
            delay(200)
            controlBarState.show()
        }

        val requester = remember { FocusRequester() }

        DrawBoard(
            modifier = Modifier
                .fillMaxSize()
                .onKeyEvent {
                    if (it.key == Key.VolumeDown) {
                        controlBarState.show()
                    }
                    if (it.key == Key.Back) {
                        onBack()
                    }
                    true
                }
                .focusRequester(requester)
                .focusable(),
            controlBarState
        )

        LaunchedEffect(Unit) {
            requester.requestFocus()
        }
    }
}