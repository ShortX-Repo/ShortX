package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.proto.action.Vibrate
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.StandardSpacer
import kotlin.math.roundToInt

class VibratePatternSelectorState(
    val selected: (Action.Vibrate) -> Unit
) : CommonDialogState() {
    var vib1 by mutableStateOf(100)
    var vib2 by mutableStateOf(0)
    var vib3 by mutableStateOf(0)

    fun show(vibrate: Action.Vibrate? = null) {
        vib1 = vibrate?.vib1 ?: 100
        vib2 = vibrate?.vib2 ?: 100
        vib3 = vibrate?.vib3 ?: 100

        show()
    }

    fun preview() {
        shortXManager.executeAction(
            Vibrate.newBuilder()
                .setVib1(vib1)
                .setVib2(vib2)
                .setVib3(vib3)
                .build().pack_()
        )
    }
}

@Composable
fun rememberVibratePatternSelectorState(
    selected: (Action.Vibrate) -> Unit
): VibratePatternSelectorState {
    return remember {
        VibratePatternSelectorState(selected)
    }
}

@Composable
fun VibratePatternSelector(state: VibratePatternSelectorState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() },
            title = {
                DialogTitle(text = LocalI18N.current["ui.action.vibrate"])
            },
            buttons = {
                TextButton(onClick = {
                    state.selected(
                        Action.Vibrate(
                            vib1 = state.vib1,
                            vib2 = state.vib2,
                            vib3 = state.vib3
                        )
                    )
                    state.dismiss()
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            content = {
                val valueRange: ClosedFloatingPointRange<Float> = 0f..1000f
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "${state.vib1} ms")
                    Slider(
                        valueRange = valueRange,
                        value = state.vib1.toFloat(),
                        onValueChange = {
                            state.vib1 = it.roundToInt()
                            state.preview()
                        })
                    StandardSpacer()

                    Text(text = "${state.vib2} ms")
                    Slider(
                        valueRange = valueRange,
                        value = state.vib2.toFloat(),
                        onValueChange = {
                            state.vib2 = it.roundToInt()
                            state.preview()
                        })
                    StandardSpacer()

                    Text(text = "${state.vib3} ms")
                    Slider(
                        valueRange = valueRange,
                        value = state.vib3.toFloat(),
                        onValueChange = {
                            state.vib3 = it.roundToInt()
                            state.preview()
                        })
                    StandardSpacer()
                }
            })
    }
}