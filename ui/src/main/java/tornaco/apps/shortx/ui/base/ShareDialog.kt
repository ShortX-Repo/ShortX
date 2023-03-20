package tornaco.apps.shortx.ui.base

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.ui.util.ClipboardUtils


class ShareDialogState(val shareName: String) {
    var isShowing by mutableStateOf(false)
    var content: String = ""

    fun show(content: String) {
        this.content = content
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberShareDialogState(
    shareName: String
): ShareDialogState {
    return remember {
        ShareDialogState(shareName)
    }
}

@Composable
fun ShareDialog(state: ShareDialogState) {
    if (state.isShowing) {
        val context = LocalContext.current
        ShortXDialog(
            title = {
                DialogTitle(text = LocalI18N.current["ui.common.share"])
            },
            onDismissRequest = { state.dismiss() }) {
            ListItem(title = LocalI18N.current["ui.common.share.clipboard"]) {
                ClipboardUtils.copyToClipboard(context, state.shareName, state.content)
                state.dismiss()
            }
        }
    }
}