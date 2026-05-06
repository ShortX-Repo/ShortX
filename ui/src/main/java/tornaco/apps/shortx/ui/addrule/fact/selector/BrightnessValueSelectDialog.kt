package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextButton
import kotlin.math.roundToInt

class BrightnessValueSelectorState(
    val selected: (Int) -> Unit
) {
    var brightnessValue by mutableStateOf(100)

    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show(current: Int) {
        this.brightnessValue = current
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberBrightnessValueSelectorState(
    selected: (Int) -> Unit
): BrightnessValueSelectorState {
    return remember {
        BrightnessValueSelectorState(selected)
    }
}

@Composable
fun BrightnessValueSelector(state: BrightnessValueSelectorState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() },
            title = {
                DialogTitle(text = LocalI18N.current["ui.action.set.brightness"])
            },
            buttons = {
                ShortXTextButton(onClick = {
                    state.selected(state.brightnessValue)
                    state.dismiss()
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            content = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = state.brightnessValue.toString())
                    Slider(
                        valueRange = 0f..255f,
                        value = state.brightnessValue.toFloat(),
                        onValueChange = {
                            state.brightnessValue = it.roundToInt()
                        })
                }
            })
    }
}