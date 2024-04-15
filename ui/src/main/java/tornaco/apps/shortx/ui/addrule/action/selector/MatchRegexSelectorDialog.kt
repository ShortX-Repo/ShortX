package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RegexMatchOptionsDropdown
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry

class MatchRegexState(
    val onSelected: (Action.MatchRegex) -> Unit
) : CommonDialogState() {

    var string by mutableStateOf("")
    var regex by mutableStateOf("")

    var testResult by mutableStateOf("")

    var matchOptions by mutableStateOf(RegexMatchOptions.RegexMatchOptions_Match)


    fun show(initialValue: Action.MatchRegex? = null) {
        this.string = initialValue?.string ?: ""
        this.regex = initialValue?.regex ?: ""
        this.matchOptions = initialValue?.matchOptions ?: RegexMatchOptions.RegexMatchOptions_Match
        this.testResult = ""

        show()
    }

}

@Composable
fun rememberMatchRegexState(
    onSelected: (Action.MatchRegex) -> Unit
): MatchRegexState {
    return remember {
        MatchRegexState(
            onSelected
        )
    }
}

@Composable
fun MatchRegexSelectorDialog(state: MatchRegexState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.action.match.regex"])
        }, buttons = {
            IconButton(
                enabled = state.string.isNotEmpty(),
                onClick = {
                }) {
                RemixIcon(remixName = Remix.Media.play_line)
            }
            IconButton(
                enabled = state.string.isNotEmpty(),
                onClick = {
                    state.onSelected(
                        Action.MatchRegex(
                            string = state.string,
                            regex = state.regex,
                            matchOptions = state.matchOptions,
                        )
                    )
                    state.dismiss()
                }) {
                RemixIcon(remixName = Remix.System.check_line)
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {

                FieldTitle(text = LocalI18N.current["ui.tools.regex.test.string"])
                ShortXTextField(
                    value = state.string,
                    onValueChange = {
                        state.string = it
                    })
                StandardSpacer()

                FieldTitle(text = LocalI18N.current["ui.tools.regex.test.regex"])
                ShortXTextField(
                    value = state.regex,
                    onValueChange = {
                        state.regex = it
                    })
                StandardSpacer()

                AnimatedVisibility(visible = state.testResult.isNotEmpty()) {
                    Column {
                        FieldTitle(text = LocalI18N.current["ui.tools.regex.test.result"])
                        Text(text = state.testResult)
                        StandardSpacer()
                    }
                }

                RegexMatchOptionsDropdown(options = state.matchOptions) {
                    state.matchOptions = it
                }

                SectionSpacer()
                SuggestedNavEntry()

            }
        })
    }
}
