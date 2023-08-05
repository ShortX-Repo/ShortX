package tornaco.apps.shortx.ui.addrule.fact.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
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
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.ShortXTheme

class SystemSettingsUrlSelectorState(
    val onSelected: (List<String>) -> Unit
) : CommonDialogState() {
    private val logger = Logger("SystemSettingsUrlSelectorState")

    var selectedUrls by mutableStateOf(emptyList<String>())

    fun show(initialValue: List<String> = emptyList()) {
        logger.p("initialValue: $initialValue")

        this.selectedUrls = initialValue

        show()
    }

    fun addUrl(it: String) {
        if (!selectedUrls.contains(it)) {
            selectedUrls = selectedUrls.toMutableList().apply {
                add(it)
            }
        }
    }

    fun removeUrl(it: String) {
        selectedUrls = selectedUrls.toMutableList().apply {
            remove(it)
        }
    }
}

@Composable
fun rememberSystemSettingsUrlSelectorState(
    onSelected: (List<String>) -> Unit
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
                                        if (state.selectedUrls.isNotEmpty()) {
                                            state.onSelected(state.selectedUrls)
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
                                title = "Url",
                                message = null,
                                onSelected = {
                                    state.addUrl(it)
                                }
                            )
                            TextInputDialog(state = inputState)

                            FieldTitle(text = LocalI18N.current["ui.fact.system.settings.changed.url"])
                            state.selectedUrls.forEach {
                                ListItem(
                                    titleLines = listOf(it),
                                    icon = {
                                        RemixIcon(remixName = Remix.Logos.flickr_line)
                                    },
                                    action1 = {
                                        IconButton(onClick = {
                                            state.removeUrl(it)
                                        }) {
                                            RemixIcon(remixName = Remix.System.close_line)
                                        }
                                    },
                                    onClick = {
                                    })
                            }

                            LargeSpacer()
                            Button(onClick = {
                                inputState.show("content://settings/")
                            }) {
                                Text(text = LocalI18N.current["ui.common.add"])
                            }
                        }
                    }
                }
            }
        }
    }
}
