package tornaco.apps.shortx.ui.addrule.fact.selector

import android.view.KeyEvent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberTextInputState

class KeyEventSelectorState(val selected: (Int) -> Unit) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberKeyEventSelectorState(selected: (Int) -> Unit): KeyEventSelectorState {
    return remember {
        KeyEventSelectorState(selected)
    }
}

@Composable
fun KeyEventSelector(state: KeyEventSelectorState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.fact.key.event"])
        }, buttons = {
        }, content = {
            val customKeyCodeDialogState = rememberTextInputState(
                title = LocalI18N.current["ui.action.custom.keycode"],
                message = null,
                showSymbolButton = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onSelected = {
                    val code = it.toIntOrNull()
                    if (code != null) {
                        state.selected(code)
                        state.dismiss()
                    }
                }
            )
            TextInputDialog(state = customKeyCodeDialogState)


            Column(modifier = Modifier.fillMaxWidth()) {
                arrayOf(
                    KeyEventItem(LocalI18N.current["ui.keycode.label.back"], KeyEvent.KEYCODE_BACK),
                    KeyEventItem(LocalI18N.current["ui.keycode.label.home"], KeyEvent.KEYCODE_HOME),
                    KeyEventItem(
                        LocalI18N.current["ui.keycode.label.volume.down"],
                        KeyEvent.KEYCODE_VOLUME_DOWN
                    ),
                    KeyEventItem(
                        LocalI18N.current["ui.keycode.label.volume.up"],
                        KeyEvent.KEYCODE_VOLUME_UP
                    ),
                    KeyEventItem(
                        LocalI18N.current["ui.keycode.label.power"],
                        KeyEvent.KEYCODE_POWER
                    ),
                    KeyEventItem(
                        LocalI18N.current["ui.action.custom.keycode"],
                        KeyEvent.KEYCODE_UNKNOWN
                    ),
                ).forEach { key ->
                    ListItem(
                        title = key.labelKey,
                        onClick = {
                            if (key.keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                                customKeyCodeDialogState.show()
                            } else {
                                state.selected(key.keyCode)
                                state.dismiss()
                            }
                        })
                }
            }
        })
    }
}

private data class KeyEventItem(
    val labelKey: String,
    val keyCode: Int
)