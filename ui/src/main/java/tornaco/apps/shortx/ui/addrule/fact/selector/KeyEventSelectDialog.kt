package tornaco.apps.shortx.ui.addrule.fact.selector

import android.view.KeyEvent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberTextInputState

data class KeyEventOptions(
    val isLongPress: Boolean,
    val isDoublePress: Boolean
)

class KeyEventSelectorState(
    val showOptions: Boolean,
    val selected: (Int, KeyEventOptions) -> Unit
) :
    CommonDialogState() {
    var isLongPress by mutableStateOf(false)
    var isDoublePress by mutableStateOf(false)
}

@Composable
fun rememberKeyEventSelectorState(selected: (Int) -> Unit): KeyEventSelectorState {
    return remember {
        KeyEventSelectorState(showOptions = false) { code, _ ->
            selected(code)
        }
    }
}

@Composable
fun rememberKeyEventWithOptionsSelectorState(selected: (Int, KeyEventOptions) -> Unit): KeyEventSelectorState {
    return remember {
        KeyEventSelectorState(showOptions = true, selected = selected)
    }
}

@Composable
fun KeyEventSelector(state: KeyEventSelectorState) {
    if (state.isShowing) {
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
                        state.selected(
                            code,
                            KeyEventOptions(state.isLongPress, state.isDoublePress)
                        )
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
                                state.selected(
                                    key.keyCode,
                                    KeyEventOptions(state.isLongPress, state.isDoublePress)
                                )
                                state.dismiss()
                            }
                        })
                }
                if (state.showOptions) {
                    StandardSpacer()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = LocalI18N.current["ui.fact.edge.gesture.longtap"])
                        LargeSpacer()
                        Checkbox(
                            checked = state.isLongPress,
                            onCheckedChange = {
                                state.isLongPress = it
                                state.isDoublePress = false
                            }
                        )
                    }

                    StandardSpacer()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = LocalI18N.current["ui.fact.edge.gesture.doubletap"])
                        LargeSpacer()
                        Checkbox(
                            checked = state.isDoublePress,
                            onCheckedChange = {
                                state.isDoublePress = it
                                state.isLongPress = false
                            }
                        )
                    }
                }
            }
        })
    }
}

private data class KeyEventItem(
    val labelKey: String,
    val keyCode: Int
)