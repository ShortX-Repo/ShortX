@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme


class KeyValueEditDialogState(val title: String) : CommonDialogState() {
    var key: String by mutableStateOf("")
    var value: String by mutableStateOf("")

    var callback: (Pair<String, String>) -> Unit = {}

    fun show(key: String, value: String, callback: (Pair<String, String>) -> Unit) {
        this.key = key
        this.value = value
        this.callback = callback

        show()
    }
}

@Composable
fun rememberKeyValueEditDialogState(title: String): KeyValueEditDialogState {
    return remember {
        KeyValueEditDialogState(title)
    }
}

@Composable
fun KeyValueEditDialog(state: KeyValueEditDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = {
            state.dismiss()
        }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = state.title) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        if (state.key.isNotEmpty()) {
                                            state.callback(state.key to state.value)
                                            state.dismiss()
                                        }
                                    }) {
                                        RemixIcon(remixName = Remix.System.check_line)
                                    }
                                },
                            )
                        }) { paddingValues ->
                        ClickOutsideClearFocusBox {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                ShortXTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.key,
                                    onValueChange = {
                                        state.key = it
                                    },
                                    label = {
                                        Text(text = "Key")
                                    },
                                    showSymbolButton = false
                                )
                                StandardSpacer()
                                ShortXTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.value,
                                    onValueChange = {
                                        state.value = it
                                    },
                                    label = {
                                        Text(text = "Value")
                                    },
                                    showSymbolButton = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}