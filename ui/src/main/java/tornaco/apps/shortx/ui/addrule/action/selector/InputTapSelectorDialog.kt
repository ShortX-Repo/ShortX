@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

class InputTapSelectorState(
    val onSelected: (Action.InputTap) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    private var _x by mutableStateOf("")
    val x get() = _x

    private var _y by mutableStateOf("")
    val y get() = _y


    fun show(current: Action.InputTap? = null) {
        this._x = current?.x?.toString() ?: ""
        this._y = current?.y?.toString() ?: ""
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }

    fun setX(text: String) {
        this._x = text
    }

    fun setY(text: String) {
        this._y = text
    }
}

@Composable
fun rememberInputTapSelectorState(
    onSelected: (Action.InputTap) -> Unit
): InputTapSelectorState {
    return remember {
        InputTapSelectorState(onSelected)
    }
}

@Composable
fun InputTapSelectorDialog(state: InputTapSelectorState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            InputTapSelector(state)
        }
    }
}

@Composable
private fun InputTapSelector(
    state: InputTapSelectorState
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(text = LocalI18N.current["ui.action.input.tap"]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            state.dismiss()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (state.x.isNotBlank() && state.x.isDigitsOnly() && state.y.isNotBlank() && state.y.isDigitsOnly()) {
                                state.onSelected(
                                    Action.InputTap(
                                        x = state.x.toIntOrNull() ?: 0,
                                        y = state.y.toIntOrNull() ?: 0
                                    )
                                )
                                state.dismiss()
                            }
                        }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                        }
                    },
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                ) {
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.x,
                        onValueChange = {
                            state.setX(it)
                        },
                        label = {
                            Text(LocalI18N.current["ui.action.input.tap.x"])
                        },
                        showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    StandardSpacer()

                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.y,
                        onValueChange = {
                            state.setY(it)
                        },
                        label = {
                            Text(LocalI18N.current["ui.action.input.tap.y"])
                        },
                        showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    StandardSpacer()
                    StandardSpacer()

                    SuggestedNavEntry()
                }
            }
        }
    }
}