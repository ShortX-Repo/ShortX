package tornaco.apps.shortx.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.core.util.windowManager
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

@SuppressLint("ViewConstructor")
@Keep
class ComposeOverlay(
    context: Context,
    private val action: ShowOverlayButton,
    private val contextData: Map<String, String>,
    private val localVars: LocalVars,
    private val parameters: List<FuncParameterInput>,
    private val initialPos: Pair<Int, Int>,
    private val onPosChange: (Int, Int) -> Unit,
    private val onButtonClick: (OverlayButtonSetting) -> Unit,
    private val onButtonLongClick: (OverlayButtonSetting) -> Unit,
    private val onTouchOutside: () -> Unit,
) : FrameLayout(context) {
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
            onPosChange(out[0], out[1])
        }

    private val edgeRunnable =
        Runnable {
            updatePosToEdge()
        }

    private val touchListener: OnTouchListener = object : OnTouchListener {
        private var touchX = 0f
        private var touchY = 0f
        private var startX = 0f
        private var startY = 0f

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_OUTSIDE -> {
                    logger.d("ACTION_OUTSIDE")
                    onTouchOutside()
                    return false
                }

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
                        removeCallbacks(edgeRunnable)

                        return true
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (!action.disableAutoEdge) {
                        removeCallbacks(edgeRunnable)
                        postDelayed(edgeRunnable, 300)
                    }

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
        val appResources = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
        val appContext = appResources.appContext

        val contextWrapper = object : ContextWrapper(context) {
            override fun getResources(): Resources {
                return appContext.resources
            }
        }

        val composeView = ComposeView(contextWrapper).apply {
            setContent {
                ShortXServiceUITheme {
                    val contentColor: Color = contentColorFor(MaterialTheme.colorScheme.surface)
                    CompositionLocalProvider(
                        LocalContext provides appContext,
                        LocalContentColor provides contentColor
                    ) {
                        OverlayContent(
                            action = action,
                            localVars = localVars,
                            contextData = contextData,
                            parameters = parameters,
                            dragHandleMotionEvent = {
                                touchListener.onTouch(
                                    this@ComposeOverlay, it
                                )
                            },
                            onButtonClick = { onButtonClick(it) },
                            onButtonLongClick = { onButtonLongClick(it) },
                            onCollapse = {
                                if (!action.disableAutoEdge) {
                                    removeCallbacks(edgeRunnable)
                                    postDelayed(edgeRunnable, 300)
                                }
                            },
                            onExpand = {}
                        )

                    }
                }

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
        logger.d("show: $action, initialPos: $initialPos")
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
        logger.d("dismiss: ${action.tag}")
        kotlin.runCatching {
            if (isAttachedToWindow) {
                context.windowManager.removeViewImmediate(this)
            }
        }.onFailure {
            logger.e(it, "dismiss: ${action.tag}")
        }
        removeCallbacks(edgeRunnable)
        removeCallbacks(callPosUpdateRunnable)
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        val lp = WindowManager.LayoutParams()
        lp.gravity = Gravity.START or Gravity.TOP
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.format = PixelFormat.RGBA_8888
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        lp.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        lp.packageName = context.packageName
        lp.windowAnimations = android.R.style.Animation_Dialog
        return lp
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

    private fun updatePosToEdge() = runCatching {
        logger.d("updatePosToEdge")
        if (!isAttachedToWindow) {
            return@runCatching
        }

        val rect = Rect()
        getWindowVisibleDisplayFrame(rect)
        if (overlayLP.x < (rect.width() - width) / 2) {
            overlayLP.x = 0
        } else {
            overlayLP.x = rect.width() - width
        }

        context.windowManager.updateViewLayout(this, overlayLP)
    }.onFailure {
        logger.e(it, "updatePosToEdge")
    }
}
