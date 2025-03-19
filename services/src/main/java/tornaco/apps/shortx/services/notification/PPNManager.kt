package tornaco.apps.shortx.services.notification

import android.app.Dialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import com.android.internal.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.toLogString
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

    fun postShowPPNView(
        evaluateContext: EvaluateContext,
        contextData: Map<String, String>,
        localVars: LocalVars,
        parameters: List<FuncParameterInput>,
    ) = scope.launchSafety("postShowPPNView") {
        withContext(Dispatchers.Main) {
            logger.logOnDebug("local vars: ${localVars.all.joinToString { it.name }}")
            logger.logOnDebug("parameters: ${parameters.joinToString { it.toLogString() }}")

            showPPNDialog(
                evaluateContext = evaluateContext,
                contextData = contextData,
                localVars = localVars,
                parameters = parameters,
            )
        }
    }

    private suspend fun showPPNDialog(
        evaluateContext: EvaluateContext,
        contextData: Map<String, String>,
        localVars: LocalVars,
        parameters: List<FuncParameterInput>,
    ) {
        val dialogView = PPNotificationView(
            context = context,
            i18N = i18NIns,
            onDismissRequest = {},
            onClick = {},
            onLongClick = {}
        )

        val androidDialog = Dialog(context)

        // Initial, alpha is 0.
        dialogView.initView()
        dialogView.setAlpha(0f)

        androidDialog.setOnShowListener {
            logger.d("androidDialog.setOnShowListener")
            dialogView.translationY = (-1) * dialogView.height / 2.0f

            dialogView.setAlpha(0f)

            dialogView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(466)
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
                            or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
                )
                addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY)
                setType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY)
                setWindowAnimations(R.style.Animation_Toast)
                params.format = PixelFormat.TRANSLUCENT
                params.title = "ShortX-PPN"
                params.windowAnimations = -1
                params.y = 120._dp
                attributes = params
            },
            onDismiss = {
            }
        )

        delay(5000)
        androidDialog.dismiss()
    }

}