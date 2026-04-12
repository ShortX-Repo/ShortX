@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.toDisplayName
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme

data class IntOpSelectorRes(val value: Int, val op: IntOp)


class IntOpSelectorState(
    val title: String,
    val valueTitle: String,
    val maxValue: Int? = null,
    val evaluate: (IntOpSelectorRes) -> String,
    val onSelected: (IntOpSelectorRes) -> Unit
) : CommonDialogState() {

    private var _currentValue by mutableStateOf(
        IntOpSelectorRes(
            value = 0,
            op = IntOp.IntEqualTo,
        )
    )

    val currentValue get() = _currentValue

    private var _output by mutableStateOf("")
    val output get() = _output

    fun show(current: IntOpSelectorRes? = null) {
        current?.let {
            inputValue(it.value.toString())
            setOp(it.op)
        }
        show()
    }

    fun inputValue(value: String) {
        val parsedValue = value.toIntOrNull() ?: 0
        _currentValue = _currentValue.copy(
            value = maxValue?.let { max -> parsedValue.coerceIn(0, max) }
                ?: parsedValue.coerceAtLeast(0)
        )
        setOutput("")
    }

    infix fun setOp(data: IntOp) {
        _currentValue = _currentValue.copy(
            op = data
        )
        setOutput("")
    }

    private fun setOutput(output: String) {
        _output = output
    }

    fun evaluate() {
        val res = evaluate(currentValue)
        setOutput(res)
    }
}

@Composable
fun rememberIntOpSelectorState(
    title: String,
    valueTitle: String,
    maxValue: Int? = null,
    evaluate: (IntOpSelectorRes) -> String,
    onSelected: (IntOpSelectorRes) -> Unit
): IntOpSelectorState {
    return remember {
        IntOpSelectorState(title, valueTitle, maxValue, evaluate, onSelected)
    }
}

@Composable
fun IntOpSelectorDialog(state: IntOpSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            IntOpSelector(state)
        }
    }
}

@Composable
private fun IntOpSelector(dialogState: IntOpSelectorState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = dialogState.title) },
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
                                dialogState.dismiss()
                                dialogState.onSelected(dialogState.currentValue)
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
                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.conditions.battery.percent.op"]
                    )
                    TinySpacer()
                    val opSelectorState = rememberDropdownSelectorState()
                    Box {
                        FilledTonalButton(
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ColorDefaults.backgroundSurfaceColor(
                                    1.dp
                                )
                            ), onClick = {
                                opSelectorState.open()
                            }) {
                            Text(dialogState.currentValue.op.toDisplayName())
                        }
                        DropdownSelector(
                            state = opSelectorState,
                            items = IntOp.entries.toMutableList().apply {
                                remove(IntOp.UNRECOGNIZED)
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

                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = dialogState.valueTitle
                    )
                    TinySpacer()
                    ShortXTextField(
                        value = dialogState.currentValue.value.let {
                            if (it <= 0) "" else it.toString()
                        }, onValueChange = {
                            dialogState.inputValue(it)
                        }, showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    StandardSpacer()
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