package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.core.proto.condition.GlobalVarOpPayload
import tornaco.apps.shortx.core.proto.fact.ValueConstraints
import tornaco.apps.shortx.core.rule.needUserInputValue
import tornaco.apps.shortx.core.rule.toDisplayName
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXFilledTonalButton
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.core.i18n.I18N_R

class ValueConstraintSelectorState(
    // Before after.
    val selected: (ValueConstraints?, ValueConstraints) -> Unit
) : CommonDialogState() {
    var _op: GlobalVarOp by mutableStateOf(GlobalVarOp.EqualTo)
    var _payload: GlobalVarOpPayload by mutableStateOf(GlobalVarOpPayload.newBuilder().build())
    var showPayloadInputField by mutableStateOf(false)

    var before: ValueConstraints? = null

    fun show(constraints: ValueConstraints? = null) {
        this.before = constraints
        this._op = constraints?.op ?: GlobalVarOp.EqualTo
        this._payload = constraints?.payload ?: GlobalVarOpPayload.newBuilder().build()
        checkShowPayloadInputField()
        show()
    }

    infix fun setOp(op: GlobalVarOp) {
        this._op = op
        checkShowPayloadInputField()
    }

    fun inputValue(value: String) {
        _payload = GlobalVarOpPayload.newBuilder().setValue(value).build()
    }

    private fun checkShowPayloadInputField() {
        showPayloadInputField = _op.needUserInputValue()
    }
}

@Composable
fun rememberValueConstraintSelectorState(
    selected: (ValueConstraints?, ValueConstraints) -> Unit
): ValueConstraintSelectorState {
    return remember {
        ValueConstraintSelectorState(selected)
    }
}

@Composable
fun ValueConstraintSelector(state: ValueConstraintSelectorState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current[I18N_R.UI_FACT_SENSOR_CONSTRAINT])
        }, buttons = {
            ShortXTextButton(onClick = {
                state.selected(
                    state.before,
                    ValueConstraints.newBuilder()
                        .setOp(state._op)
                        .setPayload(state._payload)
                        .build()
                )
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val opSelectorState = rememberDropdownSelectorState()
                Box {
                    ShortXFilledTonalButton(
                        onClick = {
                            opSelectorState.open()
                        }) {
                        Text(state._op.toDisplayName())
                    }
                    DropdownSelector(
                        state = opSelectorState,
                        items = listOf(
                            GlobalVarOp.EqualTo,
                            GlobalVarOp.GreaterThan,
                            GlobalVarOp.GreaterThanOrEQ,
                            GlobalVarOp.LessThan,
                            GlobalVarOp.LessThanOrEQ
                        ).map {
                            DropdownItem(
                                labelLines = listOf(it.toDisplayName()),
                                data = it,
                            )
                        }
                    ) {
                        state.setOp(it.data)
                    }
                }
                StandardSpacer()

                AnimatedVisibility(visible = state.showPayloadInputField) {
                    Column {
                        SmallTitle(
                            modifier = Modifier.padding(vertical = 12.dp),
                            text = LocalI18N.current[I18N_R.UI_FACT_SENSOR_VALUE]
                        )
                        TinySpacer()
                        ShortXTextField(value = state._payload.value, onValueChange = {
                            state.inputValue(it)
                        })
                        StandardSpacer()
                    }
                }
            }
        })
    }
}

