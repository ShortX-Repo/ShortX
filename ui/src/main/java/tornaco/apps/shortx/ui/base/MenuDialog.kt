package tornaco.apps.shortx.ui.base

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun MenuDialog(
    state: MenuDialogState,
) {
    if (state.isShowing) {
        ShortXDialog(
            onDismissRequest = {
                state.dismiss()
            },

            title = {
                Text(text = state.title)
            }) {

            state.menuItems.forEach { dialogItem ->
                ListItem(
                    title = dialogItem.title,
                    icon = {
                        dialogItem.iconName?.let {
                            RemixIcon(remixName = it)
                        }
                    },
                    onClick = {
                        dialogItem.onClick()
                        state.dismiss()
                    })
            }

        }
    }
}


data class MenuDialogItem(val title: String, val iconName: String? = null, val onClick: () -> Unit)

class MenuDialogState(
    val title: String,
    val menuItems: List<MenuDialogItem>
) :
    CommonDialogState()

@Composable
fun rememberMenuDialogState(
    title: String,
    menuItems: List<MenuDialogItem>
): MenuDialogState {
    return remember {
        MenuDialogState(title, menuItems)
    }
}


