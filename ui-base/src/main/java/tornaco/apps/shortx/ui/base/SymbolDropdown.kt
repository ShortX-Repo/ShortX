@file:OptIn(ExperimentalLayoutApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.LOCAL_VAR_REF_PREFIX
import tornaco.apps.shortx.ui.util.ClipboardUtils

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
    val context = LocalContext.current
    DropdownMenu(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(120.dp),
        expanded = state.isMenuOpen,
        onDismissRequest = {
            state.close()
        },
        content = {
            FlowRow {
                // Paste
                TextButton(onClick = {
                    val clipContent: String? = ClipboardUtils.readClipboard(context)
                    clipContent?.let { state.input(it) }
                }) {
                    Text(
                        stringResource(id = android.R.string.paste),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // GV
                TextButton(onClick = {
                    state.input(GLOBAL_VAR_REF_PREFIX)
                }) {
                    Text(
                        LocalI18N.current["ui.global.var.title"],
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // LocalVar
                TextButton(onClick = {
                    state.input(LOCAL_VAR_REF_PREFIX)
                }) {
                    Text(
                        LocalI18N.current["ui.local.var.title"],
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                symbols1.forEach {
                    TextButton(onClick = {
                        state.input(it)
                    }) {
                        Text(
                            it, color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    )
}