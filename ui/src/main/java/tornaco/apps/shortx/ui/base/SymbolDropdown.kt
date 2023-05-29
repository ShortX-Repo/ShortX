@file:OptIn(ExperimentalLayoutApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

val symbols1 = arrayOf(
    "{",
    "}",
    ",",
    "$",
    "[",
    "]",
    "(",
    ")",
    "()",
    "+",
    "!",
    "-",
    "*",
    ">",
    "<",

    ":",
    ".",
    ";",
    "?",
    "!",
    "/",
    "'",
    "\"",
    "\\",
)

class SymbolDropdownState(val input: (String) -> Unit) {
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
fun rememberSymbolDropdownState(input: (String) -> Unit): SymbolDropdownState {
    return remember {
        SymbolDropdownState(input)
    }
}

@Composable
fun SymbolDropdown(state: SymbolDropdownState) {
    DropdownMenu(
        modifier = Modifier.fillMaxWidth(DROPDOWN_WIDTH_FRACTION),
        expanded = state.isMenuOpen,
        onDismissRequest = {
            state.close()
        },
        content = {
            FlowRow {
                symbols1.forEach {
                    TextButton(onClick = {
                        state.input(it)
                    }) {
                        Text(it)
                    }
                }
            }
        }
    )
}