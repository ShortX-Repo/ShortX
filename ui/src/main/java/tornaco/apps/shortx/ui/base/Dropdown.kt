package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


data class DropdownItem<T>(
    val labelLines: List<String>,
    val data: T,
    val icon: @Composable () -> Unit = {}
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
    widthFraction: Float = 0.6f,
    state: DropdownSelectorState = rememberDropdownSelectorState(),
    items: List<DropdownItem<T>>,
    onSelect: (DropdownItem<T>) -> Unit,
) {
    DropdownMenu(
        modifier = Modifier.fillMaxWidth(widthFraction),
        expanded = state.isMenuOpen,
        onDismissRequest = {
            state.close()
        },
    ) {
        items.forEach {
            DropdownMenuItem(text = {
                Row(verticalAlignment = CenterVertically) {
                    it.icon.invoke()
                    Spacer(modifier = Modifier.size(8.dp))
                    Column {
                        Text(text = it.labelLines[0], style = MaterialTheme.typography.titleSmall)
                        if (it.labelLines.size > 1) {
                            it.labelLines.subList(1, it.labelLines.size).forEach {
                                Text(text = it, style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                }
            }, onClick = {
                state.close()
                onSelect(it)
            })
        }
    }
}

@Composable
fun <T> DropdownSelector(
    state: DropdownSelectorState = rememberDropdownSelectorState(),
    items: Map<I18NKey, List<DropdownItem<T>>>,
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
            val sectionTitle = it.key

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 4.dp),
                text = sectionTitle,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.W600)
            )

            val dropItems = it.value

            dropItems.forEach {
                DropdownMenuItem(text = {
                    Row(verticalAlignment = CenterVertically) {
                        it.icon.invoke()
                        Spacer(modifier = Modifier.size(8.dp))
                        Column {
                            Text(
                                text = it.labelLines[0],
                                style = MaterialTheme.typography.titleSmall
                            )
                            if (it.labelLines.size > 1) {
                                it.labelLines.subList(1, it.labelLines.size).forEach {
                                    Text(text = it, style = MaterialTheme.typography.titleSmall)
                                }
                            }
                        }
                    }
                }, onClick = {
                    state.close()
                    onSelect(it)
                })
            }
        }
    }
}