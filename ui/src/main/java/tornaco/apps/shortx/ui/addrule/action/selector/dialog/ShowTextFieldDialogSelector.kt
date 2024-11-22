@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector.dialog

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.TextFieldProp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.CommonSortDialog
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SortItem
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.rememberCommonSortState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.ui.util.sortByIndex

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

    fun applySort(items: List<SortItem>) {
        val sorted = sortByIndex(this.items, items.map { it.index })
        this.items = sorted
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
                                        textFields = dialogState.items,
                                        style = tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings.getDefaultInstance()
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sortDialogState = rememberCommonSortState(
                            apply = {
                                dialogState.applySort(it)
                            })
                        CommonSortDialog(
                            state = sortDialogState,
                            items = dialogState.items.mapIndexed { index, menuDialogUiItem ->
                                SortItem(index, menuDialogUiItem.label)
                            })

                        FieldTitle(text = LocalI18N.current["ui.action.show.menu.dialog.item"])
                        // Sort
                        AnimatedVisibility(visible = dialogState.items.size > 1) {
                            IconButton(onClick = {
                                sortDialogState.show()
                            }) {
                                RemixIcon(Remix.Editor.sort_asc)
                            }
                        }
                    }
                    MediumSpacer()
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