@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOpPayload
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.toDisplayName
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.contextdata.ContextVarSelectorDialog
import tornaco.apps.shortx.ui.contextdata.rememberContextVarSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.util.UUID

class EvaluateContextVarSelectorDialogState(
    val onSelected: (Condition.EvaluateContextVar) -> Unit
) : CommonDialogState() {
    private val newDefaultAction: () -> Condition.EvaluateContextVar = {
        Condition.EvaluateContextVar(
            varName = "",
            op = GlobalVarOp.EqualTo,
            payload = GlobalVarOpPayload.newBuilder().setValue("").build(),
        )
    }

    private var _action by mutableStateOf(newDefaultAction())
    val action get() = _action

    var showPayloadInputField by mutableStateOf(false)

    private var _output by mutableStateOf("")
    val output get() = _output

    fun show(current: Condition.EvaluateContextVar? = null) {
        _action = current ?: newDefaultAction()
        _output = ""
        checkShowPayloadInputField()

        show()
    }

    fun selectContextVar(varName: String) {
        _action = _action.copy(
            varName = varName
        )
        setOutput("")
    }

    fun inputValue(value: String) {
        _action = _action.copy(
            payload = GlobalVarOpPayload.newBuilder().setValue(value).build(),
        )
        setOutput("")
    }

    infix fun setOp(op: GlobalVarOp) {
        _action = _action.copy(
            op = op
        )
        setOutput("")
        checkShowPayloadInputField()
    }

    private fun checkShowPayloadInputField() {
        val op = _action.op
        showPayloadInputField =
            op != GlobalVarOp.HasBeenSet && op != GlobalVarOp.NotSet && op != GlobalVarOp.IsEmpty && op != GlobalVarOp.IsNotEmpty
                    && op != GlobalVarOp.IsExists && op != GlobalVarOp.IsNotExists
    }

    fun canSave(): Boolean {
        return action.varName.isNotEmpty()
    }

    private fun setOutput(output: String) {
        _output = output
    }

    fun evaluate() {
        val res = shortXManager.evaluateCondition(
            EvaluateContext.newBuilder()
                .setSrcId(UUID.randomUUID().toString())
                .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                .setSrcTitle("Testing context var")
                .build(),
            action.toProtoCondition().pack_()
        )
        setOutput(res?.toString() ?: "FAIL TO EVALUATE")
    }
}

@Composable
fun rememberEvaluateContextVarSelectorState(
    onSelected: (Condition.EvaluateContextVar) -> Unit
): EvaluateContextVarSelectorDialogState {
    return remember {
        EvaluateContextVarSelectorDialogState(onSelected)
    }
}

@Composable
fun EvaluateContextVarSelectorDialog(state: EvaluateContextVarSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            EvaluateContextVarSelector(state)
        }
    }
}

@Composable
private fun EvaluateContextVarSelector(dialogState: EvaluateContextVarSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.conditions.eva.context.var"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.evaluate()
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = "Evaluate"
                                )
                            }

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
                        .verticalScroll(rememberScrollState())
                ) {
                    val contextVarSelectorDialogState = rememberContextVarSelectorState {
                        dialogState.selectContextVar(it)
                    }
                    ContextVarSelectorDialog(contextVarSelectorDialogState)

                    val payloadValue = remember(dialogState.action.payload) {
                        dialogState.action.payload.value
                    }

                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dialogState.action.varName,
                        label = {
                            Text(LocalI18N.current["ui.action.select.global.var"])
                        },
                        trailingIcon = {
                            IconButton(onClick = { contextVarSelectorDialogState.show() }) {
                                RemixIcon(remixName = Remix.System.search_2_line)
                            }
                        },
                        onValueChange = {
                            dialogState.selectContextVar(it)
                        })

                    StandardSpacer()

                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.action.select.global.var.op"]
                    )
                    TinySpacer()
                    val opSelectorState = rememberDropdownSelectorState()
                    Box {
                        FilledTonalButton(
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ColorDefaults.backgroundSurfaceColor(
                                    1.dp
                                )
                            ),
                            onClick = {
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
                    StandardSpacer()

                    AnimatedVisibility(visible = dialogState.showPayloadInputField) {
                        Column {
                            SmallTitle(
                                modifier = Modifier.padding(vertical = 12.dp),
                                text = LocalI18N.current["ui.action.select.global.var.payload"]
                            )
                            TinySpacer()
                            ShortXTextField(value = payloadValue, onValueChange = {
                                dialogState.inputValue(it)
                            })
                            StandardSpacer()
                        }
                    }

                    AnimatedVisibility(visible = dialogState.output.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 72.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = dialogState.output,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        StandardSpacer()
                    }
                }
            }
        }
    }
}