@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package tornaco.apps.shortx.ui.discover

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.BuildProp.SHORTX_GITHIB_REPO_URL
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.action.selector.initialPageIndex
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXTabRow
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.discover.contribute.ContributorActivity
import tornaco.apps.shortx.ui.main.MainScaffold
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.main.da.DAViewerDialog
import tornaco.apps.shortx.ui.main.da.rememberDAViewerState
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.rule.RuleViewerDialog
import tornaco.apps.shortx.ui.main.rule.rememberRuleViewerState
import tornaco.apps.shortx.ui.settings.pref.DiscoveryRepoSetting
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder

enum class DiscoverTabItem(val titleKey: String, val iconName: String) {
    DA("ui.feat.label.da", Remix.Development.command_line),
    Rule("ui.feat.label.rule", Remix.Weather.meteor_line)
}

@Composable
fun DiscoverScreen() {
    val vm = hiltViewModel<MainViewModel>()
    val mainState by vm.state.collectAsState()

    val discoverModel = hiltViewModel<DiscoverViewModel>()
    val discoverState by discoverModel.state.collectAsState()

    LaunchedEffect(vm) {
        discoverModel.loadDiscoverData()
    }

    val newRepoDialogState =
        rememberTextInputState(
            title = LocalI18N.current["ui.discover.source.add.custom"],
            message = LocalI18N.current["ui.discover.source.add.custom.description"],
            showSymbolButton = true,
            onSelected = {
                discoverModel.addCustomRepo(
                    DiscoveryRepoSetting(
                        // "https://raw.githubusercontent.com/ShortX-Repo/Files/main/index.json"
                        alias = it.substringAfter("//").substringBefore("/"),
                        url = it
                    )
                )
            })
    TextInputDialog(state = newRepoDialogState)

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    val globalSubscribeState by LVLStateHolder.collectAsState()

    @Composable
    fun getRepoTitle(discoverState: DiscoverState): String {
        return when (val repo = discoverState.discoverRepo) {
            is DiscoverRepo.CustomOnline -> {
                discoverState.customRepos.firstOrNull { it.url == repo.indexUrl }?.alias.orEmpty()
            }

            DiscoverRepo.OfficialOnline -> {
                "ShortX-Repo"
            }

            DiscoverRepo.PrebuiltLocal -> {
                LocalI18N.current["ui.discover.source.prebuilt"]
            }
        }
    }

    val repoSelectState = rememberMenuDialogState<String>(
        key1 = discoverState.customRepos,
        title = LocalI18N.current["ui.discover.select.source"],
        menuItems = mutableListOf(
            MenuDialogItem(
                id = "local",
                title = LocalI18N.current["ui.discover.source.prebuilt"],
                iconName = Remix.Document.file_2_line
            ),
            MenuDialogItem(
                id = "officialOnline",
                title = "ShortX-Repo",
                summary = SHORTX_GITHIB_REPO_URL,
                iconName = Remix.Business.global_line
            ),
        ).apply {
            addAll(discoverState.customRepos.map {
                MenuDialogItem(
                    id = it.url,
                    title = it.alias,
                    summary = it.url,
                    iconName = Remix.Business.global_line,
                    customAction = Remix.System.delete_bin_2_line to {
                        discoverModel.removeCustomRepo(it)
                    }
                )
            })

            add(
                MenuDialogItem(
                    id = "new",
                    title = LocalI18N.current["ui.discover.source.add.custom"],
                    textColor = MaterialTheme.colorScheme.primary,
                    iconName = Remix.System.add_line
                ),
            )
        },
        onItemSelected = { _, id ->
            when (id) {
                "local" -> discoverModel.switchRepo(DiscoverRepo.PrebuiltLocal)
                "officialOnline" -> globalSubscribeState.isSubscribed.select({
                    discoverModel.switchRepo(DiscoverRepo.OfficialOnline)
                }, {
                    subscribeDialogState.show()
                })

                "new" -> globalSubscribeState.isSubscribed.select({
                    newRepoDialogState.show()
                }, {
                    subscribeDialogState.show()
                })
                // Custom with url
                else -> discoverModel.switchRepo(DiscoverRepo.CustomOnline(id))
            }
        })
    MenuDialog(state = repoSelectState)


    MainScaffold(
        state = mainState,
        subscribeState = globalSubscribeState,
        appBarTitle = {
            Column {
                Text(
                    text = LocalI18N.current["ui.feat.label.discover"]
                )
                Text(
                    text = getRepoTitle(discoverState),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        enableMask = false,
        appBarActions = {
            IconButton(onClick = {
                val selections = when (val currentRepo = discoverState.discoverRepo) {
                    DiscoverRepo.PrebuiltLocal -> {
                        listOf("local")
                    }

                    DiscoverRepo.OfficialOnline -> {
                        listOf("officialOnline")
                    }

                    is DiscoverRepo.CustomOnline -> {
                        listOf(currentRepo.indexUrl)
                    }
                }
                repoSelectState.show(currentSelection = selections)
            }) {
                RemixIcon(remixName = Remix.Business.global_line)
            }
        },
        floatingActionButton = {},
        onMaskClickOutside = {},
        acceptPrivacyStatement = {
            vm.acceptPrivacyStatement()
        },
        appBarContent = {}
    ) {
        val pagerState = rememberPagerState(
            initialPage = initialPageIndex,
            pageCount = {
                DiscoverTabItem.entries.size
            })
        val scope = rememberCoroutineScope()

        Column {
            ShortXTabRow(
                items = DiscoverTabItem.entries.map { LocalI18N.current[it.titleKey] },
                selectedTabIndex = pagerState.currentPage,
                onSelectedTab = {
                    scope.launch { pagerState.animateScrollToPage(it) }
                }
            )

            HorizontalPager(
                state = pagerState
            ) { index ->
                when (index) {
                    DiscoverTabItem.DA.ordinal -> {
                        DiscoverDA(discoverState = discoverState, refresh = {
                            discoverModel.loadDiscoverData()
                        }) {
                            if (discoverState.discoverRepo == DiscoverRepo.OfficialOnline) {
                                ContributorFooter()
                            }
                        }
                    }

                    DiscoverTabItem.Rule.ordinal -> {
                        DiscoverRule(discoverState = discoverState, refresh = {
                            discoverModel.loadDiscoverData()
                        }) {
                            if (discoverState.discoverRepo == DiscoverRepo.OfficialOnline) {
                                ContributorFooter()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContributorFooter() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        LinkText("View Contributors") { ContributorActivity.start(context) }
    }
}

@Composable
private fun DiscoverRule(
    discoverState: DiscoverState,
    refresh: () -> Unit,
    footer: @Composable () -> Unit
) {
    val refreshing = discoverState.data.isLoading()
    val refreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refresh()
    })

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
    ) {
        when (val discoverData = discoverState.data) {
            is LoadState.Success -> {
                if (discoverData.data.rules.isEmpty()) {
                    EmptyList(modifier = Modifier.fillMaxSize()) {
                        refresh()
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = dynamicStaggeredGridCells(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
                    ) {
                        items(
                            items = discoverData.data.rules,
                            key = { listItem: RuleUM -> listItem.id + listItem.updateTime }) { rule ->

                            val ruleViewerState = rememberRuleViewerState(
                                title = rule.title,
                                rule = rule.rule,
                                url = rule.url
                            )
                            RuleViewerDialog(state = ruleViewerState)

                            ShortXCard(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(ShortXCardRoundedCornerShape)
                                        .clickable { ruleViewerState.show() }
                                        .padding(horizontal = 12.dp)
                                        .padding(top = 12.dp)) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            ColoredIcon(
                                                modifier = Modifier,
                                                size = 32.dp,
                                                padding = 0.dp,
                                                color = Color(rule.iconColor).copy(alpha = ActionColorBgAlpha)
                                            ) {
                                                RemixIcon(
                                                    modifier = Modifier.size(16.dp),
                                                    remixName = Remix.Weather.meteor_line,
                                                    tint = MaterialTheme.colorScheme.tertiary
                                                )
                                            }
                                        }

                                        Row(modifier = Modifier) {
                                            if (rule.hasUpdate) {
                                                OutlineBadge(
                                                    modifier = Modifier,
                                                    text = "Update"
                                                )
                                            }
                                            if (rule.isInstalled) {
                                                OutlineBadge(
                                                    modifier = Modifier.padding(horizontal = 12.dp),
                                                    text = "Installed"
                                                )
                                            }
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
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
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 8
                                        )

                                        StandardSpacer()
                                        TipRow(
                                            text = rule.author,
                                            icon = {
                                                RemixIcon(
                                                    modifier = Modifier.size(12.dp),
                                                    remixName = Remix.UserAndFaces.user_line
                                                )
                                            })
                                    }
                                }
                            }
                        }
                        item(span = StaggeredGridItemSpan.FullLine) {
                            footer()
                        }
                    }
                }
            }

            is LoadState.Error -> {
                LoadError(
                    modifier = Modifier.fillMaxSize(),
                    message = discoverData.err?.stackTraceToString()
                ) {
                    refresh()
                }
            }

            is LoadState.Loading -> {
                // Noop
            }
        }

        PullRefreshIndicator(
            refreshing,
            refreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )
    }


}

@Composable
private fun DiscoverDA(
    discoverState: DiscoverState,
    refresh: () -> Unit,
    footer: @Composable () -> Unit
) {
    val refreshing = discoverState.data.isLoading()
    val refreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refresh()
    })

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
    ) {
        when (val discoverData = discoverState.data) {
            is LoadState.Success -> {
                if (discoverData.data.das.isEmpty()) {
                    EmptyList(modifier = Modifier.fillMaxSize()) {
                        refresh()
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = dynamicStaggeredGridCells(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
                    ) {
                        items(items = discoverData.data.das, key = { it.id }) {
                            val daViewerState = rememberDAViewerState(
                                title = it.title,
                                url = it.url,
                                da = it.directAction
                            )
                            DAViewerDialog(state = daViewerState)

                            ShortXCard(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(ShortXCardRoundedCornerShape)
                                        .clickable { daViewerState.show() }
                                        .padding(horizontal = 12.dp)
                                        .padding(top = 12.dp)) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            ColoredIcon(
                                                size = 32.dp,
                                                padding = 0.dp,
                                                color = Color(it.iconColor).copy(alpha = ActionColorBgAlpha)
                                            ) {
                                                RemixIcon(
                                                    modifier = Modifier.size(16.dp),
                                                    remixName = Remix.Development.command_line,
                                                    tint = MaterialTheme.colorScheme.tertiary
                                                )
                                            }
                                        }

                                        Row(modifier = Modifier) {
                                            if (it.hasUpdate) {
                                                OutlineBadge(
                                                    modifier = Modifier,
                                                    text = "Update"
                                                )
                                            }
                                            if (it.isInstalled) {
                                                OutlineBadge(
                                                    modifier = Modifier.padding(horizontal = 12.dp),
                                                    text = "Installed"
                                                )
                                            }
                                        }
                                    }

                                    MediumSpacer()

                                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                        Text(
                                            text = it.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 16.sp
                                            )
                                        )
                                        SmallSpacer()
                                        Text(
                                            text = it.description,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 8
                                        )

                                        StandardSpacer()
                                        TipRow(
                                            text = it.author,
                                            icon = {
                                                RemixIcon(
                                                    modifier = Modifier.size(12.dp),
                                                    remixName = Remix.UserAndFaces.user_line
                                                )
                                            })
                                    }

                                }
                            }
                        }

                        item(span = StaggeredGridItemSpan.FullLine) {
                            footer()
                        }
                    }
                }
            }

            is LoadState.Error -> {
                LoadError(
                    modifier = Modifier.fillMaxSize(),
                    message = discoverData.err?.stackTraceToString()
                ) {
                    refresh()
                }
            }

            is LoadState.Loading -> {
                // Noop
            }
        }

        PullRefreshIndicator(
            refreshing,
            refreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}
