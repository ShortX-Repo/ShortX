package tornaco.apps.shortx.ui.base

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.io.File


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
        val i18N = LocalI18N.current
        ShortXDialog(
            title = {
                DialogTitle(text = i18N["ui.common.share"])
            },
            onDismissRequest = { state.dismiss() }) {
            ListItem(title = i18N["ui.common.share.clipboard"]) {
                ClipboardUtils.copyToClipboard(context, state.shareName, state.content)
                state.dismiss()
            }

            ListItem(title = i18N["ui.common.share.file"]) {
                shareAsFile(context, i18N["ui.common.share"], state.content)
                state.dismiss()
            }
        }
    }
}

fun shareAsFile(context: Context, title: String, text: String) {
    val file = File(context.filesDir, "share.file")
    file.writeText(text)
    val uri = FileProvider.getUriForFile(context, "tornaco.apps.shortx", file)
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
    context.startActivity(Intent.createChooser(shareIntent, title))
}