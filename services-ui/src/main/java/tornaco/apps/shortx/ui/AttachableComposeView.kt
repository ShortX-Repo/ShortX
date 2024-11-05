package tornaco.apps.shortx.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils

@SuppressLint("ViewConstructor")
abstract class AttachableComposeView(
    context: Context,
    i18N: I18N,
    private val onDetach: (AttachableComposeView) -> Unit
) :
    BaseComposeView(context, i18N) {

    private val logger = Logger(javaClass.simpleName)
    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private var isAttached = false

    open var enableLayoutAnim = true

    fun attach() {
        initView()

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
}