package tornaco.apps.shortx.ui.settings.log


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAX_CLIPBOARD_LOG_CHARS = 256 * 1024

@Composable
fun LogList(
    horizontalScrollEnabled: Boolean,
    listState: LazyListState,
    state: LogState
) {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    val shareDialogState = rememberShareDialogState("share.shortx.log")
    val lineMenuState = rememberMenuDialogState<Int>(
        key1 = state.logs,
        title = i18N["ui.log.line.actions"],
        menuItems = listOf(
            MenuDialogItem(
                id = "copy_line",
                title = i18N["ui.log.copy.line"],
                iconName = Remix.Document.file_copy_line
            ),
            MenuDialogItem(
                id = "copy_remaining",
                title = i18N["ui.log.copy.remaining"],
                iconName = Remix.Document.file_copy_2_line
            ),
            MenuDialogItem(
                id = "share_line",
                title = i18N["ui.common.share"],
                iconName = Remix.System.share_forward_line
            )
        ),
        onItemSelected = { index, action ->
            val selectedIndex = index ?: return@rememberMenuDialogState
            val selectedLine = state.logs.getOrNull(selectedIndex) ?: return@rememberMenuDialogState
            when (action) {
                "copy_line" -> {
                    copyLogToClipboard(
                        context = context,
                        name = "log",
                        content = selectedLine,
                        successMessage = i18N["ui.common.copied.to.clipboard"],
                        tooLargeMessage = i18N["ui.log.copy.too.large.export"],
                        failedMessage = i18N["ui.log.copy.failed.export"]
                    )
                }

                "copy_remaining" -> {
                    copyLogToClipboard(
                        context = context,
                        name = "log",
                        content = state.logs.drop(selectedIndex).joinToString("\n"),
                        successMessage = i18N["ui.common.copied.to.clipboard"],
                        tooLargeMessage = i18N["ui.log.copy.too.large.export"],
                        failedMessage = i18N["ui.log.copy.failed.export"]
                    )
                }

                "share_line" -> {
                    shareDialogState.show(
                        objectTitle = "Line-${selectedIndex + 1}",
                        content = selectedLine,
                        filePrefix = "Log"
                    )
                }
            }
        }
    )
    MenuDialog(state = lineMenuState)
    ShareDialog(state = shareDialogState)
    var modifier = Modifier
        .fillMaxSize()
    if (horizontalScrollEnabled) {
        modifier = modifier.horizontalScroll(state = rememberScrollState())
    }
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(state.logs) { index, item ->
            Text(
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        lineMenuState.show(index)
                    }
                ),
                text = item,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun MenuActions(
    logs: List<String>,
    clearLogs: () -> Unit,
    scrollToTop: () -> Unit,
    scrollToBottom: () -> Unit,
    toggleHorizontalScroll: () -> Unit
) {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    val scope = rememberCoroutineScope()
    var pendingExportContent by remember { mutableStateOf("") }
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
        onResult = { uri: Uri? ->
            if (uri == null) return@rememberLauncherForActivityResult
            val content = pendingExportContent
            scope.launch {
                runCatching {
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.bufferedWriter().use { writer ->
                            requireNotNull(writer) { "Unable to open output stream." }
                            writer.write(content)
                        }
                    }
                }.onSuccess {
                    Toast.makeText(context, i18N["ui.log.export.success"], Toast.LENGTH_SHORT).show()
                }.onFailure {
                    Toast.makeText(context, i18N["ui.log.export.failed"], Toast.LENGTH_SHORT).show()
                }
                pendingExportContent = ""
            }
        }
    )
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            copyLogToClipboard(
                context = context,
                name = "log",
                content = logs.joinToString("\n"),
                successMessage = i18N["ui.common.copied.to.clipboard"],
                tooLargeMessage = i18N["ui.log.copy.too.large.export"],
                failedMessage = i18N["ui.log.copy.failed.export"]
            )
        }) {
            RemixIcon(Remix.Document.clipboard_line)
        }
        IconButton(onClick = {
            pendingExportContent = logs.joinToString("\n")
            exportLauncher.launch(autoGenLogFileName())
        }) {
            RemixIcon(Remix.Document.file_download_line)
        }
        IconButton(onClick = {
            clearLogs()
        }) {
            RemixIcon(Remix.System.delete_bin_4_fill)
        }
        IconButton(onClick = {
            toggleHorizontalScroll()
        }) {
            RemixIcon(Remix.Editor.text_wrap)
        }
        IconButton(onClick = {
            scrollToTop()
        }) {
            RemixIcon(Remix.Arrows.arrow_up_fill)
        }
        IconButton(onClick = {
            scrollToBottom()
        }) {
            RemixIcon(Remix.Arrows.arrow_down_fill)
        }
    }
}

private fun copyLogToClipboard(
    context: android.content.Context,
    name: String,
    content: String,
    successMessage: String,
    tooLargeMessage: String,
    failedMessage: String
) {
    if (content.length > MAX_CLIPBOARD_LOG_CHARS) {
        Toast.makeText(context, tooLargeMessage, Toast.LENGTH_LONG).show()
        return
    }
    runCatching {
        ClipboardUtils.copyToClipboard(context, name, content)
    }.onSuccess {
        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
    }.onFailure {
        Toast.makeText(context, failedMessage, Toast.LENGTH_LONG).show()
    }
}

private fun autoGenLogFileName(): String {
    val time = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
    return "ShortX-Log-$time.txt"
}
