@file:OptIn(ExperimentalLayoutApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.LOCAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.shortXManager
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

class SymbolDropdownState(
    val input: (String) -> Unit,
    val wrapMath: () -> Unit,
    val undo: () -> Unit,
    val redo: () -> Unit
) {
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
fun rememberSymbolDropdownState(
    input: (String) -> Unit,
    wrapMath: () -> Unit,
    undo: () -> Unit,
    redo: () -> Unit,
): SymbolDropdownState {
    return remember {
        SymbolDropdownState(input, wrapMath, undo, redo)
    }
}

@Composable
fun SymbolDropdown(state: SymbolDropdownState) {
    val context = LocalContext.current
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = ShortXCardRoundedCornerShape)
    ) {
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
                    // Undo
                    IconButton(onClick = {
                        state.undo()
                    }) {
                        RemixIcon(
                            Remix.Arrows.arrow_go_back_line,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Redo
                    IconButton(onClick = {
                        state.redo()
                    }) {
                        RemixIcon(
                            Remix.Arrows.arrow_go_forward_line,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

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

                    TextButton(onClick = {
                        state.wrapMath()
                    }) {
                        Text(
                            LocalI18N.current["ui.common.enable.math"],
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

                    val allEnvs = shortXManager.getAllSystemEnv()
                    val envMenuDialogState =
                        rememberMenuDialogState<String>(
                            title = LocalI18N.current["ui.system.env"],
                            message = null,
                            menuItems = allEnvs.map {
                                MenuDialogItem(
                                    id = it.keyDecored,
                                    title = LocalI18N.current[it.displayName],
                                    summary = it.tip.takeIf { it.isNotBlank() }?.let {
                                        LocalI18N.current[it]
                                    },
                                    customAction = {
                                        if (it.requireParam) {
                                            Text(LocalI18N.current["ui.system.env.require.param"])
                                        }
                                    }
                                )
                            }
                        ) { _, id ->
                            state.input(id)
                            state.close()
                        }
                    MenuDialog(envMenuDialogState)
                    // System env
                    TextButton(onClick = {
                        envMenuDialogState.show()
                    }) {
                        Text(
                            LocalI18N.current["ui.system.env"],
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
}