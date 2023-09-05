package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Switch
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
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody

class DelayState(
    val onSelected: (Action.Delay) -> Unit
) : CommonDialogState() {

    var currentValue by mutableStateOf("")
    var useAlarm by mutableStateOf(false)

    fun show(initialValue: Action.Delay? = null) {
        this.currentValue = initialValue?.time ?: ""
        this.useAlarm = initialValue?.useAlarm ?: false
        show()
    }

}

@Composable
fun rememberDelayState(
    onSelected: (Action.Delay) -> Unit
): DelayState {
    return remember {
        DelayState(
            onSelected
        )
    }
}

@Composable
fun DelaySelectorDialog(state: DelayState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.action.delay"])
        }, buttons = {
            TextButton(
                enabled = state.currentValue.isNotEmpty(),
                onClick = {
                    state.onSelected(
                        Action.Delay(
                            time = state.currentValue,
                            useAlarm = state.useAlarm
                        )
                    )
                    state.dismiss()
                }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {

                FieldTitle(text = LocalI18N.current["ui.action.delay"])
                TipBody(text = LocalI18N.current["ui.action.delay.input.message"])
                ShortXTextField(
                    value = state.currentValue,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        state.currentValue = it
                    })

                StandardSpacer()


                FieldTitle(text = LocalI18N.current["ui.action.delay.input.use.alarm"])
                TipBody(text = LocalI18N.current["ui.action.delay.input.use.alarm.summary"])
                ShortXSwitch(checked = state.useAlarm, onCheckedChange = {
                    state.useAlarm = it
                })

            }
        })
    }
}
