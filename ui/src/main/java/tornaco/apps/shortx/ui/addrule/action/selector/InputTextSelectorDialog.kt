package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXDialog

class TextSelectorState(
    val title: String,
    val onSelected: (String) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    var textString by mutableStateOf("")

    fun show(current: String? = null) {
        textString = current ?: ""
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberTextSelectorState(
    title: String = LocalI18N.current["ui.action.input.text"],
    onSelected: (String) -> Unit
): TextSelectorState {
    return remember {
        TextSelectorState(title, onSelected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTextSelector(state: TextSelectorState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = state.title)
        }, buttons = {
            TextButton(onClick = {
                if (state.textString.isNotBlank()) {
                    state.onSelected(state.textString)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    leadingIcon = {
                        RemixIcon(remixName = Remix.Editor.text)
                    },
                    shape = TextFieldDefaults.outlinedShape,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    value = state.textString,
                    onValueChange = {
                        state.textString = it
                    })
            }
        })
    }
}