package tornaco.apps.shortx.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> MenuDialog(
    state: MenuDialogState<T>,
) {
    if (state.isShowing) {
        ShortXDialog(
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
            state.menuItems.forEach { dialogItem ->
                ListItem(
                    title = dialogItem.title,
                    icon = {
                        dialogItem.iconName?.let {
                            RemixIcon(remixName = it)
                        }
                    },
                    onClick = {
                        state.dismiss()
                        state.onItemSelected(state._data, dialogItem.id)
                    })
            }

        }
    }
}


data class MenuDialogItem(val id: String, val title: String, val iconName: String? = null)

open class MenuDialogState<DATA>(
    val title: String,
    val message: String? = null,
    val menuItems: List<MenuDialogItem>,
    val onItemSelected: (DATA?, String) -> Unit
) :
    CommonDialogState() {
    var _data: DATA? = null

    fun show(data: DATA? = null) {
        _data = data

        show()
    }
}

@Composable
fun <T> rememberMenuDialogState(
    title: String,
    message: String? = null,
    menuItems: List<MenuDialogItem>,
    onItemSelected: (T?, String) -> Unit
): MenuDialogState<T> {
    return remember {
        MenuDialogState(title, message, menuItems, onItemSelected)
    }
}


