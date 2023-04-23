@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOpPayload
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.toDisplayName
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.gvar.GlobalVarSelectorDialog
import tornaco.apps.shortx.ui.gvar.rememberGlobalVarSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme

class EvaluateGlobalVarSelectorDialogState(
    val onSelected: (Condition.EvaluateGlobalVar) -> Unit
) : CommonDialogState() {

    private var _action by mutableStateOf(
        Condition.EvaluateGlobalVar(
            varId = "",
            op = GlobalVarOp.EqualTo,
            payload = GlobalVarOpPayload.newBuilder().setValue("").build(),
        )
    )

    val action get() = _action

    fun selectGlobalVar(globalVar: GlobalVar) {
        _action = _action.copy(
            varId = globalVar.id
        )
    }

    fun inputValue(value: String) {
        _action = _action.copy(
            payload = GlobalVarOpPayload.newBuilder().setValue(value).build(),
        )
    }

    infix fun setOp(data: GlobalVarOp) {
        _action = _action.copy(
            op = data
        )
    }

    fun canSave(): Boolean {
        return action.varId.isNotEmpty()
    }

    fun evaluate() {

    }
}

@Composable
fun rememberEvaluateGlobalVarSelectorState(
    onSelected: (Condition.EvaluateGlobalVar) -> Unit
): EvaluateGlobalVarSelectorDialogState {
    return remember {
        EvaluateGlobalVarSelectorDialogState(onSelected)
    }
}

@Composable
fun EvaluateGlobalVarSelectorDialog(state: EvaluateGlobalVarSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            EvaluateGlobalVarSelector(state)
        }
    }
}

@Composable
private fun EvaluateGlobalVarSelector(dialogState: EvaluateGlobalVarSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.dialog.title.select.app"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (dialogState.canSave()) {
                                    dialogState.dismiss()
                                    dialogState.onSelected(dialogState.action)
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    val globalVarToWriteSelectorState = rememberGlobalVarSelectorState {
                        dialogState.selectGlobalVar(it)
                    }
                    GlobalVarSelectorDialog(globalVarToWriteSelectorState)

                    val selectedVarName = remember(dialogState.action.varId) {
                        shortXManager.getGlobalVarById(dialogState.action.varId)?.name
                    }
                    val payloadValue = remember(dialogState.action.payload) {
                        dialogState.action.payload.value
                    }

                    TipBody(text = LocalI18N.current["ui.action.select.global.var"])
                    FilledTonalButton(onClick = {
                        globalVarToWriteSelectorState.show()
                    }) {
                        if (selectedVarName != null) {
                            Text(text = selectedVarName)
                        } else {
                            RemixIcon(remixName = Remix.System.add_line)
                        }
                    }

                    StandardSpacer()

                    TipBody(text = LocalI18N.current["ui.action.select.global.var.op"])
                    val opSelectorState = rememberDropdownSelectorState()
                    Box {
                        FilledTonalButton(onClick = {
                            opSelectorState.open()
                        }) {
                            Text(dialogState.action.op.toDisplayName())
                        }
                        DropdownSelector(
                            state = opSelectorState,
                            items = GlobalVarOp.values().toMutableList().apply {
                                remove(GlobalVarOp.UNRECOGNIZED)
                            }.map {
                                DropdownItem(
                                    labelLines = listOf(it.toDisplayName()),
                                    data = it,
                                )
                            }
                        ) {
                            dialogState setOp (it.data)
                        }
                    }

                    TipBody(text = LocalI18N.current["ui.action.select.global.var.payload"])
                    OutlinedTextField(value = payloadValue, onValueChange = {
                        dialogState.inputValue(it)
                    })

                }
            }
        }
    }
}