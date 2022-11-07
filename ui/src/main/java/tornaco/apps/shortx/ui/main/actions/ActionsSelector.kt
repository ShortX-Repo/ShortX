package tornaco.apps.shortx.ui.main.actions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


class ActionsSelectorState {
    private var _isMenuOpen by mutableStateOf(false)
    val isMenuOpen get() = _isMenuOpen

    fun open() {
        _isMenuOpen = true
    }

    fun close() {
        _isMenuOpen = false
    }
}

@Composable
fun rememberActionsSelectorState(): ActionsSelectorState {
    return remember {
        ActionsSelectorState()
    }
}

@Composable
fun ActionsSelector(
    state: ActionsSelectorState = rememberActionsSelectorState(),
    availableGestureActions: List<String>,
    setAction: (String) -> Unit,
) {
    DropdownMenu(
        modifier = Modifier.fillMaxWidth(.68f),
        expanded = state.isMenuOpen,
        onDismissRequest = {
            state.close()
        },
    ) {
        availableGestureActions.forEach {
            DropdownMenuItem(text = {
                Text(it)
            }, onClick = {
                state.close()
                setAction(it)
            })
        }
    }
}