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
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


class ShowDialogSettingsDialogState(
    val onSelected: (Action.ShowAlertDialog) -> Unit
) {
    var isShowing by mutableStateOf(false)
    var currentSettings: Action.ShowAlertDialog? = null

    fun show(currentSettings: Action.ShowAlertDialog? = null) {
        this.currentSettings = currentSettings
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberShowDialogSettingsDialogState(
    onSelected: (Action.ShowAlertDialog) -> Unit
): ShowDialogSettingsDialogState {
    return remember {
        ShowDialogSettingsDialogState(onSelected)
    }
}

@Composable
fun ShowDialogSettingsDialog(state: ShowDialogSettingsDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShowDialogSelector(state)
        }
    }
}

@Composable
private fun ShowDialogSelector(dialogState: ShowDialogSettingsDialogState) {
    val vm = hiltViewModel<ShowDialogSelectorViewModel>().apply { resetState() }
    LaunchedEffect(vm) {
        dialogState.currentSettings?.let {
            vm.initFrom(it)
        }
    }
    val state: Action.ShowAlertDialog by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { LocalI18N.current["ui.action.show.dialog"] },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(state)
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) {
                Content(paddingValues = it, vm = vm)
            }
        }
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, vm: ShowDialogSelectorViewModel) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            shape = TextFieldDefaults.outlinedShape,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            label = {
                Text(text = LocalI18N.current["ui.action.dialog.title"])
            },
            value = state.title,
            onValueChange = {
                vm.updateTitle(it)
            })
        StandardSpacer()
        StandardSpacer()

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            shape = TextFieldDefaults.outlinedShape,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            label = {
                Text(text = LocalI18N.current["ui.action.dialog.message"])
            },
            value = state.message,
            onValueChange = {
                vm.updateMessage(it)
            })
        StandardSpacer()
        StandardSpacer()



        PositiveButton(state, vm)
        StandardSpacer()

        NegativeButton(state, vm)
        StandardSpacer()

        NeutralButton(state, vm)
        StandardSpacer()


        SwitchRow(
            text = LocalI18N.current["ui.action.dialog.cancelable"],
            isChecked = state.cancelable
        ) {
            vm.updateCancelable(it)
        }
        LargeSpacer()
    }
}


@Composable
private fun PositiveButton(
    state: Action.ShowAlertDialog,
    vm: ShowDialogSelectorViewModel
) {
    ButtonSettings(
        headerTitle = LocalI18N.current["ui.actions.dialog.positive.button"],
        text = state.positiveText ?: "",
        onTextChange = {
            vm.updateButtonText(it, DialogAction.Positive)
        },
        actions = state.positiveActions,
        removeAction = {
            vm.removePositiveAction(it)
        },
        addAction = {
            vm.addPositiveAction(it)
        })
}

@Composable
private fun NegativeButton(
    state: Action.ShowAlertDialog,
    vm: ShowDialogSelectorViewModel
) {
    ButtonSettings(
        headerTitle = LocalI18N.current["ui.actions.dialog.negative.button"],
        text = state.negativeText ?: "",
        onTextChange = {
            vm.updateButtonText(it, DialogAction.Negative)
        },
        actions = state.negativeActions,
        removeAction = {
            vm.removeNegativeAction(it)
        },
        addAction = {
            vm.addNegativeAction(it)
        })
}


@Composable
private fun NeutralButton(
    state: Action.ShowAlertDialog,
    vm: ShowDialogSelectorViewModel
) {
    ButtonSettings(
        headerTitle = LocalI18N.current["ui.actions.dialog.neutral.button"],
        text = state.neutralText ?: "",
        onTextChange = {
            vm.updateButtonText(it, DialogAction.Neutral)
        },
        actions = state.neutralActions,
        removeAction = {
            vm.removeNeutralAction(it)
        },
        addAction = {
            vm.addNeutralAction(it)
        })
}

@Composable
private fun ButtonSettings(
    headerTitle: String,
    text: String,
    onTextChange: (String) -> Unit,
    actions: List<Action>,
    addAction: (Action) -> Unit,
    removeAction: (Action) -> Unit,
) {
    MediumTitle(headerTitle)
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        shape = TextFieldDefaults.outlinedShape,
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
    TipRow(LocalI18N.current["ui.actions.dialog.button.optional"])
    TinySpacer()


    ActionContent(
        header = {
            SmallTitle(text = LocalI18N.current["ui.actions.title"])
        }, addAction = { action ->
            addAction(action)
        },
        removeAction = { action ->
            removeAction(action)
        },
        actions = actions
    )
}