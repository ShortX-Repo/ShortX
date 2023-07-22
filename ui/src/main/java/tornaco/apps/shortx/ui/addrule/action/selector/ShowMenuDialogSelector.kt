@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.action.DialogMenuItem
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.ActionHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


data class MenuDialogUiItem(
    val title: String,
    val actionHandler: ActionContentStateHandler
)


class ShowMenuDialogSettingsDialogState(
    val onSelected: (Action.ShowMenuDialog) -> Unit
) : CommonDialogState() {

    var title by mutableStateOf("")
    var message by mutableStateOf("")
    var isCancelable by mutableStateOf(true)
    var items by mutableStateOf<List<MenuDialogUiItem>>(emptyList())

    fun show(currentSettings: Action.ShowMenuDialog? = null) {
        this.title = currentSettings?.title ?: ""
        this.message = currentSettings?.message ?: ""
        this.isCancelable = currentSettings?.cancelable ?: true
        this.items = currentSettings?.items?.map { dialogMenuItem ->
            MenuDialogUiItem(dialogMenuItem.title, ActionContentStateHandler().apply {
                updateState {
                    copy(actions = dialogMenuItem.clickActionsList.mapNotNull {
                        it.toAction(shortXManager = tornaco.apps.shortx.core.shortXManager)
                    })
                }
            })
        } ?: emptyList()

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

    fun addItem(item: MenuDialogUiItem) {
        this.items = this.items.toMutableList().apply {
            add(item)
        }
    }

    fun removeItem(index: Int) {
        this.items = this.items.toMutableList().apply {
            removeAt(index)
        }
    }

    fun updateItemTitle(title: String, index: Int) {
        this.items = this.items.toMutableList().apply {
            val current = get(index)
            val updated = current.copy(title = title)
            set(index, updated)
        }
    }

    fun addButton() {
        this.items = this.items.toMutableList().apply {
            add(MenuDialogUiItem("", ActionContentStateHandler()))
        }
    }


}

@Composable
fun rememberShowMenuDialogSettingsDialogState(
    onSelected: (Action.ShowMenuDialog) -> Unit
): ShowMenuDialogSettingsDialogState {
    return remember {
        ShowMenuDialogSettingsDialogState(onSelected)
    }
}

@Composable
fun ShowMenuDialogSettingsDialog(state: ShowMenuDialogSettingsDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShowMenuDialogSelector(state)
        }
    }
}

@Composable
private fun ShowMenuDialogSelector(dialogState: ShowMenuDialogSettingsDialogState) {
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
                                    Action.ShowMenuDialog(
                                        title = dialogState.title,
                                        message = dialogState.message,
                                        cancelable = dialogState.isCancelable,
                                        items = dialogState.items.map { uiItem ->
                                            DialogMenuItem.newBuilder()
                                                .setTitle(uiItem.title)
                                                .addAllClickActions(uiItem.actionHandler.actions.map {
                                                    it.toProtoAction().pack_()
                                                })
                                                .build()
                                        }
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
                        dialogState.addButton()
                    }) {
                        Text(text = LocalI18N.current["ui.action.show.add.button"])
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
    state: ShowMenuDialogSettingsDialogState,
) {
    state.items.forEachIndexed { index, menuDialogUiItem ->
        ItemSettings(
            headerTitle = LocalI18N.current["ui.action.show.menu.dialog.item"] + " ${index + 1}",
            text = menuDialogUiItem.title,
            onTextChange = {
                state.updateItemTitle(it, index)
            },
            actions = menuDialogUiItem.actionHandler.actions,
            warnings = emptyMap(),
            actionHandler = menuDialogUiItem.actionHandler.asActionHandler(),
            removeItem = {
                state.removeItem(index)
            }
        )

        StandardSpacer()
    }
}

@Composable
private fun ItemSettings(
    headerTitle: String,
    text: String,
    onTextChange: (String) -> Unit,
    actions: List<Action>,
    warnings: Map<String, List<String>>,
    actionHandler: ActionHandler,
    removeItem: () -> Unit
) {
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

        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent
        ),
        label = {
            Text(text = LocalI18N.current["ui.action.dialog.button.text"])
        },
        value = text,
        onValueChange = {
            onTextChange(it)
        })
    TinySpacer()

    ActionContent(
        header = {
            SmallTitle(
                modifier = Modifier.padding(vertical = 12.dp),
                text = LocalI18N.current["ui.actions.title"]
            )
        },
        actions = actions,
        handler = actionHandler,
        warnings = warnings
    )
}