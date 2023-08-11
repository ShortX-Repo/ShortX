package tornaco.apps.shortx.ui.base


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.util.Logger

class TextInputState(
    val title: String,
    val message: String?,
    val showSymbolButton: Boolean,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val inputValidator: ((String) -> Pair<Boolean, String>),
    val onSelected: (String) -> Unit
) {
    private val logger = Logger("TextInputState")

    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    var currentValue by mutableStateOf("")

    var errorMsg by mutableStateOf("")
    var isValid by mutableStateOf(false)

    fun show(initialValue: String? = null) {
        logger.p("initialValue: $initialValue")
        this.currentValue = initialValue ?: ""
        this.errorMsg = ""
        this.isValid = true

        // Trigger validate
        val validate = inputValidator(this.currentValue)
        this.isValid = validate.first
        this.errorMsg = validate.second

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
    showSymbolButton: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputValidator: ((String) -> Pair<Boolean, String>) = { it.isNotEmpty() to "" },
    onSelected: (String) -> Unit
): TextInputState {
    return remember {
        TextInputState(
            title,
            message,
            showSymbolButton,
            keyboardOptions,
            inputValidator,
            onSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputDialog(state: TextInputState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = state.title)
        }, buttons = {
            TextButton(
                enabled = state.isValid,
                onClick = {
                    if (state.isValid) {
                        state.onSelected(state.currentValue)
                        state.dismiss()
                    }
                }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                state.message?.let {
                    DialogMessage(
                        text = it,
                    )
                    StandardSpacer()
                }

                ShortXTextField(
                    modifier = Modifier.heightIn(max = 400.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    isError = state.isValid.not(),
                    value = state.currentValue,
                    showSymbolButton = state.showSymbolButton,
                    keyboardOptions = state.keyboardOptions,
                    onValueChange = {
                        val validate = state.inputValidator(it)
                        state.currentValue = it
                        state.isValid = validate.first
                        state.errorMsg = validate.second
                    })

                MediumSpacer()
                AnimatedVisibility(visible = !state.isValid && state.errorMsg.isNotEmpty()) {
                    ErrorTipRow(text = state.errorMsg)
                }
            }
        })
    }
}
