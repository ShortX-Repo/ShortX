package tornaco.apps.shortx.ui.settings.log


import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.util.ClipboardUtils

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
                    ClipboardUtils.copyToClipboard(context, "log", selectedLine)
                    Toast.makeText(
                        context,
                        i18N["ui.common.copied.to.clipboard"],
                        Toast.LENGTH_SHORT
                    ).show()
                }

                "copy_remaining" -> {
                    ClipboardUtils.copyToClipboard(
                        context,
                        "log",
                        state.logs.drop(selectedIndex).joinToString("\n")
                    )
                    Toast.makeText(
                        context,
                        i18N["ui.common.copied.to.clipboard"],
                        Toast.LENGTH_SHORT
                    ).show()
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
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            ClipboardUtils.copyToClipboard(context, "log", logs.joinToString("\n"))
            Toast.makeText(
                context,
                i18N["ui.common.copied.to.clipboard"],
                Toast.LENGTH_SHORT
            ).show()
        }) {
            RemixIcon(Remix.Document.clipboard_line)
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