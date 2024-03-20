@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

class InputTapSelectorState(
    val onSelected: (Action.InputTap) -> Unit
) : CommonDialogState() {
    private var _x by mutableStateOf("")
    val x get() = _x

    private var _y by mutableStateOf("")
    val y get() = _y


    fun show(current: Action.InputTap? = null) {
        this._x = current?.x.orEmpty()
        this._y = current?.y.orEmpty()

        super.show()
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
    if (state.isShowing) {
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
                            if (state.x.isNotBlank()
                                && state.y.isNotBlank()
                            ) {
                                state.onSelected(
                                    Action.InputTap(
                                        x = state.x,
                                        y = state.y
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