@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix

@Composable
@Preview
fun ShortXTextFieldPreview() {
    Surface {
        Scaffold {
            Column(Modifier.padding(it)) {
                ShortXTextField(
                    value = "",
                    onValueChange = {},
                    label = {
                        Text("Label")
                    },
                    placeholder = {
                        Text("Input your name")
                    }
                )
            }
        }
    }
}

@Composable
fun ShortXTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ShortXCardRoundedCornerShapeL,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
    showSymbolButton: Boolean = true
) {
    Box {
        var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }

        // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
        // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
        // composition.
        val textFieldValue = textFieldValueState.copy(text = value)

        SideEffect {
            if (textFieldValue.selection != textFieldValueState.selection ||
                textFieldValue.composition != textFieldValueState.composition
            ) {
                textFieldValueState = textFieldValue
            }
        }

        // Last String value that either text field was recomposed with or updated in the onValueChange
        // callback. We keep track of it to prevent calling onValueChange(String) for same String when
        // CoreTextField's onValueChange is called multiple times without recomposition in between.
        var lastTextValue by remember(value) { mutableStateOf(value) }

        val symbolDropdownState = rememberSymbolDropdownState { inputText ->
            val newTextFieldValueState = TextFieldValue(
                textFieldValueState.text
                    .toMutableList()
                    .apply {
                        addAll(textFieldValueState.selection.start, inputText.toList())
                    }
                    .joinToString(""),
                TextRange(textFieldValueState.selection.start + inputText.length)
            )
            textFieldValueState = newTextFieldValueState

            val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
            lastTextValue = newTextFieldValueState.text

            if (stringChangedSinceLastInvocation) {
                onValueChange(newTextFieldValueState.text)
            }
        }
        SymbolDropdown(state = symbolDropdownState)

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newTextFieldValueState ->
                textFieldValueState = newTextFieldValueState

                val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
                lastTextValue = newTextFieldValueState.text

                if (stringChangedSinceLastInvocation) {
                    onValueChange(newTextFieldValueState.text)
                }
            },
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = {
                Row {
                    trailingIcon?.invoke()
                    if (showSymbolButton) IconButton(onClick = {
                        symbolDropdownState.open()
                    }) {
                        RemixIcon(modifier = Modifier.size(18.dp), remixName = Remix.Editor.text)
                    }
                }
            },
            supportingText = supportingText,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors
        )
    }
}