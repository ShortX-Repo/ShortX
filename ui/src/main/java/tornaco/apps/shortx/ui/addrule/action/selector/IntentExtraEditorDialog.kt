@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtra
import tornaco.apps.shortx.core.proto.common.AndroidIntentExtraType
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class IntentExtraEditDialogState(val title: String, val onSelected: (AndroidIntentExtra) -> Unit) :
    CommonDialogState() {
    var key: String by mutableStateOf("")
    var value: String by mutableStateOf("")
    var type: String by mutableStateOf(AndroidIntentExtraType.String.name)

    fun show(extra: AndroidIntentExtra) {
        this.key = extra.key
        this.value = extra.value
        this.type = extra.type.name
        show()
    }
}

@Composable
fun rememberIntentExtraEditDialogState(
    title: String,
    onSelected: (AndroidIntentExtra) -> Unit
): IntentExtraEditDialogState {
    return remember {
        IntentExtraEditDialogState(title, onSelected)
    }
}

@Composable
fun IntentExtraEditDialog(state: IntentExtraEditDialogState) {
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
                                            state.onSelected(
                                                AndroidIntentExtra.newBuilder()
                                                    .setKey(state.key)
                                                    .setValue(state.value)
                                                    .setType(AndroidIntentExtraType.valueOf(state.type))
                                                    .build()
                                            )
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
                                        Text(text = LocalI18N.current[I18N_R.UI_COMMON_EXTRAS_K])
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
                                        Text(text = LocalI18N.current[I18N_R.UI_COMMON_EXTRAS_V])
                                    },
                                    showSymbolButton = false
                                )
                                StandardSpacer()
                                // Type.
                                Box(modifier = Modifier.weight(1f)) {
                                    val typeDropdownState = rememberDropdownSelectorState()

                                    DropdownSelector(
                                        state = typeDropdownState,
                                        items = AndroidIntentExtraType.entries
                                            .filter { it != AndroidIntentExtraType.UNRECOGNIZED }
                                            .map {
                                                DropdownItem(
                                                    labelLines = listOf(it.name),
                                                    data = it
                                                )
                                            }
                                    ) {
                                        state.type = it.data.name
                                    }

                                    FilledTonalButton(onClick = {
                                        typeDropdownState.open()
                                    }) {
                                        Row(verticalAlignment = CenterVertically) {
                                            Text(text = state.type)
                                            StandardSpacer()
                                            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}