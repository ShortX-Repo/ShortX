@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import okhttp3.internal.toLongOrDefault
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.ui.addrule.base.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.NodeMatcher
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RegexTextInputDialog
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberRegexTextInputState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

private const val DEFAULT_TIMEOUT = 5000L

class HasFoundNodeOnScreenSelectorDialogState(
    val selectorTitle: String,
    val canSelectPkgName: Boolean,
    val canSelectActivity: Boolean,
    val canSelectTimeout: Boolean,
    val onSelected: (Fact.HasFoundNodeOnScreen) -> Unit
) : CommonDialogState() {
    var packageName: String by mutableStateOf("")
    var componentName: String by mutableStateOf("")
    var timeout: Long by mutableLongStateOf(DEFAULT_TIMEOUT)
    var matchers: List<NodeMatcher> by mutableStateOf(emptyList())

    fun addMatcher(matcher: NodeMatcher) {
        this.matchers = this.matchers.toMutableList().apply {
            add(matcher)
        }
    }

    fun removeMatcherAtIndex(index: Int) {
        this.matchers = this.matchers.toMutableList().apply {
            removeAt(index)
        }
    }

    fun show(action: Fact.HasFoundNodeOnScreen? = null) {
        this.packageName = action?.packageName ?: ""
        this.timeout = action?.detectTimeout ?: DEFAULT_TIMEOUT
        this.componentName = action?.componentName ?: ""
        this.matchers = action?.matchers ?: emptyList()

        show()
    }
}

@Composable
fun rememberHasFoundNodeOnScreenState(
    selectorTitle: String,
    canSelectPkgName: Boolean = true,
    canSelectActivity: Boolean = true,
    canSelectTimeout: Boolean = true,
    onSelected: (Fact.HasFoundNodeOnScreen) -> Unit
): HasFoundNodeOnScreenSelectorDialogState {
    return remember {
        HasFoundNodeOnScreenSelectorDialogState(
            selectorTitle,
            canSelectPkgName,
            canSelectActivity,
            canSelectTimeout,
            onSelected
        )
    }
}

@Composable
fun HasFoundNodeOnScreenDialog(state: HasFoundNodeOnScreenSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            HasFoundNodeOnScreen(state)
        }
    }
}

