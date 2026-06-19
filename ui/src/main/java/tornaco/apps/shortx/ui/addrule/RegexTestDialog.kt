@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RegexMatchOptionsDropdown
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class RegexTestDialogState(
) : CommonDialogState() {

    var regex by mutableStateOf("")
    var options by mutableStateOf(RegexMatchOptions.RegexMatchOptions_Match)
    var target by mutableStateOf("")
    var output by mutableStateOf("")

    fun test() {
        val matchRes = kotlin.runCatching {
            if (options == RegexMatchOptions.RegexMatchOptions_Match) {
                regex.toRegex().matches(target).toString()
            } else {
                regex.toRegex().containsMatchIn(target).toString()
            }
        }.getOrElse {
            it.message
        }
        output = matchRes ?: "ERROR."
    }
}

@Composable
fun rememberRegexTestState(
): RegexTestDialogState {
    return remember {
        RegexTestDialogState()
    }
}

@Composable
fun RegexTestDialog(state: RegexTestDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            RegexTest(state)
        }
    }
}

@Composable
private fun RegexTest(dialogState: RegexTestDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scope = rememberCoroutineScope()
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current[I18N_R.UI_TOOLS_REGEX_TEST_TITLE]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    dialogState.output = ""
                                    delay(500)
                                    dialogState.test()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = "Evaluate"
                                )
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    arrayOf<@Composable () -> Unit>(
                        {
                            Column {
                                FieldTitle(text = LocalI18N.current[I18N_R.UI_TOOLS_REGEX_TEST_REGEX])
                                MediumSpacer()
                                ShortXTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = dialogState.regex,
                                    onValueChange = { dialogState.regex = it })

                                MediumSpacer()
                                RegexMatchOptionsDropdown(
                                    options = dialogState.options,
                                    onSelectNewOptions = {
                                        dialogState.options = it
                                    })
                            }
                        },
                        {
                            FieldTitle(text = LocalI18N.current[I18N_R.UI_TOOLS_REGEX_TEST_STRING])
                            MediumSpacer()
                            ShortXTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = dialogState.target,
                                onValueChange = { dialogState.target = it })
                        },
                        {
                            AnimatedVisibility(visible = dialogState.output.isNotBlank()) {
                                Column {
                                    MediumSpacer()
                                    ShortXCard {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            FieldTitle(text = LocalI18N.current[I18N_R.UI_TOOLS_REGEX_TEST_RESULT])
                                            StandardSpacer()
                                            Text(dialogState.output)
                                        }
                                    }
                                }
                            }
                        },
                    ).forEach {
                        it()
                        LargeSpacer()
                    }

                }
            }
        }
    }
}