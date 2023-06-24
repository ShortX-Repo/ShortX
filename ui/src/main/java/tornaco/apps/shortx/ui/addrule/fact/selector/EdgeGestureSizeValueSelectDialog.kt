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
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.ShortXDialog
import kotlin.math.roundToInt

class EdgeGestureSizeValueSelectorState(
    val selected: (Int) -> Unit
) {
    var sizeValue by mutableStateOf(shortXManager.getGestureAreaSize())

    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show(current: Int) {
        this.sizeValue = current
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberEdgeGestureSizeValueSelectorState(
    selected: (Int) -> Unit
): EdgeGestureSizeValueSelectorState {
    return remember {
        EdgeGestureSizeValueSelectorState(selected)
    }
}

@Composable
fun EdgeGestureSizeValueSelector(state: EdgeGestureSizeValueSelectorState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() },
            buttons = {
                TextButton(onClick = {
                    state.selected(state.sizeValue)
                    state.dismiss()
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            content = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = state.sizeValue.toString())
                    Slider(
                        valueRange = 12f..48f,
                        value = state.sizeValue.toFloat(),
                        onValueChange = {
                            state.sizeValue = it.roundToInt()
                        })
                }
            })
    }
}