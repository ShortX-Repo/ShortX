package tornaco.apps.shortx.services.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.internal.R
import tornaco.apps.shortx.core.proto.settings.DanmuUISettings
import tornaco.apps.shortx.core.settings.DANMU_COLOR_AUTO
import tornaco.apps.shortx.core.util.ApkUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ui.widget.CardView
import util.Reflections

@SuppressLint("ViewConstructor")
class LiveDanmuView(
    context: Context,
    private val uiSettings: DanmuUISettings,
) : FrameLayout(context) {
    private val logger = Logger("LiveDanmuView")

    private val windowManager: WindowManager
    private var isAttached = false
    private val laneView: LaneView

    init {
        val rootContainer = FrameLayout(context)
        rootContainer.isFocusable = false
        rootContainer.isClickable = false

        laneView = LaneView(context).apply {
            createView = { createLaneItemView(context) }
            bindView = { data: Any, view: View -> bindLaneViewItem(view, data) }
            verticalGap = 120
            horizontalGap = 32
            duration = uiSettings.duration
        }
        val matchParent = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        rootContainer.addView(laneView, matchParent)

        windowManager = getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val screenHeight = windowManager.defaultDisplay.height
        val rootLP = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenHeight / 2)
        rootLP.setMargins(0, 72.dp, 0, 0)
        addView(rootContainer, rootLP)

        alpha = uiSettings.alpha
    }

    private fun bindLaneViewItem(view: View, data: Any) {
        if (view is CardView && data is Danmu) {
            view.findViewById<ImageView>(android.R.id.icon1).let { imageView ->
                imageView.visibility = GONE
                data.toDrawable(context)?.let { drawable ->
                    imageView.visibility = VISIBLE
                    imageView.setImageDrawable(drawable)
                }
            }

            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.text = data.text
        }
    }

    private fun createLaneItemView(context: Context): CardView {
        val cardView = CardView(context, 20.dp).apply {
            if (uiSettings.backgroundColor == DANMU_COLOR_AUTO) {
                applyDayNightMode(this)
            } else {
                setCardBackgroundColor(uiSettings.backgroundColor)
            }
        }
        val cardViewLP = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cardView.layoutParams = cardViewLP

        val linearLayout = LinearLayout(context)
        val linearLayoutLP = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayoutLP.gravity = Gravity.CENTER
        linearLayout.layoutParams = linearLayoutLP

        val imageView = ImageView(context)
        val imageViewLP = LinearLayout.LayoutParams(24.dp, 24.dp)
        imageViewLP.gravity = Gravity.CENTER
        imageViewLP.setMargins(12.dp, 2.dp, 12.dp, 2.dp)
        imageView.id = android.R.id.icon1
        imageView.layoutParams = imageViewLP

        val textView = TextView(context).apply {
            if (uiSettings.textColor == DANMU_COLOR_AUTO) {
                applyDayNightMode(this)
            } else {
                setTextColor(uiSettings.textColor)
            }
        }
        val textViewLP = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewLP.gravity = Gravity.CENTER or Gravity.START
        textView.gravity = Gravity.CENTER or Gravity.START
        textView.isFocusable = false
        textView.isClickable = false
        textView.maxLines = 4
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.id = android.R.id.text1
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, uiSettings.textSizeSp.toFloat())
        textView.layoutParams = textViewLP

        linearLayout.addView(imageView, imageViewLP)
        linearLayout.addView(textView, textViewLP)
        cardView.addView(linearLayout)
        return cardView
    }

    private fun applyDayNightMode(view: CardView) {
        if (context.isUsingNightModeResources()) {
            view.setCardBackgroundColor(Color.BLACK)
        } else {
            view.setCardBackgroundColor(Color.WHITE)
        }
    }

    private fun applyDayNightMode(view: TextView) {
        if (context.isUsingNightModeResources()) {
            view.setTextColor(Color.WHITE)
        } else {
            view.setTextColor(Color.BLACK)
        }
    }

    fun addDanmu(danmu: Danmu) {
        if (isAttached) {
            laneView.show(danmu)
        }
    }

    fun attach() {
        logger.i("LiveDanmu attach")

        isFocusable = false
        isClickable = false

        val lp = createLayoutParams()
        lp.trySetTrustedOverlay()
        lp.title = "LiveDanmu"
        windowManager.addView(this, lp)
        isAttached = true
        laneView.onEmpty = {
            logger.i("LiveDanmu onEmpty")

            if (isAttached()) {
                detach()
            }
        }
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        val lp = WindowManager.LayoutParams()
        lp.gravity = Gravity.START or Gravity.TOP
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags =
            (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        @Suppress("DEPRECATION")
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        lp.packageName = mContext.packageName
        lp.windowAnimations = R.style.Animation_Toast
        return lp
    }

    fun isAttached(): Boolean {
        return isAttached && isAttachedToWindow
    }

    fun detach() {
        logger.i("LiveDanmu detach")
        try {
            isAttached = false
            windowManager.removeViewImmediate(this@LiveDanmuView)
        } catch (e: Throwable) {
            logger.e(e, "LiveDanmu, fail detach")
        }
    }

    private fun WindowManager.LayoutParams.trySetTrustedOverlay() {
        try {
            Reflections.callMethod(this, "setTrustedOverlay")
        } catch (e: Throwable) {
            logger.e(e, "LiveDanmu trySetTrustedOverlay error")
        }
    }
}


data class Danmu(
    val icon: String?,
    val text: String,
)

private fun Danmu.toDrawable(context: Context): Drawable? {
    if (icon == null) return null
    // app://com.tencent.mm
    if (icon.startsWith("app://")) {
        val pkgName = icon.removePrefix("app://")
        return ApkUtils.loadIconByPkgName(context, pkgName)
    }
    return null
}