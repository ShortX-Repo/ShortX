@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.TextFieldProp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils

class ShowTextFieldDialogSettingsDialogState(
    val onSelected: (Action.ShowTextFieldDialog) -> Unit
) : CommonDialogState() {

    var title by mutableStateOf("")
    var message by mutableStateOf("")
    var isCancelable by mutableStateOf(true)
    var items by mutableStateOf<List<TextFieldProp>>(emptyList())

    fun show(currentSettings: Action.ShowTextFieldDialog? = null) {
        this.title = currentSettings?.title ?: ""
        this.message = currentSettings?.message ?: ""
        this.isCancelable = currentSettings?.cancelable ?: true
        this.items = currentSettings?.textFields ?: emptyList()

        show()
    }

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateMessage(message: String) {
        this.message = message
    }

    fun updateCancelable(cancelable: Boolean) {
        this.isCancelable = cancelable
    }

    fun addItem(item: TextFieldProp) {
        this.items = this.items.toMutableList().apply {
            add(item)
        }
    }

    fun removeItem(index: Int) {
        this.items = this.items.toMutableList().apply {
            removeAt(index)
        }
    }

    fun updateItemPlaceholder(holder: String, index: Int) {
        this.items = this.items.toMutableList().apply {
            val current = get(index)
            val updated = TextFieldProp.newBuilder(current).setPlaceholder(holder).build()
            set(index, updated)
        }
    }
}

@Composable
fun rememberShowTextFieldDialogSettingsDialogState(
    onSelected: (Action.ShowTextFieldDialog) -> Unit
): ShowTextFieldDialogSettingsDialogState {
    return remember {
        ShowTextFieldDialogSettingsDialogState(onSelected)
    }
}

@Composable
fun ShowTextFieldDialogSettingsDialog(state: ShowTextFieldDialogSettingsDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShowTextFieldDialogSelector(state)
        }
    }
}

@Composable
private fun ShowTextFieldDialogSelector(dialogState: ShowTextFieldDialogSettingsDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.show.menu.dialog"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(
                                    Action.ShowTextFieldDialog(
                                        title = dialogState.title,
                                        message = dialogState.message,
                                        cancelable = dialogState.isCancelable,
                                        textFields = dialogState.items
                                    )
                                )
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = LocalI18N.current["ui.action.dialog.title"])
                        },
                        value = dialogState.title,
                        onValueChange = {
                            dialogState.updateTitle(it)
                        })
                    StandardSpacer()
                    StandardSpacer()

                    Items(dialogState)
                    StandardSpacer()

                    // Add icon
                    Button(onClick = {
                        dialogState.addItem(TextFieldProp.newBuilder().build())
                    }) {
                        Text(text = LocalI18N.current["ui.action.show.text.field.dialog.item.add"])
                    }

                    SectionSpacer()

                    SwitchRow(
                        text = LocalI18N.current["ui.action.dialog.cancelable"],
                        isChecked = dialogState.isCancelable
                    ) {
                        dialogState.updateCancelable(it)
                    }

                    LargeSpacer()

                    SuggestedNavEntry()
                }
            }
        }
    }

}

@Composable
private fun Items(
    state: ShowTextFieldDialogSettingsDialogState,
) {
    state.items.forEachIndexed { index, menuDialogUiItem ->
        ItemSettings(
            index = index,
            headerTitle = LocalI18N.current["ui.action.show.text.field.dialog.item"] + " ${index + 1}",
            placeholder = menuDialogUiItem.placeholder,
            onTextChange = {
                state.updateItemPlaceholder(it, index)
            },
            removeItem = {
                state.removeItem(index)
            }
        )

        StandardSpacer()
    }
}

@Composable
private fun ItemSettings(
    index: Int,
    headerTitle: String,
    placeholder: String,
    onTextChange: (String) -> Unit,
    removeItem: () -> Unit
) {
    val i18N = LocalI18N.current
    val context = LocalContext.current
    val contextKey = "{${ContextDataMapping.ShowTextFieldDialog.textFieldInput.name + (index + 1)}}"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f)) {
            MediumTitle(headerTitle)
        }

        IconButton(onClick = {
            removeItem()
        }) {
            RemixIcon(remixName = Remix.System.close_line)
        }
    }
    ShortXTextField(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = i18N["ui.action.show.text.field.dialog.item.placeholder"])
        },
        value = placeholder,
        onValueChange = {
            onTextChange(it)
        })
    MediumSpacer()
    TextButton(onClick = {
        ClipboardUtils.copyToClipboard(
            context,
            "key",
            contextKey
        )
        Toast.makeText(
            context,
            i18N["ui.common.copied.to.clipboard"],
            Toast.LENGTH_LONG
        ).show()
    }) {
        Text(text = i18N["ui.action.show.text.field.dialog.item.context.var", mapOf("var" to contextKey)])
    }
}