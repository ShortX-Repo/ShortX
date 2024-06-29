@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)

package tornaco.apps.shortx.ui.main.rule

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.AddRuleActivity
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXFilterChip
import tornaco.apps.shortx.ui.base.TipIds
import tornaco.apps.shortx.ui.base.UserTip
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.MainScaffold
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.userSetIdOrNull
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.ViewerActivity
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.io.File


@Composable
fun RuleScreen() {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val mainState by mainViewModel.state.collectAsState()

    val ruleListViewModel = hiltViewModel<RuleListViewModel>()
    val ruleListState by ruleListViewModel.state.collectAsState()

    LaunchedEffect(ruleListViewModel) {
        ruleListViewModel.loadRuleList(delay = false)
    }

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

    MainScaffold(
        state = mainState,
        appBarTitleKey = "ui.feat.label.rule",
        enableSearch = true,
        searchBarState = searchBarState,
        appBarActions = {
            IconButton(onClick = {
                ruleListViewModel.toggleShowRuleDescription()
            }) {
                RemixIcon(remixName = Remix.Design.collage_line)
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
                ruleSetId = ruleListState.selectedRuleSetFilter.userSetIdOrNull
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
                ruleSetFilters = ruleListState.ruleSetFilters,
                selectedRuleSetFilter = ruleListState.selectedRuleSetFilter,
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
        val ruleHandler = getLocalRuleItemHandler(vm = ruleListViewModel)
        CompositionLocalProvider(
            LocalRuleItemHandler provides ruleHandler,
            LocalRuleSetItemHandler provides setHandler,
        ) {
            RuleContent(
                isLoading = ruleListState.isLoading,
                colorfulCard = ruleListState.colorfulCard,
                rules = ruleListState.rules,
                showDescription = ruleListState.showDescription
            )
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
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            ruleSetFilters.forEach {
                ShortXFilterChip(
                    modifier = Modifier.padding(start = 16.dp),
                    selected = selectedRuleSetFilter == it,
                    label = {
                        Text(text = it.title)
                    },
                    onClick = {
                        selectRuleSetFilter(it)
                    })
            }
        }
    }
}

@Composable
fun RuleContent(
    isLoading: Boolean,
    colorfulCard: Boolean,
    showDescription: Boolean,
    rules: List<RuleUM>
) {
    val refreshState = rememberPullRefreshState(isLoading, onRefresh = {
    })
    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
    ) {
        Column {
            if (!isLoading && rules.isEmpty()) {
                EmptyList(modifier = Modifier.fillMaxSize())
            } else {
                if (!isLoading) {
                    UserTip(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        tipId = TipIds.commonRuleItem,
                        message = LocalI18N.current["ui.common.rule.item.tip"]
                    )
                }

                val subscribeState by LVLStateHolder.collectAsState()
                RuleListContent(
                    isSubscribed = subscribeState.isSubscribed,
                    rules = rules,
                    colorfulCard = colorfulCard,
                    showDescription = showDescription
                )
            }
        }

        PullRefreshIndicator(
            isLoading,
            refreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun RuleListContent(
    isSubscribed: Boolean,
    colorfulCard: Boolean,
    showDescription: Boolean,
    rules: List<RuleUM>,
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = dynamicStaggeredGridCells(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 72.dp)
    ) {
        items(
            items = rules,
            key = { listItem: RuleUM -> listItem.id + listItem.updateTime }) { rule ->
            ShortXCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                backgroundColor = if (!colorfulCard || rule.isRuleSet) {
                    ColorDefaults.backgroundSurfaceColor()
                } else {
                    Color(rule.factIconColor).copy(alpha = ActionColorBgAlpha)
                }
            ) {
                if (rule.isRuleSet) {
                    RuleSetItem(
                        rule = rule,
                        isSubscribed = isSubscribed
                    )
                } else {
                    RuleItem(
                        rule = rule,
                        isSubscribed = isSubscribed,
                        colorfulCard = colorfulCard,
                        showDescription = showDescription
                    )
                }
            }
        }
    }
}
