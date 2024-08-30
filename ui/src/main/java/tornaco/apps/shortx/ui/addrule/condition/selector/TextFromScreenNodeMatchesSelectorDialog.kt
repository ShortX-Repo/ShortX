@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RegexTextInputDialog
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.labelKey
import tornaco.apps.shortx.ui.base.rememberRegexTextInputState

class TextFromScreenNodeMatchesState(
    val selected: (Condition.TextFromScreenNodeMatches) -> Unit
) : CommonDialogState() {
    var nodeId by mutableStateOf("")
    var regex by mutableStateOf("")
    var options by mutableStateOf(RegexMatchOptions.RegexMatchOptions_Match)

    fun show(current: Condition.TextFromScreenNodeMatches? = null) {
        this.nodeId = current?.nodeId ?: ""
        this.regex = current?.regex ?: ""
        this.options = current?.regexMatchOptions ?: RegexMatchOptions.RegexMatchOptions_Match

        show()
    }
}

@Composable
fun rememberTextFromScreenNodeMatchesState(
    selected: (Condition.TextFromScreenNodeMatches) -> Unit
): TextFromScreenNodeMatchesState {
    return remember {
        TextFromScreenNodeMatchesState(selected)
    }
}

@Composable
fun TextFromScreenNodeMatchesSelector(dialogState: TextFromScreenNodeMatchesState) {
    if (dialogState.isShowing) {
        Scaffold(modifier = Modifier,
            topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(text = LocalI18N.current["ui.condition.text.from.screen.node.matches"]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            dialogState.dismiss()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            dialogState.dismiss()
                            dialogState.selected(
                                Condition.TextFromScreenNodeMatches(
                                    nodeId = dialogState.nodeId,
                                    regex = dialogState.regex,
                                    regexMatchOptions = dialogState.options
                                )
                            )
                        }) {
                            RemixIcon(remixName = Remix.System.check_line)
                        }
                    }
                )
            }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                FieldTitle(text = LocalI18N.current["ui.fact.has.found.node.on.screen.view.id"])
                TinySpacer()
                ShortXTextField(
                    value = dialogState.nodeId,
                    onValueChange = { dialogState.nodeId = it }
                )
                LargeSpacer()


                val textInputRegexState = rememberRegexTextInputState(
                    title = LocalI18N.current["ui.fact.has.found.node.on.screen.view.text.regex"],
                    message = null,
                    inputValidator = {
                        InputValidateResult.OK
                    },
                    onSelected = { text, options ->
                        dialogState.regex = text
                        dialogState.options = options
                    }
                )
                RegexTextInputDialog(state = textInputRegexState)
                LargeSpacer()

                ListItem(
                    titleLines = listOf(
                        LocalI18N.current["ui.fact.has.found.node.on.screen.view.text"],
                        dialogState.regex,
                        LocalI18N.current[dialogState.options.labelKey()]
                    ),
                    onClick = {
                        textInputRegexState.show(
                            initialValue = dialogState.regex,
                            options = dialogState.options
                        )
                    }
                )
            }
        }
    }
}

