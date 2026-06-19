@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RegexMatchOptionsDropdown
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

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
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val i18n = LocalI18N.current
                    Scaffold(
                        modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(i18n[I18N_R.UI_ACTION_MATCH_REGEX]) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
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

                                }
                            )
                        }) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .padding(paddingValues)
                                .animateContentSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            FieldTitle(text = LocalI18N.current[I18N_R.UI_TOOLS_REGEX_TEST_STRING])
                            ShortXTextField(
                                value = state.string,
                                onValueChange = {
                                    state.string = it
                                })
                            StandardSpacer()

                            FieldTitle(text = LocalI18N.current[I18N_R.UI_TOOLS_REGEX_TEST_REGEX])
                            ShortXTextField(
                                value = state.regex,
                                onValueChange = {
                                    state.regex = it
                                })
                            StandardSpacer()

                            AnimatedVisibility(visible = state.testResult.isNotEmpty()) {
                                Column {
                                    FieldTitle(text = LocalI18N.current[I18N_R.UI_TOOLS_REGEX_TEST_RESULT])
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
                    }
                }

            }
        }
    }
}