package tornaco.apps.shortx.ui.addrule.fact.selector


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
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

class SystemSettingsUrlSelectorState(
    val onSelected: (Fact.SystemSettingsChanged) -> Unit
) : CommonDialogState() {
    var inputUrl by mutableStateOf("content://settings/")
    var inputValueRegex by mutableStateOf("")

    fun show(initialValue: Fact.SystemSettingsChanged? = null) {
        this.inputUrl = initialValue?.urlAndValueRegex?.first ?: "content://settings/"
        this.inputValueRegex = initialValue?.urlAndValueRegex?.second ?: ""

        show()
    }

    fun inputUrl(it: String) {
        this.inputUrl = it
    }

    fun inputRegex(it: String) {
        this.inputValueRegex = it
    }
}

@Composable
fun rememberSystemSettingsUrlSelectorState(
    onSelected: (Fact.SystemSettingsChanged) -> Unit
): SystemSettingsUrlSelectorState {
    return remember {
        SystemSettingsUrlSelectorState(
            onSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemSettingsUrlSelectorDialog(state: SystemSettingsUrlSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = LocalI18N.current["ui.fact.system.settings.changed"]) },
                                actions = {
                                    IconButton(onClick = {
                                        if (state.inputUrl.isNotEmpty() && state.inputValueRegex.isNotEmpty()) {
                                            state.onSelected(
                                                Fact.SystemSettingsChanged(
                                                    urlAndValueRegex = StringPair.newBuilder()
                                                        .setFirst(state.inputUrl)
                                                        .setSecond(state.inputValueRegex)
                                                        .build()
                                                )
                                            )
                                            state.dismiss()
                                        }
                                    }) {
                                        RemixIcon(remixName = Remix.System.check_line)
                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
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
                            val inputState = rememberTextInputState(
                                title = LocalI18N.current["ui.fact.system.settings.changed.url"],
                                message = LocalI18N.current["ui.fact.system.settings.changed.summary"],
                                onSelected = {
                                    state.inputUrl(it)
                                }
                            )
                            TextInputDialog(state = inputState)

                            FieldTitle(text = LocalI18N.current["ui.fact.system.settings.changed.url"])
                            SmallSpacer()
                            TipRow(text = LocalI18N.current["ui.fact.system.settings.changed.url.summary"])
                            MediumSpacer()
                            ShortXTextField(
                                value = state.inputUrl,
                                onValueChange = { state.inputUrl(it) })

                            LargeSpacer()

                            FieldTitle(text = LocalI18N.current["ui.fact.system.settings.changed.regex"])
                            SmallSpacer()
                            TipRow(text = LocalI18N.current["ui.fact.system.settings.changed.regex.summary"])
                            MediumSpacer()
                            ShortXTextField(
                                value = state.inputValueRegex,
                                onValueChange = { state.inputRegex(it) })


                            LargeSpacer()
                            SectionSpacer()
                            SuggestedNavEntry()
                        }
                    }
                }
            }
        }
    }
}
