package tornaco.apps.shortx.services.overlay.actionsidebar

import android.app.Dialog
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import com.android.internal.R
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ActionSidebarButton
import tornaco.apps.shortx.core.proto.action.ActionSidebarPosition
import tornaco.apps.shortx.core.proto.action.ShowActionSidebar
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.toLogString
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.overlay.sidebar.TouchoutsideDismissDialog
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.services.watchdog.postSafety
import tornaco.apps.shortx.ui.actionsidebar.ActionSidebarButtonUiModel
import tornaco.apps.shortx.ui.actionsidebar.ActionSidebarCallbacks
import tornaco.apps.shortx.ui.actionsidebar.ActionSidebarDialogView

private const val ACTION_SIDEBAR_WIDTH_DP = 124
private const val ACTION_SIDEBAR_X_DP = 8
private const val ACTION_SIDEBAR_Y_DP = 8

class ActionSidebarManager(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val mainHandler: Handler,
) {
    private val logger = Logger("ActionSidebarManager")
    private var sidebarDialog: Dialog? = null
    private var sidebarDialogView: ActionSidebarDialogView? = null
    private var session: ActionSidebarSession? = null

    fun postShowActionSidebar(
        evaluateContext: EvaluateContext,
        localVars: LocalVars,
        parameters: List<FuncParameterInput>,
        action: ShowActionSidebar,
    ) = mainHandler.postSafety("postShowActionSidebar") {
        logger.d("postShowActionSidebar: ${action.id}, buttons: ${action.buttonsCount}")
        logger.d("parameters: ${parameters.joinToString { it.toLogString() }}")

        val visibleDialog = sidebarDialog
        if (visibleDialog != null && visibleDialog.isShowing && session?.sourceActionId == action.id) {
            logger.d("skip duplicate action sidebar: ${action.id}")
            return@postSafety
        }

        if (action.buttonsCount == 0) {
            fail(action, i18NIns["ui.action.show.action.sidebar.empty"])
            return@postSafety
        }

        showActionSidebarDialog(
            evaluateContext = evaluateContext,
            localVars = localVars,
            parameters = parameters,
            action = action,
        )
    }

    private fun showActionSidebarDialog(
        evaluateContext: EvaluateContext,
        localVars: LocalVars,
        parameters: List<FuncParameterInput>,
        action: ShowActionSidebar,
    ) {
        sidebarDialog?.dismiss()
        sidebarDialogView = null

        val buttons = action.buttonsList
            .sortedBy { it.order }
            .map { it.toUiModel() }

        val dialogView = ActionSidebarDialogView(
            context = context,
            i18N = i18NIns,
            buttons = buttons,
            position = action.position.normalized(),
            sidebarStyle = action.style,
            callbacks = ActionSidebarCallbacks(
                onDismiss = { postHideActionSidebar() },
                onButtonTap = { button ->
                    executeButtonActions(
                        evaluateContext = evaluateContext,
                        localVars = localVars,
                        parameters = parameters,
                        actions = button.tapActions,
                    )
                },
                onButtonLongPress = { button ->
                    executeButtonActions(
                        evaluateContext = evaluateContext,
                        localVars = localVars,
                        parameters = parameters,
                        actions = button.longPressActions,
                    )
                },
            )
        )

        val androidDialog = TouchoutsideDismissDialog(context) {
            postHideActionSidebar()
        }

        runCatching {
            dialogView.initView()
            androidDialog.showInSystemServer(
                view = dialogView,
                cancelable = true,
                gravity = action.position.normalized().toGravity(),
                width = { ACTION_SIDEBAR_WIDTH_DP._dp },
                onWindow = {
                    val params = attributes
                    clearFlags(
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND
                                or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    )
                    addFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                    )
                    addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY)
                    setType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY)
                    setWindowAnimations(R.style.Animation_Toast)
                    params.format = PixelFormat.TRANSLUCENT
                    params.title = "ShortX-ActionSidebar"
                    params.windowAnimations = -1
                    val position = action.position.normalized()
                    params.x = if (position.isHorizontal()) ACTION_SIDEBAR_X_DP._dp else 0
                    params.y = if (position.isVertical()) ACTION_SIDEBAR_Y_DP._dp else 0
                    attributes = params
                },
                onDismiss = {
                    sidebarDialog = null
                    sidebarDialogView = null
                    session = session?.copy(state = ActionSidebarSessionState.Dismissed)
                }
            )
            sidebarDialog = androidDialog
            sidebarDialogView = dialogView
            session = ActionSidebarSession(
                sourceActionId = action.id,
                visibleButtons = action.buttonsList,
                state = ActionSidebarSessionState.Visible,
            )
        }.onFailure {
            logger.e(it, "Failed to show action sidebar")
            fail(action, it.message ?: "Unable to show sidebar")
        }
    }

    private fun executeButtonActions(
        evaluateContext: EvaluateContext,
        localVars: LocalVars,
        parameters: List<FuncParameterInput>,
        actions: List<com.google.protobuf.Any>,
    ) {
        if (actions.isEmpty()) return
        runCatching {
            shortXContext.rule.executeActions(
                context = evaluateContext,
                actions = actions,
                conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                localVars = localVars,
                parameterInputs = parameters,
                actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync,
            )
        }.onFailure {
            logger.e(it, "Failed to execute action sidebar button actions")
            Toast.makeText(context, it.message ?: "Action failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun postHideActionSidebar() = mainHandler.postSafety("postHideActionSidebar") {
        session = session?.copy(state = ActionSidebarSessionState.Dismissing)
        val dialog = sidebarDialog
        val dialogView = sidebarDialogView
        if (dialog != null && dialog.isShowing && dialogView != null) {
            dialogView.dismissWithAnimation { dialog.dismiss() }
        } else {
            dialog?.dismiss()
            sidebarDialog = null
            sidebarDialogView = null
            session = session?.copy(state = ActionSidebarSessionState.Dismissed)
        }
    }

    private fun fail(action: ShowActionSidebar, message: String) {
        session = ActionSidebarSession(
            sourceActionId = action.id,
            visibleButtons = action.buttonsList,
            state = ActionSidebarSessionState.Failed,
            failureMessage = message,
        )
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

private fun ActionSidebarButton.toUiModel(): ActionSidebarButtonUiModel {
    return ActionSidebarButtonUiModel(
        id = id,
        icon = icon,
        text = text,
        order = order,
        tapActions = tapActionsList,
        longPressActions = longPressActionsList,
    )
}

private fun ActionSidebarPosition.toGravity(): Int = when (this) {
    ActionSidebarPosition.ActionSidebarPosition_Right -> Gravity.END or Gravity.CENTER_VERTICAL
    ActionSidebarPosition.ActionSidebarPosition_Top -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
    ActionSidebarPosition.ActionSidebarPosition_Bottom -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    else -> Gravity.START or Gravity.CENTER_VERTICAL
}

private fun ActionSidebarPosition.normalized(): ActionSidebarPosition {
    return if (this == ActionSidebarPosition.UNRECOGNIZED) {
        ActionSidebarPosition.ActionSidebarPosition_Left
    } else {
        this
    }
}

private fun ActionSidebarPosition.isHorizontal(): Boolean {
    return this == ActionSidebarPosition.ActionSidebarPosition_Left ||
            this == ActionSidebarPosition.ActionSidebarPosition_Right
}

private fun ActionSidebarPosition.isVertical(): Boolean {
    return this == ActionSidebarPosition.ActionSidebarPosition_Top ||
            this == ActionSidebarPosition.ActionSidebarPosition_Bottom
}
