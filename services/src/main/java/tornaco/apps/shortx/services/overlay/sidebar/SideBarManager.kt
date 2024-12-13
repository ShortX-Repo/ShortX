package tornaco.apps.shortx.services.overlay.sidebar

import android.app.Dialog
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.view.Gravity
import android.view.ViewPropertyAnimator
import android.view.WindowManager
import com.android.internal.R
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.ShowSideBar
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.services.watchdog.postSafety
import tornaco.apps.shortx.ui.sidebar.SideBarDialogView
import tornaco.apps.shortx.ui.sidebar.SideBarDragHandleView
import kotlin.math.roundToInt


private const val SIDE_BAR_WIDTH_DP = 70
private const val SIDE_BAR_X_DP = 8
private const val SIDE_BAR_DRAG_HANDLE_X_DP = 0
private const val SIDE_BAR_DRAG_HANDLE_Y_SCREEN_HEIGHT_FACTOR = 0.52f

class SideBarManager(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val mainHandler: Handler
) {

    private val logger = Logger("SideBarManager")

    private var sideBarDialog: Dialog? = null
    private var sideBarDragHandle: SideBarDragHandleView? = null

    fun postShowSideBar(
        evaluateContext: EvaluateContext,
        contextData: Map<String, String>,
        localVars: LocalVars,
        parameters: List<FuncParameterInput>,
        action: ShowSideBar
    ) = mainHandler.postSafety("postShowSideBar") {
        logger.logOnDebug("local vars: ${localVars.all.joinToString { it.name }}")
        logger.logOnDebug("parameters: ${parameters.joinToString { it.toLogString() }}")

        val currentShowingSideBarDialog = sideBarDialog
        if (currentShowingSideBarDialog != null && currentShowingSideBarDialog.isShowing) {
            logger.d("postShowSideBar skip since has sideBarDialog showing.")
            return@postSafety
        }

        val currentDragHandle = sideBarDragHandle
        if (currentDragHandle != null && currentDragHandle.isShow()) {
            logger.d("postShowSideBar skip since has currentDragHandle showing.")
            return@postSafety
        }

        val screenSize = context.screenSize()

        val handleView = SideBarDragHandleView(
            context = context,
            initialPos = SIDE_BAR_DRAG_HANDLE_X_DP._dp
                    to (screenSize.y * SIDE_BAR_DRAG_HANDLE_Y_SCREEN_HEIGHT_FACTOR).roundToInt(),
            onPosChange = { x, y ->

            },
            showBar = {
                showSideBarDialog(
                    evaluateContext = evaluateContext,
                    contextData = contextData,
                    localVars = localVars,
                    parameters = parameters,
                    action = action
                )
            }
        ).apply {
            show()
        }

        sideBarDragHandle = handleView
    }

    private fun showSideBarDialog(
        evaluateContext: EvaluateContext,
        contextData: Map<String, String>,
        localVars: LocalVars,
        parameters: List<FuncParameterInput>,
        action: ShowSideBar,
    ) {
        val currentShowingSideBarDialog = sideBarDialog
        if (currentShowingSideBarDialog != null && currentShowingSideBarDialog.isShowing) {
            logger.d("showSideBarDialog skip since has sideBarDialog showing.")
            return
        }

        val dialogView = SideBarDialogView(
            context = context,
            i18N = i18NIns,
            action = action,
            localVars = localVars,
            contextData = contextData,
            parameters = parameters,
            onButtonClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.actionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                    parameterInputs = parameters,
                    actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
                )
            },
            onButtonLongClick = { button: OverlayButtonSetting ->
                shortXContext.rule.executeActions(
                    context = evaluateContext,
                    actions = button.longClickActionsList,
                    conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                    localVars = localVars,
                    parameterInputs = parameters,
                    actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
                )
            }
        )

        val androidDialog = TouchoutsideDismissDialog(context, {
            dismissSidebarDialog(dialogView)
        })

        // Initial, alpha is 0.
        dialogView.initView()
        dialogView.setAlpha(0f)

        val isWindowGravityLeft = true

        androidDialog.setOnShowListener {
            logger.d("androidDialog.setOnShowListener")
            dialogView.translationX =
                (if (isWindowGravityLeft) -1 else 1) * dialogView.width / 2.0f

            dialogView.setAlpha(0f)

            dialogView.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(466)
                .setInterpolator(LogDecelerateInterpolator())
                .start()
        }

        androidDialog.showInSystemServer(
            view = dialogView,
            cancelable = true,
            gravity = Gravity.START or Gravity.CENTER_VERTICAL,
            width = { SIDE_BAR_WIDTH_DP._dp },
            onWindow = {
                val params = attributes

                clearFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
                            or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                )
                addFlags(
                    (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
                )
                addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY)
                setType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY)
                setWindowAnimations(R.style.Animation_Toast)
                params.format = PixelFormat.TRANSLUCENT
                params.title = "ShortX-SideBar"
                params.windowAnimations = -1
                params.x = SIDE_BAR_X_DP._dp

                attributes = params
            },
            onDismiss = {
                sideBarDragHandle?.showDragHandle()
                sideBarDialog = null
            }
        )


        sideBarDialog = androidDialog
    }

    private fun dismissSidebarDialog(dialogView: SideBarDialogView) {
        dialogView.translationX = 0f
        dialogView.setAlpha(1f)
        val animator: ViewPropertyAnimator = dialogView.animate()
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(LogAccelerateInterpolator())
            .withEndAction {
                mainHandler.postDelayed({
                    sideBarDialog?.dismiss()
                }, 50)
            }
        val isWindowGravityLeft = true
        animator.translationX(
            (if (isWindowGravityLeft) -1 else 1) * dialogView.width / 2.0f
        )
    }

    fun postHideAllSideBar() = mainHandler.postSafety("postHideAllSideBar") {
        logger.d("postHideAllSideBar")
        sideBarDialog?.dismiss()
        sideBarDialog = null
    }
}