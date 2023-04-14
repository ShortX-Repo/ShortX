package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import github.tornaco.shortx.android.icon.Remix
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXDialog

class ShellCommandSelectorState(val onSelected: (String) -> Unit) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberShellCommandSelectorState(onSelected: (String) -> Unit): ShellCommandSelectorState {
    return remember {
        ShellCommandSelectorState(onSelected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShellCommandSelector(state: ShellCommandSelectorState) {
    var commandString by remember(state) {
        mutableStateOf("")
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = "Shell Command")
        }, buttons = {
            TextButton(onClick = {
                if (commandString.isNotBlank()) {
                    state.onSelected(commandString)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    leadingIcon = {
                        RemixIcon(Remix.Development.terminal_line)
                    },
                    shape = TextFieldDefaults.outlinedShape,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    label = {
                        Text(text = "Command")
                    },
                    value = commandString,
                    onValueChange = {
                        commandString = it
                    })
            }
        })
    }
}