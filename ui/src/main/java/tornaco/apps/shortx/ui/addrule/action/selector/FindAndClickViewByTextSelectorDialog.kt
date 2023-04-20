@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme

class FindAndClickViewByTextSelectorState(
    val onSelected: (Action.FindAndClickViewByText) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    private var _text by mutableStateOf("")
    val text get() = _text

    private var _isRegex by mutableStateOf(false)
    val isRegex get() = _isRegex

    fun show(current: Action.FindAndClickViewByText? = null) {
        this._text = current?.text ?: ""
        this._isRegex = current?.isRegex ?: false
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }

    fun setText(text: String) {
        _text = text
    }

    fun setIsRegex(isRegex: Boolean) {
        _isRegex = isRegex
    }
}

@Composable
fun rememberFindAndClickViewByTextSelectorState(
    onSelected: (Action.FindAndClickViewByText) -> Unit
): FindAndClickViewByTextSelectorState {
    return remember {
        FindAndClickViewByTextSelectorState(onSelected)
    }
}

@Composable
fun FindAndClickViewByTextSelectorDialog(state: FindAndClickViewByTextSelectorState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            FindAndClickViewByTextSelector(state)
        }
    }
}

@Composable
private fun FindAndClickViewByTextSelector(
    state: FindAndClickViewByTextSelectorState
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                TopAppBar(
                    modifier = Modifier,
                    title = { Text(text = LocalI18N.current["ui.action.find.click.view.by.text"]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            state.dismiss()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (state.text.isNotBlank()) {
                                state.onSelected(
                                    Action.FindAndClickViewByText(
                                        text = state.text,
                                        isRegex = state.isRegex
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
                    SmallTitle(LocalI18N.current["ui.action.find.click.view.by.text.text"])
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                        shape = TextFieldDefaults.outlinedShape,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        value = state.text,
                        onValueChange = {
                            state.setText(it)
                        })
                    StandardSpacer()

                    SmallTitle(LocalI18N.current["ui.action.find.click.view.by.text.is.regex"])
                    Switch(checked = state.isRegex, onCheckedChange = { state.setIsRegex(it) })
                }
            }
        }
    }
}