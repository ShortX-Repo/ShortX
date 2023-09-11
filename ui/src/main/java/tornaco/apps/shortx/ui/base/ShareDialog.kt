package tornaco.apps.shortx.ui.base

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.io.File


class ShareDialogState(val shareName: String) : CommonDialogState() {
    var content: String = ""
    var objectTitle: String = ""

    fun show(objectTitle: String, content: String) {
        this.content = content
        this.objectTitle = objectTitle
        show()
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

            ListItem(title = i18N["ui.common.share.save.file"]) {
                saveAsFile(context, state.objectTitle, state.content).let {
                    Toast.makeText(
                        context,
                        i18N["ui.common.save.success.to", mapOf("path" to "${it.first}${File.separator}${it.second}")],
                        Toast.LENGTH_LONG
                    ).show()
                }
                state.dismiss()
            }
        }
    }
}

fun shareAsFile(context: Context, title: String, text: String) {
    val file = File(
        context.filesDir,
        generateShareFileName(title)
    )
    file.writeText(text)
    val uri = FileProvider.getUriForFile(context, "tornaco.apps.shortx", file)
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
    context.startActivity(Intent.createChooser(shareIntent, "ShortX"))
}

fun saveAsFile(context: Context, title: String, text: String): Pair<String, String> {
    return runCatching {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(
            dir,
            generateShareFileName(title)
        )
        file.writeText(text)
        dir.name to file.name
    }.getOrElse {
        composeLogger.e(it, "saveAsFile to DIRECTORY_DOWNLOADS")
        // Save to External cache
        val extDir =
            requireNotNull(context.getExternalFilesDir(null)) { "Can not getExternalFilesDir" }
        val file = File(
            extDir,
            generateShareFileName(title)
        )
        file.writeText(text)
        extDir.name to file.name
    }
}

private fun generateShareFileName(title: String) =
    "ShortX-${title.replace("""[^a-zA-Z0-9\u4e00-\u9fa5._-]""".toRegex(), "_")}.txt"