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
import tornaco.apps.shortx.core.proto.common.ScreenOnTimeStartFrom
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
import tornaco.apps.shortx.ui.addrule.fact.selector.ScreenOnFromButton
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
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.util.UUID

class EvaluateScreenOnTimeSelectorDialogState(
    val onSelected: (Condition.EvaluateScreenOnTime) -> Unit
) : CommonDialogState() {
    private val newDefaultCondition: () -> Condition.EvaluateScreenOnTime = {
        Condition.EvaluateScreenOnTime(
            op = GlobalVarOp.EqualTo,
            payload = GlobalVarOpPayload.newBuilder().setValue("").build(),
            from = ScreenOnTimeStartFrom.ScreenOnTimeStartFrom_LastScreenOff
        )
    }

    private var _condition by mutableStateOf(newDefaultCondition())
    val condition get() = _condition

    var showPayloadInputField by mutableStateOf(false)

    private var _output by mutableStateOf("")
    val output get() = _output

    fun show(current: Condition.EvaluateScreenOnTime? = null) {
        _condition = current ?: newDefaultCondition()
        _output = ""
        checkShowPayloadInputField()

        show()
    }

    fun setFrom(from: ScreenOnTimeStartFrom) {
        _condition = _condition.copy(
            from = from
        )
        setOutput("")
    }

    fun inputValue(value: String) {
        _condition = _condition.copy(
            payload = GlobalVarOpPayload.newBuilder().setValue(value).build(),
        )
        setOutput("")
    }

    infix fun setOp(op: GlobalVarOp) {
        _condition = _condition.copy(
            op = op
        )
        setOutput("")
        checkShowPayloadInputField()
    }

    private fun checkShowPayloadInputField() {
        val op = _condition.op
        showPayloadInputField =
            op != GlobalVarOp.HasBeenSet && op != GlobalVarOp.NotSet && op != GlobalVarOp.IsEmpty && op != GlobalVarOp.IsNotEmpty
                    && op != GlobalVarOp.IsExists && op != GlobalVarOp.IsNotExists
    }

    fun canSave(): Boolean {
        return true
    }

    private fun setOutput(output: String) {
        _output = output
    }

    fun evaluate() {
        val res = shortXManager.evaluateCondition(
            EvaluateContext.newBuilder()
                .setSrcId(UUID.randomUUID().toString())
                .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                .setSrcTitle("Testing screen on time")
                .build(),
            condition.toProtoCondition().pack_()
        )
        setOutput(res?.toString() ?: "FAIL TO EVALUATE")
    }
}

@Composable
fun rememberEvaluateScreenOnTimeSelectorState(
    onSelected: (Condition.EvaluateScreenOnTime) -> Unit
): EvaluateScreenOnTimeSelectorDialogState {
    return remember {
        EvaluateScreenOnTimeSelectorDialogState(onSelected)
    }
}

@Composable
fun EvaluateScreenOnTimeSelectorDialog(state: EvaluateScreenOnTimeSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            EvaluateScreenOnTimeSelector(state)
        }
    }
}

@Composable
private fun EvaluateScreenOnTimeSelector(dialogState: EvaluateScreenOnTimeSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.fact.screen.on.time"]) },
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
                                    dialogState.onSelected(dialogState.condition)
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
                    ScreenOnFromButton(dialogState.condition.from) {
                        dialogState.setFrom(it)
                    }
                    StandardSpacer()

                    val payloadValue = remember(dialogState.condition.payload) {
                        dialogState.condition.payload.value
                    }
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
                            Text(dialogState.condition.op.toDisplayName())
                        }
                        DropdownSelector(
                            state = opSelectorState,
                            items = GlobalVarOp.entries.toMutableList().apply {
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
                            }, label = {
                                Text(LocalI18N.current["ui.fact.screen.on.time.seconds"])
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