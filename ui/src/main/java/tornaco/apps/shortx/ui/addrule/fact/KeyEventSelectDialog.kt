package tornaco.apps.shortx.ui.addrule.fact

import android.view.KeyEvent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.ui.base.ShortXDialog

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyEventSelector(state: KeyEventSelectorState) {
    var keyCode by remember(state) {
        mutableStateOf(KeyEvent.KEYCODE_UNKNOWN)
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            Text(text = "Key Event")
        }, buttons = {
            TextButton(onClick = {
                if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                    state.selected(keyCode)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Tag, contentDescription = "Key Code")
                    },
                    shape = TextFieldDefaults.outlinedShape,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent),
                    label = {
                        Text(text = "Component")
                    },
                    value = keyCode.toString(),
                    onValueChange = {
                        keyCode = kotlin.runCatching {
                            it.toInt()
                        }.getOrElse { KeyEvent.KEYCODE_UNKNOWN }
                    })
            }
        })
    }
}