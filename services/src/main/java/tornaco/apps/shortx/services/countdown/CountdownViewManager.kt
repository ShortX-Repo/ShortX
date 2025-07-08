package tornaco.apps.shortx.services.countdown

import android.app.Dialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import com.android.internal.R
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.overlay.sidebar.LogDecelerateInterpolator
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.ui.countdown.CountdownView

class CountdownViewManager(
    private val context: Context,
    private val shortXContext: ShortXContext,
) {

    private val logger = Logger("CountdownViewManager")

    fun showCDDialog(total: Int, note: String): Dialog {
        val androidDialog = Dialog(context)

        val dialogView = CountdownView(
            context = context,
            i18N = i18NIns,
            totalNumber = total,
            onDismissRequest = {
                androidDialog.dismiss()
            },
            onClick = {},
            onLongClick = {},
            note = note
        )


        // Initial, alpha is 0.
        dialogView.initView()
        dialogView.alpha = 0f

        val initialTranslationY = (50._dp.toFloat())
        val animDuration = 466L
        val screenSize = context.screenSize()

        androidDialog.setOnShowListener {
            logger.d("androidDialog.setOnShowListener")
            dialogView.translationY = initialTranslationY

            dialogView.alpha = 0f

            dialogView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(animDuration)
                .setInterpolator(LogDecelerateInterpolator())
                .start()
        }

        androidDialog.showInSystemServer(
            view = dialogView,
            cancelable = true,
            gravity = Gravity.TOP or Gravity.CENTER_VERTICAL,
            onWindow = {
                val params = attributes

                clearFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
                            or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                )
                addFlags(
                    (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
                )
                addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY)
                setType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY)
                setWindowAnimations(R.style.Animation_Toast)
                params.format = PixelFormat.TRANSLUCENT
                params.title = "ShortX-CD"
                params.windowAnimations = -1
                params.y = (screenSize.y * 0.76f).toInt()
                attributes = params
            },
            onDismiss = {
            }
        )

        return androidDialog
    }

}