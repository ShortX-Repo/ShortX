package tornaco.apps.shortx.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.overlay.addToLifecycle

@SuppressLint("ViewConstructor")
abstract class BaseComposeView(
    context: Context,
    private val i18N: I18N,
    private val onDetach: (BaseComposeView) -> Unit
) :
    LinearLayout(context) {

    private val logger = Logger(javaClass.simpleName)
    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private var isAttached = false

    open var enableLayoutAnim = true

    fun attach() {
        val wrapContent = FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )

        val appResources = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
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
                CompositionLocalProvider(
                    LocalContext provides localContextWrapper,
                    LocalI18N provides i18N
                ) {
                    ComposeContent()
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

    fun detach(callback: Boolean = true) {
        logger.d("detach")
        if (isAttached) {
            windowManager.removeViewImmediate(this)
            isAttached = false
            if (callback) onDetach(this)
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
        if (enableLayoutAnim) lp.windowAnimations = android.R.style.Animation_InputMethod
        return lp
    }

    @Composable
    abstract fun ComposeContent()
}