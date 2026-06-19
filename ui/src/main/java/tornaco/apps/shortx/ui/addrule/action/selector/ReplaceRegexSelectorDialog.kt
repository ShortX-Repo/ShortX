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
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class ReplaceRegexState(
    val onSelected: (Action.ReplaceRegex) -> Unit
) : CommonDialogState() {

    var string by mutableStateOf("")
    var regex by mutableStateOf("")
    var replacement by mutableStateOf("")

    var testResult by mutableStateOf("")

    fun show(initialValue: Action.ReplaceRegex? = null) {
        this.string = initialValue?.string ?: ""
        this.regex = initialValue?.regex ?: ""
        this.replacement = initialValue?.replacement ?: ""
        this.testResult = ""

        show()
    }

    fun test() {

    }

}

@Composable
fun rememberReplaceRegexState(
    onSelected: (Action.ReplaceRegex) -> Unit
): ReplaceRegexState {
    return remember {
        ReplaceRegexState(
            onSelected
        )
    }
}

@Composable
fun ReplaceRegexSelectorDialog(state: ReplaceRegexState) {
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
                                title = { Text(i18n[I18N_R.UI_ACTION_REPLACE_REGEX]) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                },
                                actions = {
                                    if (false) {
                                        IconButton(onClick = {
                                            state.test()
                                        }) {
                                            RemixIcon(remixName = Remix.Media.play_line)
                                        }
                                    }

                                    IconButton(onClick = {
                                        state.onSelected(
                                            Action.ReplaceRegex(
                                                string = state.string,
                                                regex = state.regex,
                                                replacement = state.replacement,
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

                            FieldTitle(text = LocalI18N.current[I18N_R.UI_TOOLS_REGEX_TEST_REPLACEMENT])
                            ShortXTextField(
                                value = state.replacement,
                                onValueChange = {
                                    state.replacement = it
                                })
                            StandardSpacer()


                            AnimatedVisibility(visible = state.testResult.isNotEmpty()) {
                                Column {
                                    FieldTitle(text = LocalI18N.current[I18N_R.UI_TOOLS_REGEX_TEST_RESULT])
                                    Text(text = state.testResult)
                                    StandardSpacer()
                                }
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