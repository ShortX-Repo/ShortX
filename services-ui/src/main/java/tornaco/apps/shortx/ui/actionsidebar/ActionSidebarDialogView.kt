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
import tornaco.apps.shortx.core.proto.action.ActionSidebarStyleSettings
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

const val ACTION_SIDEBAR_ANIMATION_DURATION_MS = 220

@SuppressLint("ViewConstructor")
class ActionSidebarDialogView(
    private val buttons: List<ActionSidebarButtonUiModel>,
    private val callbacks: ActionSidebarCallbacks,
    private val position: ActionSidebarPosition,
    private val sidebarStyle: ActionSidebarStyleSettings,
    context: Context,
    i18N: I18N,
) : BaseComposeView(context, i18N) {
    private var visible by mutableStateOf(false)

    @Composable
    override fun ComposeContent() {
        LaunchedEffect(Unit) { visible = true }
        ShortXServiceUITheme {
            ActionSidebarPanel(
                buttons = buttons,
                callbacks = callbacks,
                position = position,
                style = sidebarStyle.withDefaults(),
                visible = visible,
            )
        }
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
