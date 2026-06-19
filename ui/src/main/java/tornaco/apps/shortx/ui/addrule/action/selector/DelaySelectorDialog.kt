@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import tornaco.apps.shortx.core.proto.common.TimeUnit
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.core.i18n.I18N_R

class DelayState(
    val canUseAlarm: Boolean,
    val onSelected: (Action.Delay) -> Unit
) : CommonDialogState() {

    var currentValue by mutableStateOf("")
    var useAlarm by mutableStateOf(false)
    var showCD by mutableStateOf(false)
    var timeUnit by mutableStateOf(TimeUnit.TimeUnit_MS)

    fun show(initialValue: Action.Delay? = null) {
        this.currentValue = initialValue?.time ?: ""
        this.useAlarm = initialValue?.useAlarm ?: false
        this.showCD = initialValue?.showCD ?: false
        this.timeUnit = initialValue?.timeUnit ?: TimeUnit.TimeUnit_MS
        show()
    }

    fun show(
        initialTime: String? = null,
        useAlarm: Boolean? = false,
        showCD: Boolean? = false,
        timeUnit: TimeUnit? = TimeUnit.TimeUnit_MS
    ) {
        this.currentValue = initialTime ?: ""
        this.useAlarm = useAlarm ?: false
        this.showCD = showCD ?: false
        this.timeUnit = timeUnit ?: TimeUnit.TimeUnit_MS
        show()
    }

}

@Composable
fun rememberDelayState(
    canUseAlarm: Boolean,
    onSelected: (Action.Delay) -> Unit
): DelayState {
    return remember {
        DelayState(
            canUseAlarm,
            onSelected
        )
    }
}

@Composable
fun DelaySelectorDialog(state: DelayState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current[I18N_R.UI_ACTION_DELAY])
        }, buttons = {
            ShortXTextButton(
                enabled = state.currentValue.isNotEmpty(),
                onClick = {
                    state.onSelected(
                        Action.Delay(
                            time = state.currentValue,
                            useAlarm = state.useAlarm,
                            showCD = state.showCD,
                            timeUnit = state.timeUnit
                        )
                    )
                    state.dismiss()
                }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {

                FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_DELAY])
                TipBody(text = LocalI18N.current[I18N_R.UI_ACTION_DELAY_INPUT_MESSAGE])
                ShortXTextField(
                    value = state.currentValue,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        state.currentValue = it
                    })

                StandardSpacer()

                val units = TimeUnit.entries.filterNot { it == TimeUnit.UNRECOGNIZED }
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    ShortXTextField(
                        value = LocalI18N.current[state.timeUnit.labelKey()],
                        onValueChange = {},
                        readOnly = true,
                        showSymbolButton = false,
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        units.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(text = LocalI18N.current[unit.labelKey()]) },
                                onClick = {
                                    state.timeUnit = unit
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                StandardSpacer()


                if (state.canUseAlarm) {
                    FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_DELAY_INPUT_USE_ALARM])
                    TipBody(text = LocalI18N.current[I18N_R.UI_ACTION_DELAY_INPUT_USE_ALARM_SUMMARY])
                    ShortXSwitch(checked = state.useAlarm, onCheckedChange = {
                        state.useAlarm = it
                    })
                }

                FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_DELAY_SHOW_CD])
                ShortXSwitch(checked = state.showCD, onCheckedChange = {
                    state.showCD = it
                })
            }
        })
    }
}
