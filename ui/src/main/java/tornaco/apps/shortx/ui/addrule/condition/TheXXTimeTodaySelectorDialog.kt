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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.proto.condition.TheXXTimeTodayScope
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.toDisplayName
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
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
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class TheXXTimeTodaySelectorState(
    val onSelected: (Condition.TheXXTimeToday) -> Unit
) : CommonDialogState() {

    private var _currentValue by mutableStateOf(
        Condition.TheXXTimeToday(
            what = "",
            scope = TheXXTimeTodayScope.Today,
            time = 0,
            op = IntOp.IntEqualTo,
        )
    )

    val currentValue get() = _currentValue

    private var _output by mutableStateOf("")
    val output get() = _output

    fun show(current: Condition.TheXXTimeToday? = null) {
        current?.let {
            inputValue(it.time.toString())
            setOp(it.op)
            setWhat(it.what)
            setScope(it.scope)
        }
        show()
    }

    fun inputValue(value: String) {
        _currentValue = _currentValue.copy(
            time = minOf(100, value.toIntOrNull() ?: 0)
        )
        setOutput("")
    }

    infix fun setOp(data: IntOp) {
        _currentValue = _currentValue.copy(
            op = data
        )
        setOutput("")
    }

    infix fun setScope(scope: TheXXTimeTodayScope) {
        _currentValue = _currentValue.copy(
            scope = scope
        )
        setOutput("")
    }

    infix fun setWhat(what: String) {
        _currentValue = _currentValue.copy(
            what = what
        )
        setOutput("")
    }

    private fun setOutput(output: String) {
        _output = output
    }
}

@Composable
fun rememberTheXXTimeTodaySelectorState(
    onSelected: (Condition.TheXXTimeToday) -> Unit
): TheXXTimeTodaySelectorState {
    return remember {
        TheXXTimeTodaySelectorState(onSelected)
    }
}

@Composable
fun TheXXTimeTodaySelectorDialog(state: TheXXTimeTodaySelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            TheXXTimeTodaySelector(state)
        }
    }
}

@Composable
private fun TheXXTimeTodaySelector(dialogState: TheXXTimeTodaySelectorState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current[I18N_R.UI_CONDITIONS_THE_XX_TIME_WITHIN]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
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
                    TipBody(text = LocalI18N.current[I18N_R.UI_CONDITIONS_THE_XX_TIME_WITHIN_MESSAGE])
                    StandardSpacer()


                    FieldTitle(text = LocalI18N.current[I18N_R.UI_CONDITIONS_THE_XX_TIME_WITHIN_WHAT])
                    TinySpacer()
                    ShortXTextField(value = dialogState.currentValue.what, onValueChange = {
                        dialogState.setWhat(it)
                    })
                    StandardSpacer()


                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current[I18N_R.UI_CONDITIONS_THE_XX_TIME_WITHIN_SCOPE]
                    )
                    TinySpacer()
                    val scopeSelectorState = rememberDropdownSelectorState()
                    Box {
                        FilledTonalButton(
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ColorDefaults.backgroundSurfaceColor(
                                    1.dp
                                )
                            ), onClick = {
                                scopeSelectorState.open()
                            }) {
                            Text(LocalI18N.current[dialogState.currentValue.scope.labelKey()])
                        }
                        DropdownSelector(
                            state = scopeSelectorState,
                            items = TheXXTimeTodayScope.entries.toMutableList().apply {
                                remove(TheXXTimeTodayScope.UNRECOGNIZED)
                            }.map {
                                DropdownItem(
                                    labelLines = listOf(LocalI18N.current[it.labelKey()]),
                                    data = it,
                                )
                            }
                        ) {
                            dialogState setScope (it.data)
                        }
                    }
                    StandardSpacer()


                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current[I18N_R.UI_CONDITIONS_THE_XX_TIME_WITHIN_TIME_OP]
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
                        text = LocalI18N.current[I18N_R.UI_CONDITIONS_THE_XX_TIME_WITHIN_TIME]
                    )
                    TinySpacer()
                    ShortXTextField(
                        value = dialogState.currentValue.time.let {
                            if (it < 0) "" else it.toString()
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