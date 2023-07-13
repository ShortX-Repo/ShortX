@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.action.selector.initialPageIndex
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXTabRow
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.adaptiveCardSizeMin

enum class DiscoverTabItem(val titleKey: String, val iconName: String) {
    DA("ui.feat.label.da", Remix.Development.command_line),
    Rule("ui.feat.label.rule", Remix.Weather.meteor_line)
}

@Composable
fun DiscoverScreen() {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.refreshStates()
    }

    MainScaffold(
        state = state,
        appBarTitleKey = "ui.feat.label.discover",
        enableMask = false,
        appBarActions = {

        },
        floatingActionButton = {},
        onMaskClickOutside = {},
        acceptPrivacyStatement = {
            vm.acceptPrivacyStatement()
        }
    ) {
        val pagerState = rememberPagerState(initialPage = initialPageIndex)
        val scope = rememberCoroutineScope()

        Column {
            ShortXTabRow(
                items = DiscoverTabItem.values().map { LocalI18N.current[it.titleKey] },
                selectedTabIndex = pagerState.currentPage,
                onSelectedTab = {
                    scope.launch { pagerState.animateScrollToPage(it) }
                }
            )

            HorizontalPager(
                state = pagerState,
                pageCount = DiscoverTabItem.values().size,
            ) { index ->
                when (index) {
                    DiscoverTabItem.DA.ordinal -> {
                        DiscoverDA()
                    }

                    DiscoverTabItem.Rule.ordinal -> {
                        DiscoverRule()
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverRule() {
    val discoverModel = hiltViewModel<DiscoverModel>()
    val discoverState by discoverModel.state.collectAsState()

    if (discoverState.isLoading) {
        Loading(Modifier.fillMaxSize())
    } else {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(adaptiveCardSizeMin),
            contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
        ) {
            items(
                items = discoverState.rules,
                key = { listItem: RuleUM -> listItem.id + listItem.updateTime }) { rule ->

                val ruleViewerState = rememberRuleViewerState(rule = rule.rule)
                RuleViewerDialog(state = ruleViewerState)

                ShortXCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShortXCardRoundedCornerShape)
                        .clickable { ruleViewerState.show() }) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RuleIcon(rule)
                        }

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 6.dp, bottom = 12.dp)
                        ) {
                            Text(
                                text = rule.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp
                                )
                            )
                            TinySpacer()
                            Text(
                                text = rule.description,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                maxLines = 8
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverDA() {
    val discoverModel = hiltViewModel<DiscoverModel>()
    val discoverState by discoverModel.state.collectAsState()

    if (discoverState.isLoading) {
        Loading(Modifier.fillMaxSize())
    } else {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(adaptiveCardSizeMin),
            contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
        ) {
            items(items = discoverState.das, key = { it.id }) {
                val daViewerState = rememberDAViewerState(rule = it.directAction)
                DAViewerDialog(state = daViewerState)

                ShortXCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clip(ShortXCardRoundedCornerShape)
                        .clickable { daViewerState.show() }
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp)) {

                        ColoredIcon(
                            size = 32.dp,
                            padding = 0.dp,
                            color = Color(it.iconColor).copy(alpha = ActionColorBgAlpha)
                        ) {
                            RemixIcon(
                                modifier = Modifier.size(16.dp),
                                remixName = it.icon,
                                tint = Color(it.iconColor)
                            )
                        }

                        MediumSpacer()

                        Column(modifier = Modifier.padding(bottom = 12.dp)) {
                            Text(
                                text = it.title, style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp
                                )
                            )
                            SmallSpacer()
                            Text(
                                text = it.description,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                maxLines = 8
                            )
                        }

                    }
                }
            }
        }
    }
}
