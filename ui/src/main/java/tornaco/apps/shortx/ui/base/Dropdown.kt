package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


data class DropdownItem<T>(
    val label: String,
    val data: T,
    val icon: ImageVector? = null,
)

class DropdownSelectorState {
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
fun rememberDropdownSelectorState(): DropdownSelectorState {
    return remember {
        DropdownSelectorState()
    }
}

@Composable
fun <T> DropdownSelector(
    state: DropdownSelectorState = rememberDropdownSelectorState(),
    items: List<DropdownItem<T>>,
    onSelect: (DropdownItem<T>) -> Unit,
) {
    DropdownMenu(
        modifier = Modifier.fillMaxWidth(.68f),
        expanded = state.isMenuOpen,
        onDismissRequest = {
            state.close()
        },
    ) {
        items.forEach {
            DropdownMenuItem(text = {
                Row(verticalAlignment = CenterVertically) {
                    it.icon?.let { vector ->
                        Icon(imageVector = vector, contentDescription = it.label)
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = it.label)
                }
            }, onClick = {
                state.close()
                onSelect(it)
            })
        }
    }
}