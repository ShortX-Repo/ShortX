package tornaco.apps.shortx.ui.base


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

class TextInputState(
    val title: String,
    val message: String?,
    val onSelected: (String) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    var currentValue by mutableStateOf("")

    fun show(initialValue: String = "") {
        this.currentValue = initialValue
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberTextInputState(
    title: String,
    message: String?,
    onSelected: (String) -> Unit
): TextInputState {
    return remember {
        TextInputState(title, message, onSelected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputDialog(state: TextInputState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = state.title)
        }, buttons = {
            TextButton(onClick = {
                if (state.currentValue.isNotBlank()) {
                    state.onSelected(state.currentValue)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                state.message?.let {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                    StandardSpacer()
                }

                TextField(
                    shape = TextFieldDefaults.outlinedShape,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    value = state.currentValue,
                    onValueChange = {
                        state.currentValue = it
                    })
            }
        })
    }
}