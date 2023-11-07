package tornaco.apps.shortx.ui.base


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.InputValidateResult.OK.toError


class RegexTextInputState(
    val title: String,
    val message: String?,
    val showSymbolButton: Boolean,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val inputValidator: ((String) -> InputValidateResult),
    val onSelected: (String, RegexMatchOptions) -> Unit
) : CommonDialogState() {
    private val logger = Logger("RegexTextInputState")

    var currentValue by mutableStateOf("")
    var matchOptions by mutableStateOf<RegexMatchOptions>(RegexMatchOptions.RegexMatchOptions_Match)
    var validateRes by mutableStateOf<InputValidateResult>(InputValidateResult.OK)

    fun show(
        initialValue: String? = null,
        options: RegexMatchOptions? = null
    ) {
        logger.p("initialValue: $initialValue")
        this.currentValue = initialValue ?: ""
        this.matchOptions = options ?: RegexMatchOptions.RegexMatchOptions_Match

        // Trigger validate
        val validate = inputValidator(this.currentValue)
        this.validateRes = validate

        super.show()
    }
}

@Composable
fun rememberRegexTextInputState(
    title: String,
    message: String?,
    showSymbolButton: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputValidator: (String) -> InputValidateResult = { if (it.isNotEmpty()) InputValidateResult.OK else "*required".toError() },
    onSelected: (String, RegexMatchOptions) -> Unit
): RegexTextInputState {
    return remember {
        RegexTextInputState(
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
fun RegexTextInputDialog(state: RegexTextInputState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = state.title)
        }, buttons = {
            TextButton(
                enabled = state.validateRes !is InputValidateResult.Error,
                onClick = {
                    state.onSelected(state.currentValue, state.matchOptions)
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

                StandardSpacer()

                val regexOptionsState = rememberDropdownSelectorState()
                Box {
                    FilledTonalButton(
                        onClick = {
                            regexOptionsState.open()
                        }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(LocalI18N.current[state.matchOptions.labelKey()])
                            LargeSpacer()
                            LargeSpacer()
                            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                        }
                    }
                    DropdownSelector(
                        state = regexOptionsState,
                        items = RegexMatchOptions.values().toMutableList().apply {
                            remove(RegexMatchOptions.UNRECOGNIZED)
                        }.map {
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current[it.labelKey()]),
                                data = it,
                            )
                        }
                    ) {
                        state.matchOptions = it.data
                    }
                }
            }
        })
    }
}

fun RegexMatchOptions.labelKey(): String {
    return "ui.common.regex.options.${name.lowercase()}"
}
