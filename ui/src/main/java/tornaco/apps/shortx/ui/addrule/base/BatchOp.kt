package tornaco.apps.shortx.ui.addrule.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.theme.PrefIcon

@Composable
fun batchOpSelectedItemHighlightColor() = MaterialTheme.colorScheme.secondaryContainer

@Composable
fun BatchOpToolbar(state: BatchOpState) {
    AnimatedVisibility(
        visible = state.isEnable
    ) {
        val deleteConfirmDialog = rememberConfirmDialogState()
        val i18N = LocalI18N.current
        ConfirmDialog(
            title = i18N["ui.common.delete"],
            messageHint = {
                i18N["ui.delete.dialog.confirm", mapOf("wht" to "")]
            },
            state = deleteConfirmDialog,
            data = state.batchSelectedItems,
            onConfirm = {
                state.onDeleteConfirmed(it)
            })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FieldTitle(
                modifier = Modifier, text = i18N["ui.common.selected.count", mapOf(
                    "selected" to "${state.batchSelectedItems.size}"
                )]
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    deleteConfirmDialog.show()
                }) {
                    RemixIcon(remixName = Remix.System.delete_bin_4_line, tint = PrefIcon)
                }
                IconButton(onClick = {
                    state.onCopy(state.batchSelectedItems)
                }) {
                    RemixIcon(remixName = Remix.Document.file_copy_line, tint = PrefIcon)
                }

                StandardSpacer()
                TextButton(onClick = { state.disable() }) {
                    Text(text = i18N["ui.common.done"])
                }
            }
        }
    }
}

class BatchOpState(
    val onCopy: (List<String>) -> Unit,
    val onDeleteConfirmed: (List<String>) -> Unit,
) {
    private var _isEnable by mutableStateOf(false)
    val isEnable get() = _isEnable

    var batchSelectedItems by mutableStateOf(emptyList<String>())

    fun addOrRemoveItem(itemId: String) {
        batchSelectedItems = batchSelectedItems.toMutableList().apply {
            if (batchSelectedItems.contains(itemId)) {
                remove(itemId)
            } else {
                add(itemId)
            }
        }
        if (batchSelectedItems.isEmpty()) {
            disable()
        }
    }

    fun enable() {
        _isEnable = true
    }

    fun disable() {
        _isEnable = false
        batchSelectedItems = emptyList()
    }
}

@Composable
fun rememberBatchOpState(
    onCopy: (List<String>) -> Unit,
    onDeleteConfirmed: (List<String>) -> Unit,
): BatchOpState {
    return remember {
        BatchOpState(onCopy = onCopy, onDeleteConfirmed = onDeleteConfirmed)
    }
}