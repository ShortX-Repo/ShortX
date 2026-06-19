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
import tornaco.apps.shortx.core.proto.action.NotificationButton
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
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
import tornaco.apps.shortx.ui.base.RegexMatchOptionsDropdown
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class NotificationAppAndMatcherSelectorState(
    val title: String,
    val canMatchTitle: Boolean,
    val canMatchContent: Boolean,
    val canMatchButtonTitle: Boolean,
    val onSelected: (Fact.Notification, NotificationButton) -> Unit
) : CommonDialogState() {
    var titleRegex by mutableStateOf("")
    var contentRegex by mutableStateOf("")
    var buttonTitleRegex by mutableStateOf("")
    var titleMatchOptions by mutableStateOf(RegexMatchOptions.RegexMatchOptions_Match)
    var contentMatchOptions by mutableStateOf(RegexMatchOptions.RegexMatchOptions_Match)
    var buttonTitleMatchOptions by mutableStateOf(RegexMatchOptions.RegexMatchOptions_Match)

    var tag by mutableStateOf("")

    var selectedApps by mutableStateOf(emptyList<App>())
    var selectedPkgSets by mutableStateOf(emptyList<PkgSet>())

    fun show(initialValue: Fact.Notification? = null, button: NotificationButton? = null) {
        this.titleRegex = initialValue?.titleRegex ?: ""
        this.contentRegex = initialValue?.contentRegex ?: ""
        this.buttonTitleRegex = button?.label ?: ""

        this.titleMatchOptions =
            initialValue?.titleMatchOptions ?: RegexMatchOptions.RegexMatchOptions_Match
        this.contentMatchOptions =
            initialValue?.contentMatchOptions ?: RegexMatchOptions.RegexMatchOptions_Match
        this.buttonTitleMatchOptions =
            button?.labelRegexOptions ?: RegexMatchOptions.RegexMatchOptions_Match

        this.tag = initialValue?.tag ?: ""

        this.selectedApps = initialValue?.apps ?: emptyList()
        this.selectedPkgSets = initialValue?.pkgSets ?: emptyList()

        show()
    }
}

@Composable
fun rememberNotificationAppAndMatcherSelectorState(
    title: String,
    canMatchTitle: Boolean = true,
    canMatchContent: Boolean = true,
    canMatchButtonTitle: Boolean = false,
    onSelected: (Fact.Notification, NotificationButton) -> Unit
): NotificationAppAndMatcherSelectorState {
    return remember {
        NotificationAppAndMatcherSelectorState(
            title,
            canMatchTitle,
            canMatchContent,
            canMatchButtonTitle,
            onSelected
        )
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
                                                titleMatchOptions = state.titleMatchOptions,
                                                contentMatchOptions = state.contentMatchOptions,
                                                tag = state.tag,
                                                apps = state.selectedApps,
                                                pkgSets = state.selectedPkgSets
                                            ),
                                            NotificationButton.newBuilder()
                                                .setLabel(state.buttonTitleRegex)
                                                .setLabelRegexOptions(state.buttonTitleMatchOptions)
                                                .build()
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

                                    FieldTitle(text = LocalI18N.current[I18N_R.UI_DIALOG_TITLE_SELECT_APP] + " - " + LocalI18N.current[I18N_R.UI_COMMON_OPTIONAL])
                                    ListItem(
                                        icon = {
                                            RemixIcon(remixName = Remix.System.apps_2_line)
                                        },
                                        titleLines = mutableListOf(
                                            LocalI18N.current[I18N_R.UI_DIALOG_TITLE_SELECT_APP],
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
                                    if (state.canMatchTitle) {
                                        Column {
                                            FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_N_MATCHER_TITLE])
                                            MediumSpacer()
                                            TipBody(text = LocalI18N.current[I18N_R.UI_FACT_N_MATCHER_TITLE_MESSAGE])
                                            MediumSpacer()
                                            ShortXTextField(
                                                modifier = Modifier.fillMaxWidth(),
                                                value = state.titleRegex,
                                                onValueChange = { state.titleRegex = it })

                                            SmallSpacer()

                                            RegexMatchOptionsDropdown(
                                                options = state.titleMatchOptions,
                                                onSelectNewOptions = {
                                                    state.titleMatchOptions = it
                                                }
                                            )
                                        }
                                    }
                                },
                                {
                                    if (state.canMatchContent) {
                                        Column {
                                            FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_N_MATCHER_CONTENT])
                                            MediumSpacer()
                                            TipBody(text = LocalI18N.current[I18N_R.UI_FACT_N_MATCHER_CONTENT_MESSAGE])
                                            MediumSpacer()
                                            ShortXTextField(
                                                modifier = Modifier.fillMaxWidth(),
                                                value = state.contentRegex,
                                                onValueChange = { state.contentRegex = it })

                                            SmallSpacer()

                                            RegexMatchOptionsDropdown(
                                                options = state.contentMatchOptions,
                                                onSelectNewOptions = {
                                                    state.contentMatchOptions = it
                                                }
                                            )
                                        }
                                    }
                                },
                                {
                                    FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_N_MATCHER_TAG])
                                    MediumSpacer()
                                    TipBody(text = LocalI18N.current[I18N_R.UI_FACT_N_MATCHER_TAG_MESSAGE])
                                    MediumSpacer()
                                    ShortXTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = state.tag,
                                        onValueChange = { state.tag = it })
                                },
                                {
                                    if (state.canMatchButtonTitle) {
                                        Column {
                                            FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_N_MATCHER_BUTTON])
                                            MediumSpacer()
                                            TipBody(text = LocalI18N.current[I18N_R.UI_FACT_N_MATCHER_BUTTON_MESSAGE])
                                            MediumSpacer()
                                            ShortXTextField(
                                                modifier = Modifier.fillMaxWidth(),
                                                value = state.buttonTitleRegex,
                                                onValueChange = { state.buttonTitleRegex = it })

                                            SmallSpacer()

                                            RegexMatchOptionsDropdown(
                                                options = state.buttonTitleMatchOptions,
                                                onSelectNewOptions = {
                                                    state.buttonTitleMatchOptions = it
                                                }
                                            )
                                        }
                                    }
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
