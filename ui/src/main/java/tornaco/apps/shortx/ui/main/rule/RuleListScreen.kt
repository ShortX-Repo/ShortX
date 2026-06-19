@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package tornaco.apps.shortx.ui.main.rule

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.AddRuleActivity
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.LoadState.Loading.data
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalUiSettings
import tornaco.apps.shortx.ui.base.NiaGradientBackground
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.TipIds
import tornaco.apps.shortx.ui.base.UserTip
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.MainScaffold
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.main.SettingsAppBarActions
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.uniqueUIKey
import tornaco.apps.shortx.ui.main.userSetIdOrNull
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.ViewerActivity
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.theme.gradientColorsFromThemeColor
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.io.File
import tornaco.apps.shortx.core.i18n.I18N_R


@Composable
fun RuleScreen() {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val mainState by mainViewModel.state.collectAsState()

    val ruleListViewModel = hiltViewModel<RuleListViewModel>()
    val ruleListState by ruleListViewModel.state.collectAsState()
    val ruleList by ruleListViewModel.ruleList.collectAsState()

    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { fileUri ->
        if (fileUri != null) {
            ViewerActivity.start(context, fileUri)
        }
    }

    val searchBarState = rememberSearchBarState(
        keywordChanged = {
            ruleListViewModel.updateSearchQuery(it)
        }
    )

    var selectedRuleIds by remember { mutableStateOf(emptySet<String>()) }
    val isSelectionMode = selectedRuleIds.isNotEmpty()
    val selectedRules = remember(ruleList, selectedRuleIds) {
        ruleList.data?.rules?.filter { selectedRuleIds.contains(it.id) } ?: emptyList()
    }

    val ruleHandler = getLocalRuleItemHandler(vm = ruleListViewModel) { rule ->
        selectedRuleIds = selectedRuleIds + rule.id
    }

    MainScaffold(
        state = mainState,
        appBarTitle = {
            Text(
                modifier = Modifier.clickable {
                    if (!isSelectionMode) {
                        ruleListViewModel.toggleShowRuleDescription()
                    }
                },
                text = if (isSelectionMode) {
                    LocalI18N.current[I18N_R.UI_COMMON_SELECTED_COUNT, mapOf("selected" to selectedRuleIds.size.toString())]
                } else {
                    LocalI18N.current[I18N_R.UI_FEAT_LABEL_RULE]
                }
            )
        },
        enableSearch = true,
        searchBarState = searchBarState,
        appBarNavigationIcon = {
            if (isSelectionMode) {
                IconButton(onClick = { selectedRuleIds = emptySet() }) {
                    RemixIcon(Remix.System.close_line)
                }
            } else {
                SettingsAppBarActions(mainState.hasSensorUsage)
            }
        },
        appBarActions = {
            if (isSelectionMode) {
                IconButton(onClick = {
                    ruleHandler.share(selectedRules)
                    selectedRuleIds = emptySet()
                }) {
                    RemixIcon(Remix.System.share_line)
                }
            } else {
                RuleSortSelector(
                    currentSortBy = ruleListState.ruleSortBy,
                    onSortByChanged = { sortBy ->
                        ruleListViewModel.setRuleSortBy(sortBy)
                    }
                )
                IconButton(onClick = {
                    ruleListViewModel.toggleOnlyShowEnabled()
                }) {
                    RemixIcon(
                        remixName = Remix.Hardware.switch_one,
                        tint = if (ruleListState.onlyShowEnabled) MaterialTheme.colorScheme.primary else Color.Unspecified
                    )
                }
            }
        },
        onClipboardClick = {
            ClipboardUtils.readClipboard(context)?.let { clip ->
                val file = File(context.cacheDir, "clipboard.content")
                file.writeText(clip)
                ViewerActivity.start(context, file.toUri())
            }
        },
        onFileClick = {
            pickFileLauncher.launch("text/plain")
        },
        onAddClick = {
            AddRuleActivity.create(
                context = context,
                ruleSetId = ruleList.data?.selectedRuleSetFilter?.userSetIdOrNull
            )
        },
        onUrlClick = {
            ViewerActivity.start(context, SharedData(SharedData.TYPE_RULE_URL, it))
        },
        onNewSetClick = {
            ruleListViewModel.createRuleSet(it)
        },
        acceptPrivacyStatement = {
            mainViewModel.acceptPrivacyStatement()
        },
        appBarContent = {
            RuleSetFlowRow(
                ruleSetFilters = ruleList.data?.ruleSetFilters ?: emptyList(),
                selectedRuleSetFilter = ruleList.data?.selectedRuleSetFilter
                    ?: CommonSetFilter.PinnedAll,
                selectRuleSetFilter = {
                    ruleListViewModel.selectRuleSetFilter(it)
                }
            )
        }
    ) {
        val bottomSheetState = rememberShortXBottomSheetState()
        RuleDetailsBottomSheet(dialogState = bottomSheetState)

        val setHandler =
            getLocalRuleSetItemHandler(bottomSheetState = bottomSheetState, vm = ruleListViewModel)
        CompositionLocalProvider(
            LocalRuleItemHandler provides ruleHandler,
            LocalRuleSetItemHandler provides setHandler,
        ) {
            when (ruleList) {
                is LoadState.Error -> {
                    LoadError(modifier = Modifier.fillMaxSize()) {
                    }
                }

                LoadState.Loading -> {
                    Loading(modifier = Modifier.fillMaxSize())
                }

                is LoadState.Success -> {
                    val activeJobIds by ruleListViewModel.activeJobIds.collectAsState()
                    RuleContent(
                        rules = (ruleList as LoadState.Success).data.rules,
                        showDescription = ruleListState.showDescription,
                        activeJobs = activeJobIds,
                        selectedRuleIds = selectedRuleIds,
                        onRuleSelectionChange = { rule, selected ->
                            selectedRuleIds = if (selected) {
                                selectedRuleIds + rule.id
                            } else {
                                selectedRuleIds - rule.id
                            }
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun RuleSetFlowRow(
    ruleSetFilters: List<CommonSetFilter> = emptyList(),
    selectedRuleSetFilter: CommonSetFilter = CommonSetFilter.PinnedAll,
    selectRuleSetFilter: (CommonSetFilter) -> Unit = {},
) {
    if (ruleSetFilters.any { !it.isStock }) {
        val context = LocalContext.current
        val pref = remember { Pref(context) }
        val miscToggles by pref.miscUiToggles.collectAsState(initial = emptySet())
        val isRowView = miscToggles.contains(KEY_RULE_LIST_RULE_SET_FILTER_ROW)
        val scope = rememberCoroutineScope()

        if (isRowView) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                itemsIndexed(ruleSetFilters) { index, rs ->
                    OutlinedToggleButton(
                        checked = selectedRuleSetFilter == rs,
                        onCheckedChange = {
                            selectRuleSetFilter(rs)
                        },
                        modifier = Modifier.padding(start = 2.dp),
                        shapes =
                            when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                ruleSetFilters.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            }
                    ) {
                        Text(rs.title)
                    }
                }

                item {
                    IconButton(onClick = {
                        scope.launch {
                            pref.miscUiTogglesOff(KEY_RULE_LIST_RULE_SET_FILTER_ROW)
                        }
                    }) {
                        RemixIcon(Remix.Editor.text_wrap)
                    }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                ruleSetFilters.forEachIndexed { index, rs ->
                    OutlinedToggleButton(
                        checked = selectedRuleSetFilter == rs,
                        onCheckedChange = {
                            selectRuleSetFilter(rs)
                        },
                        modifier = Modifier.padding(start = 2.dp),
                        shapes =
                            when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                ruleSetFilters.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            }
                    ) {
                        Text(rs.title)
                    }
                }

                IconButton(onClick = {
                    scope.launch {
                        pref.miscUiTogglesOn(KEY_RULE_LIST_RULE_SET_FILTER_ROW)
                    }
                }) {
                    RemixIcon(Remix.Editor.text_wrap)
                }
            }
        }
    }
}

@Composable
fun RuleContent(
    showDescription: Boolean,
    rules: List<RuleUM>,
    activeJobs: List<String>,
    selectedRuleIds: Set<String> = emptySet(),
    onRuleSelectionChange: (RuleUM, Boolean) -> Unit = { _, _ -> },
) {
    Column {
        if (rules.isEmpty()) {
            EmptyList(modifier = Modifier.fillMaxSize())
        } else {

            UserTip(
                modifier = Modifier.padding(horizontal = 16.dp),
                tipId = TipIds.commonRuleItem,
                message = LocalI18N.current[I18N_R.UI_COMMON_RULE_ITEM_TIP]
            )

            val subscribeState by LVLStateHolder.collectAsState()
            RuleListContent(
                isSubscribed = subscribeState.isSubscribed,
                rules = rules,
                showDescription = showDescription,
                activeJobs = activeJobs,
                selectedRuleIds = selectedRuleIds,
                onRuleSelectionChange = onRuleSelectionChange
            )
        }
    }
}

@Composable
private fun RuleListContent(
    isSubscribed: Boolean,
    showDescription: Boolean,
    rules: List<RuleUM>,
    activeJobs: List<String>,
    selectedRuleIds: Set<String>,
    onRuleSelectionChange: (RuleUM, Boolean) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = dynamicStaggeredGridCells(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 160.dp)
    ) {
        items(
            items = rules,
            key = { listItem: RuleUM -> listItem.uniqueUIKey }) { rule ->
            ShortXCard(
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                backgroundColor =
                    if (LocalUiSettings.current.uiExperimental) {
                        Color.Transparent
                    } else {
                        if (!LocalUiSettings.current.uiColorfulCard || rule.isRuleSet) {
                            ColorDefaults.backgroundSurfaceColor()
                        } else {
                            ruleIconColor(rule).copy(alpha = ActionColorBgAlpha)
                        }
                    }
            ) {
                val content = movableContentOf {
                    if (rule.isRuleSet) {
                        RuleSetItem(
                            rule = rule,
                            isSubscribed = isSubscribed
                        )
                    } else {
                        val runningInsCount = remember(rule.id, activeJobs) {
                            activeJobs.count { it == rule.id }
                        }
                        RuleItem(
                            rule = rule,
                            isSubscribed = isSubscribed,
                            showDescription = showDescription,
                            runningInsCount = runningInsCount,
                            isSelectionMode = selectedRuleIds.isNotEmpty(),
                            selected = selectedRuleIds.contains(rule.id),
                            onSelectionChange = { onRuleSelectionChange(rule, it) }
                        )
                    }
                }

                if (LocalUiSettings.current.uiExperimental) {
                    NiaGradientBackground(
                        gradientColors = gradientColorsFromThemeColor(ruleIconColor(rule))
                    ) {
                        content()
                    }
                } else {
                    content()
                }
            }
        }
    }
}

private const val KEY_RULE_LIST_RULE_SET_FILTER_ROW = "feat.rule.list.rule.set.filter.row"
