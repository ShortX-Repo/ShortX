package tornaco.apps.shortx.ui.base


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties

class SelectionDialogState(
    val title: String,
    val message: String?,
    val selectionLabel2Ids: List<Pair<String, String>>,
    val onSelect: (id: String) -> Unit
) : CommonDialogState()

@Composable
fun rememberSelectionDialogState(
    title: String,
    message: String?,
    selectionLabel2Ids: List<Pair<String, String>>,
    onSelect: (id: String) -> Unit
): SelectionDialogState {
    return remember {
        SelectionDialogState(title, message, selectionLabel2Ids, onSelect)
    }
}

@Composable
fun SelectionDialog(state: SelectionDialogState) {
    if (state.isShowing) {
        ShortXDialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
            onDismissRequest = {
                state.dismiss()
            },
            title = {
                DialogTitle(text = state.title)
            }) {

            state.message?.let {
                DialogMessage(text = it)
                StandardSpacer()
            }

            state.selectionLabel2Ids.forEach {
                ListItem(title = it.first, onClick = {
                    state.onSelect(it.second)
                    state.dismiss()
                })
            }
        }
    }
}

