package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
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
    val canUseAlarm: Boolean,
    val onSelected: (Action.Delay) -> Unit
) : CommonDialogState() {

    var currentValue by mutableStateOf("")
    var useAlarm by mutableStateOf(false)
    var showCD by mutableStateOf(false)

    fun show(initialValue: Action.Delay? = null) {
        this.currentValue = initialValue?.time ?: ""
        this.useAlarm = initialValue?.useAlarm ?: false
        this.showCD = initialValue?.showCD ?: false
        show()
    }

    fun show(initialTime: String? = null, useAlarm: Boolean? = false, showCD: Boolean? = false) {
        this.currentValue = initialTime ?: ""
        this.useAlarm = useAlarm ?: false
        this.showCD = showCD ?: false
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
            DialogTitle(text = LocalI18N.current["ui.action.delay"])
        }, buttons = {
            TextButton(
                enabled = state.currentValue.isNotEmpty(),
                onClick = {
                    state.onSelected(
                        Action.Delay(
                            time = state.currentValue,
                            useAlarm = state.useAlarm,
                            showCD = state.showCD
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


                if (state.canUseAlarm) {
                    FieldTitle(text = LocalI18N.current["ui.action.delay.input.use.alarm"])
                    TipBody(text = LocalI18N.current["ui.action.delay.input.use.alarm.summary"])
                    ShortXSwitch(checked = state.useAlarm, onCheckedChange = {
                        state.useAlarm = it
                    })
                }

                FieldTitle(text = LocalI18N.current["ui.action.delay.show.cd"])
                ShortXSwitch(checked = state.showCD, onCheckedChange = {
                    state.showCD = it
                })
            }
        })
    }
}
