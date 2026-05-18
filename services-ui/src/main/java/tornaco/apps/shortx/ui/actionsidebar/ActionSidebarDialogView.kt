package tornaco.apps.shortx.ui.actionsidebar

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.action.ActionSidebarPosition
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.ui.dialog.ShadowDialogView

const val ACTION_SIDEBAR_ANIMATION_DURATION_MS = 220

@SuppressLint("ViewConstructor")
class ActionSidebarDialogView(
    private val buttons: List<ActionSidebarButtonUiModel>,
    private val callbacks: ActionSidebarCallbacks,
    private val position: ActionSidebarPosition,
    context: Context,
    i18N: I18N,
    style: DialogUiStyleSettings = DialogUiStyleSettings.getDefaultInstance(),
) : ShadowDialogView(context, i18N, style) {
    private var visible by mutableStateOf(false)

    @Composable
    override fun DialogContent() {
        LaunchedEffect(Unit) { visible = true }
        ActionSidebarPanel(
            buttons = buttons,
            callbacks = callbacks,
            position = position,
            visible = visible,
            onDismissRequest = { dismissWithAnimation(callbacks.onDismiss) },
        )
    }

    fun dismissWithAnimation(onDismissed: () -> Unit) {
        if (!visible) {
            onDismissed()
            return
        }
        visible = false
        postDelayed(onDismissed, ACTION_SIDEBAR_ANIMATION_DURATION_MS.toLong())
    }
}
