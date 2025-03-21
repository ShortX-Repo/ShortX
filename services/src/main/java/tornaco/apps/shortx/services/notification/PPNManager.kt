package tornaco.apps.shortx.services.notification

import android.app.Dialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import com.android.internal.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.ShowPPN
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.overlay.sidebar.LogDecelerateInterpolator
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.ui.notification.PPNotificationView

class PPNManager(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val scope: CoroutineScope
) {

    private val logger = Logger("PPNManager")

    private val messageQueue = Channel<ShowPPN>(capacity = Channel.UNLIMITED)
    private var processingJob: Job? = null

    fun enqueueMessage(message: ShowPPN) {
        scope.launch {
            messageQueue.send(message) // 添加到队列
        }
        startProcessing()
    }

    private fun startProcessing() {
        if (processingJob?.isActive == true) return // 避免重复启动

        processingJob = scope.launchSafety("startProcessing") {
            for (message in messageQueue) {
                withContext(Dispatchers.Main) {
                    showPPNDialog(message)
                }
            }
        }
    }

    private suspend fun showPPNDialog(ppn: ShowPPN) {
        val dialogView = PPNotificationView(
            context = context,
            i18N = i18NIns,
            showPPN = ppn,
            onDismissRequest = {},
            onClick = {},
            onLongClick = {}
        )

        val androidDialog = Dialog(context)

        // Initial, alpha is 0.
        dialogView.initView()
        dialogView.setAlpha(0f)

        val initialTranslationY = (50._dp.toFloat())
        val animDuration = 466L

        androidDialog.setOnShowListener {
            logger.d("androidDialog.setOnShowListener")
            dialogView.translationY = initialTranslationY

            dialogView.setAlpha(0f)

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
            gravity = Gravity.BOTTOM or Gravity.CENTER_VERTICAL,
            onWindow = {
                val params = attributes

                clearFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
                            or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                )
                addFlags(
                    (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
                )
                addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY)
                setType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY)
                setWindowAnimations(R.style.Animation_Toast)
                params.format = PixelFormat.TRANSLUCENT
                params.title = "ShortX-PPN"
                params.windowAnimations = -1
                params.verticalMargin = 100._dp.toFloat()
                attributes = params
            },
            onDismiss = {
            }
        )

        delay(6000)
        dialogView.animate()
            .alpha(0f)
            .translationY(initialTranslationY)
            .setDuration(animDuration)
            .setInterpolator(LogDecelerateInterpolator())
            .withEndAction {
                androidDialog.dismiss()
            }
            .start()
    }

}