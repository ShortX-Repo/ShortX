package tornaco.apps.shortx.ui.base


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
import tornaco.apps.shortx.ui.base.InputValidateResult.OK.toError

sealed interface InputValidateResult {
    data object OK : InputValidateResult
    data class Warn(val message: String) : InputValidateResult
    data class Error(val message: String) : InputValidateResult

    fun String.toWarn() = Warn(this)
    fun String.toError() = Error(this)
}

class TextInputState(
    val title: String,
    val message: String?,
    val showSymbolButton: Boolean,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val inputValidator: ((String) -> InputValidateResult),
    val onSelected: (String) -> Unit
) {
    private val logger = Logger("TextInputState")

    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    var currentValue by mutableStateOf("")

    var validateRes by mutableStateOf<InputValidateResult>(InputValidateResult.OK)

    fun show(initialValue: String? = null) {
        logger.p("initialValue: $initialValue")
        this.currentValue = initialValue ?: ""

        // Trigger validate
        val validate = inputValidator(this.currentValue)
        this.validateRes = validate

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
    inputValidator: (String) -> InputValidateResult = { if (it.isNotEmpty()) InputValidateResult.OK else "*required".toError() },
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
                enabled = state.validateRes !is InputValidateResult.Error,
                onClick = {
                    state.onSelected(state.currentValue)
                    state.dismiss()
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
                    isError = state.validateRes is InputValidateResult.Error,
                    value = state.currentValue,
                    showSymbolButton = state.showSymbolButton,
                    keyboardOptions = state.keyboardOptions,
                    onValueChange = {
                        val validate = state.inputValidator(it)
                        state.currentValue = it
                        state.validateRes = validate
                    })

                MediumSpacer()

                val validateRes = state.validateRes
                if (validateRes is InputValidateResult.Warn && validateRes.message.isNotEmpty()) {
                    WarnTipRow(text = validateRes.message)
                }
                if (validateRes is InputValidateResult.Error && validateRes.message.isNotEmpty()) {
                    ErrorTipRow(text = validateRes.message)
                }
            }
        })
    }
}
