package tornaco.apps.shortx.ui.addrule.fact.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

class NotificationAppAndMatcherSelectorState(
    val title: String,
    val onSelected: (Fact.Notification) -> Unit
) : CommonDialogState() {
    var titleRegex by mutableStateOf("")
    var contentRegex by mutableStateOf("")
    var tagRegex by mutableStateOf("")

    var selectedApps by mutableStateOf(emptyList<App>())
    var selectedPkgSets by mutableStateOf(emptyList<PkgSet>())


    fun show(initialValue: Fact.Notification? = null) {
        this.titleRegex = initialValue?.titleRegex ?: ""
        this.contentRegex = initialValue?.contentRegex ?: ""
        this.tagRegex = initialValue?.tagRegex ?: ""

        this.selectedApps = initialValue?.apps ?: emptyList()
        this.selectedPkgSets = initialValue?.pkgSets ?: emptyList()

        show()
    }
}

@Composable
fun rememberNotificationAppAndMatcherSelectorState(
    title: String,
    onSelected: (Fact.Notification) -> Unit
): NotificationAppAndMatcherSelectorState {
    return remember {
        NotificationAppAndMatcherSelectorState(title, onSelected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationAppAndMatcherSelectorDialog(state: NotificationAppAndMatcherSelectorState) {
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
                                title = { Text(text = state.title) },
                                actions = {
                                    IconButton(onClick = {
                                        state.onSelected(
                                            Fact.Notification(
                                                titleRegex = state.titleRegex,
                                                contentRegex = state.contentRegex,
                                                tagRegex = state.tagRegex,
                                                apps = state.selectedApps,
                                                pkgSets = state.selectedPkgSets
                                            )
                                        )
                                        state.dismiss()

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
                            arrayOf<@Composable () -> Unit>(
                                {
                                    val appPkgSelectorState = rememberAppPkgSelectorState(
                                        onAppListSelected = {
                                            state.selectedApps = it
                                            state.selectedPkgSets = emptyList()
                                        },
                                        onPkgSetSelected = {
                                            state.selectedApps = emptyList()
                                            state.selectedPkgSets = it
                                        }
                                    )
                                    AppPkgSelectorDialog(state = appPkgSelectorState)

                                    FieldTitle(text = LocalI18N.current["ui.dialog.title.select.app"])
                                    ListItem(
                                        icon = {
                                            RemixIcon(remixName = Remix.System.apps_2_line)
                                        },
                                        titleLines = mutableListOf(
                                            LocalI18N.current["ui.dialog.title.select.app"],
                                        ).labelLinesFromAppsAndPkgSets(
                                            apps = state.selectedApps,
                                            pkgSets = state.selectedPkgSets
                                        ),
                                        onClick = {
                                            appPkgSelectorState.show(
                                                currentAppList = state.selectedApps,
                                                currentPkgSet = state.selectedPkgSets
                                            )
                                        }
                                    )
                                },


                                {
                                    FieldTitle(text = LocalI18N.current["ui.fact.n.matcher.title"])
                                    MediumSpacer()
                                    TipBody(text = LocalI18N.current["ui.fact.n.matcher.title.message"])
                                    MediumSpacer()
                                    ShortXTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = state.titleRegex,
                                        onValueChange = { state.titleRegex = it })
                                },
                                {
                                    FieldTitle(text = LocalI18N.current["ui.fact.n.matcher.content"])
                                    MediumSpacer()
                                    TipBody(text = LocalI18N.current["ui.fact.n.matcher.content.message"])
                                    MediumSpacer()
                                    ShortXTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = state.contentRegex,
                                        onValueChange = { state.contentRegex = it })
                                },
                                {
                                    FieldTitle(text = LocalI18N.current["ui.fact.n.matcher.tag"])
                                    MediumSpacer()
                                    TipBody(text = LocalI18N.current["ui.fact.n.matcher.tag.message"])
                                    MediumSpacer()
                                    ShortXTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = state.tagRegex,
                                        onValueChange = { state.tagRegex = it })
                                },

                                ).forEach {
                                it()
                                LargeSpacer()
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
