package tornaco.apps.shortx.ui.pkgset

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogMessage
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.ErrorTipRow
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.WarnTipRow

data class NewPkgSet(
    val label: String,
    val description: String
)

class NewPkgSetState(
    val inputValidator: (String) -> InputValidateResult,
    val onSelected: (NewPkgSet) -> Unit
) : CommonDialogState() {
    var label by mutableStateOf("")
    var description by mutableStateOf("")
    var validateRes by mutableStateOf<InputValidateResult>(InputValidateResult.OK)

    fun show(current: NewPkgSet? = null) {
        label = current?.label ?: ""
        description = current?.description ?: ""
        validateRes = inputValidator(label)
        show()
    }
}

@Composable
fun rememberNewPkgSetState(
    inputValidator: (String) -> InputValidateResult,
    onSelected: (NewPkgSet) -> Unit
): NewPkgSetState {
    return remember {
        NewPkgSetState(inputValidator, onSelected)
    }
}

@Composable
fun NewPkgSetDialog(state: NewPkgSetState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.pkg.set.add.title"])
        }, buttons = {
            ShortXTextButton(
                enabled = state.validateRes !is InputValidateResult.Error,
                onClick = {
                    state.onSelected(
                        NewPkgSet(
                            label = state.label.trim(),
                            description = state.description.trim()
                        )
                    )
                    state.dismiss()
                }
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val i18N = LocalI18N.current

                FieldTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = i18N["ui.pkg.set.add.input.label"]
                )
                SmallSpacer()
                DialogMessage(text = i18N["ui.pkg.set.add.input.label"])
                SmallSpacer()
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.label,
                    showSymbolButton = false,
                    onValueChange = {
                        state.label = it
                        state.validateRes = state.inputValidator(it)
                    }
                )

                MediumSpacer()

                when (val validateRes = state.validateRes) {
                    is InputValidateResult.Warn -> {
                        if (validateRes.message.isNotEmpty()) {
                            WarnTipRow(text = validateRes.message)
                        }
                    }

                    is InputValidateResult.Error -> {
                        if (validateRes.message.isNotEmpty()) {
                            ErrorTipRow(text = validateRes.message)
                        }
                    }

                    InputValidateResult.OK -> Unit
                }

                FieldTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = i18N["ui.common.note"]
                )
                SmallSpacer()
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.description,
                    showSymbolButton = false,
                    onValueChange = {
                        state.description = it
                    }
                )
            }
        })
    }
}