package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.ShortXDialog

class OverlayButtonClickSelectorState(val selected: (Int) -> Unit) {
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
fun rememberOverlayButtonClickSelectorState(selected: (Int) -> Unit): OverlayButtonClickSelectorState {
    return remember {
        OverlayButtonClickSelectorState(selected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlayButtonClickSelector(state: OverlayButtonClickSelectorState) {
    var index by remember(state) {
        mutableStateOf(0)
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = "Button index")
        }, buttons = {
            TextButton(onClick = {
                state.selected(index)
                state.dismiss()
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
                        containerColor = Color.Transparent
                    ),
                    label = {
                        Text(text = "Component")
                    },
                    value = index.toString(),
                    onValueChange = {
                        index = kotlin.runCatching {
                            it.toInt()
                        }.getOrElse { 0 }
                    })
            }
        })
    }
}