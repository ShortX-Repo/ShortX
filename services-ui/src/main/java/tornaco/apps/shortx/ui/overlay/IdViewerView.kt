package tornaco.apps.shortx.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.PixelFormat
import android.graphics.RectF
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.ui.XRectF
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils

@SuppressLint("ViewConstructor", "ClickableViewAccessibility")
class IdViewerView(
    context: Context,
    onClick: (Float, Float) -> Unit
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

    fun attach(nodes: List<tornaco.apps.shortx.core.ui.ViewNode>) {
        val wrapContent = FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )

        val appResources = AppResources(context, BuildProp.APP_PACKAGE_NAME)
        val appContext = appResources.appContext

        val contextWrapper = object : ContextWrapper(context) {
            override fun getResources(): Resources {
                return appContext.resources
            }
        }

        val composeView = ComposeView(contextWrapper).apply {
            setContent {
                CompositionLocalProvider(LocalContext provides appContext) {
                    IVVContent(isLoading = isLoading, nodes = nodes)
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
private fun IVVContent(isLoading: Boolean, nodes: List<tornaco.apps.shortx.core.ui.ViewNode>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(4.dp, color = MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Loading(Modifier.size(48.dp))
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            nodes.forEachIndexed { index, it ->
                drawTextOnRectF(
                    rect = it.position,
                    text = it.shortViewId,
                    nodeColor = it.color
                )
            }
        }
    }
}

private fun DrawScope.drawTextOnRectF(
    rect: XRectF,
    text: String,
    nodeColor: Int,
) {
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            color = nodeColor
            textSize = 10.sp.toPx()
        }
        val strokePaint = android.graphics.Paint().apply {
            style = android.graphics.Paint.Style.STROKE
            color = nodeColor
            strokeWidth = 6f
        }

        canvas.nativeCanvas.drawText(text, rect.left + 12, rect.top - 12, paint)
        canvas.nativeCanvas.drawRect(
            RectF(rect.left, rect.top, rect.right, rect.bottom),
            strokePaint
        )
    }
}