@Composable
private fun HasFoundNodeOnScreen(dialogState: HasFoundNodeOnScreenSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = dialogState.selectorTitle) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (dialogState.matchers.isNotEmpty()) {
                                    dialogState.dismiss()
                                    dialogState.onSelected.invoke(
                                        Fact.HasFoundNodeOnScreen(
                                            packageName = dialogState.packageName,
                                            componentName = dialogState.componentName,
                                            detectTimeout = dialogState.timeout,
                                            matchers = dialogState.matchers
                                        )
                                    )
                                }
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
                        .verticalScroll(rememberScrollState())
                ) {
                    if (dialogState.canSelectPkgName) {
                        FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_PKG])
                        SmallSpacer()
                        TipBody(text = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_PKG_DESCRIPTION])
                        MediumSpacer()
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = dialogState.packageName,
                            showSymbolButton = true,
                            onValueChange = { newValue ->
                                dialogState.packageName = newValue
                            })
                        StandardSpacer()
                    }

                    if (dialogState.canSelectActivity) {
                        FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_COMPONENT])
                        SmallSpacer()
                        TipBody(text = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_COMPONENT_DESCRIPTION])
                        MediumSpacer()

                        val activityComponentSelectorState = rememberAppComponentSelectorState(
                            types = listOf(ComponentType.Activity),
                            maxItemCount = 1,
                            onAppComponentSelected = { infoList ->
                                if (infoList.isNotEmpty()) {
                                    dialogState.componentName =
                                        infoList[0].component.toComponentName()
                                            .flattenToString()
                                }
                            }
                        )
                        AppComponentSelectorDialog(activityComponentSelectorState)

                        val singleAppSelectorState = rememberSingleAppPkgSelectorState {
                            activityComponentSelectorState.show(it.pkg)
                        }
                        SingleAppSelectorDialog(state = singleAppSelectorState)
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = dialogState.componentName,
                            showSymbolButton = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    singleAppSelectorState.show()
                                }) {
                                    RemixIcon(
                                        modifier = Modifier,
                                        remixName = Remix.System.search_2_line
                                    )
                                }
                            },
                            onValueChange = { newValue ->
                                dialogState.componentName = newValue
                            })
                        StandardSpacer()
                    }



                    if (dialogState.canSelectTimeout) {
                        FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_TIMEOUT])
                        SmallSpacer()
                        TipBody(text = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_TIMEOUT_DESCRIPTION])
                        MediumSpacer()
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = dialogState.timeout.toString(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            showSymbolButton = false,
                            onValueChange = { newValue ->
                                dialogState.timeout = newValue.toLongOrDefault(DEFAULT_TIMEOUT)
                            })
                        StandardSpacer()
                    }

                    FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_MATCHER])
                    SmallSpacer()
                    TipBody(text = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_MATCHER_DESCRIPTION])
                    MediumSpacer()
                    dialogState.matchers.forEachIndexed { index, it ->
                        ListItem(
                            titleLines = listOf(
                                LocalI18N.current[it.displayLabel],
                                LocalI18N.current[it.displayMessage]
                            ),
                            icon = {
                                RemixIcon(remixName = Remix.Logos.flickr_line)
                            },
                            action1 = {
                                IconButton(onClick = {
                                    dialogState.removeMatcherAtIndex(index)
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            onClick = {

                            })
                    }
                    StandardSpacer()

                    val textInputState = rememberTextInputState(
                        title = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_VIEW_TEXT],
                        message = null,
                        onSelected = {
                            dialogState.addMatcher(
                                NodeMatcher.Text(
                                    it,
                                    false,
                                    RegexMatchOptions.RegexMatchOptions_Match
                                )
                            )
                        }
                    )
                    TextInputDialog(state = textInputState)

                    val textInputRegexState = rememberRegexTextInputState(
                        title = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_VIEW_TEXT_REGEX],
                        message = null,
                        inputValidator = {
                            InputValidateResult.OK
                        },
                        onSelected = { text, options ->
                            dialogState.addMatcher(
                                NodeMatcher.Text(
                                    text,
                                    true,
                                    options
                                )
                            )
                        }
                    )
                    RegexTextInputDialog(state = textInputRegexState)

                    val viewIdInputState = rememberTextInputState(
                        title = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_VIEW_ID],
                        message = null,
                        onSelected = {
                            dialogState.addMatcher(NodeMatcher.ViewId(it))
                        }
                    )
                    TextInputDialog(state = viewIdInputState)

                    val matcherTypeMenuDialogState = rememberMenuDialogState<Unit>(
                        title = LocalI18N.current[I18N_R.UI_COMMON_ADD],
                        menuItems = listOf(
                            MenuDialogItem(
                                id = "id",
                                title = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_VIEW_ID]
                            ),
                            MenuDialogItem(
                                id = "text",
                                title = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_VIEW_TEXT]
                            ),
                            MenuDialogItem(
                                id = "regex",
                                title = LocalI18N.current[I18N_R.UI_FACT_HAS_FOUND_NODE_ON_SCREEN_VIEW_TEXT_REGEX]
                            ),
                        ),
                        onItemSelected = { _: Unit?, s: String ->
                            if (s == "id") {
                                viewIdInputState.show("")
                            } else if (s == "text") {
                                textInputState.show("")
                            } else {
                                textInputRegexState.show("")
                            }
                        }
                    )
                    MenuDialog(state = matcherTypeMenuDialogState)

                    androidx.compose.material3.Button(onClick = {
                        matcherTypeMenuDialogState.show()
                    }) {
                        Text(text = LocalI18N.current[I18N_R.UI_COMMON_ADD])
                    }
                }
            }
        }
    }
}











