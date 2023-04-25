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
    var objectTitle: String = ""

    fun show(objectTitle: String, content: String) {
        this.content = content
        this.objectTitle = objectTitle
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
                shareAsFile(context, state.objectTitle, state.content)
                state.dismiss()
            }
        }
    }
}

fun shareAsFile(context: Context, title: String, text: String) {
    val file = File(
        context.filesDir,
        "ShortX-${title.replace("""[^a-zA-Z0-9\u4e00-\u9fa5._-]""".toRegex(), "_")}.txt"
    )
    file.writeText(text)
    val uri = FileProvider.getUriForFile(context, "tornaco.apps.shortx", file)
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
    context.startActivity(Intent.createChooser(shareIntent, "ShortX"))
}