@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.allActionClasses
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.main.SubscribeStateViewModel
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class ActionCategory(val i18NKey: String, val icon: String) {
    Recent("ui.action.recent", Remix.System.history_line),
    Ui("UI", Remix.Design.layout_2_fill),
    Apps("ui.action.category.app", Remix.System.apps_2_fill),
    Settings("ui.action.system.settings", Remix.System.settings_2_fill),
    Life("ui.action.life", Remix.Map.riding_fill),
    Advanced("ui.action.advanced", Remix.System.function_fill),
    Misc("ui.action.advanced", Remix.Others.box_1_fill),
}

enum class TabItems(val i18NKey: String, val icon: String) {
    All("ui.action.all", Remix.Buildings.store_2_line),
    Remote("ui.action.remote", Remix.Business.cloud_line)
}

val initialPageIndex = TabItems.All.ordinal

class ActionSelectorDialogState<ARGS>(val onSelected: (Action, ARGS?) -> Unit) {
    var isShowing by mutableStateOf(false)
    var args: ARGS? = null

    fun show(args: ARGS? = null) {
        isShowing = true
        this.args = args
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun <ARGS> rememberActionSelectorDialogState(onSelected: (Action, ARGS?) -> Unit): ActionSelectorDialogState<ARGS> {
    return remember {
        ActionSelectorDialogState(onSelected)
    }
}

@Composable
fun <ARGS> rememberActionSelectorDialogState(onSelected: (Action) -> Unit): ActionSelectorDialogState<ARGS> {
    return remember {
        ActionSelectorDialogState { action, _ ->
            onSelected(action)
        }
    }
}

@Composable
fun <ARGS> ActionSelectorDialog(state: ActionSelectorDialogState<ARGS>) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ActionSelector(state)
        }
    }
}

@Composable
private fun <ARGS> ActionSelector(
    state: ActionSelectorDialogState<ARGS>
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.title.select.action"]) },
                        actions = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                    )
                }) { paddingValues ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    val pagerState = rememberPagerState(initialPage = initialPageIndex)
                    val scope = rememberCoroutineScope()

                    val items = TabItems.values().map { it.i18NKey }
                    val icons = TabItems.values().map { it.icon }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f, fill = false)
                        ) {
                            val selectorVM = hiltViewModel<ActionSelectorViewModel>()
                            val selectorState by selectorVM.state.collectAsState()

                            HorizontalPager(
                                state = pagerState,
                                pageCount = TabItems.values().size,
                            ) { index ->
                                val addAction: (Action) -> Unit = {
                                    state.onSelected(it, state.args)
                                    selectorVM.onActionAdd(it)
                                    state.dismiss()
                                }

                                when (index) {
                                    TabItems.Remote.ordinal -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp)
                                        ) {
                                            RemoteActions(addAction = addAction)
                                        }
                                    }

                                    TabItems.All.ordinal -> {
                                        AllActions(selectorState, addAction)
                                    }

                                    else -> {}
                                }
                            }
                        }

                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = { RemixIcon(remixName = icons[index]) },
                                    label = { Text(LocalI18N.current[item]) },
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AllActions(
    selectorState: SelectorState,
    addAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val searchBarState = rememberSearchBarState(
            itemCount = allActionClasses.size,
            keywordChanged = { })
        SearchBar(searchBarState = searchBarState)

        val subscribeStateViewModel = hiltViewModel<SubscribeStateViewModel>()
        LaunchedEffect(subscribeStateViewModel) {
            subscribeStateViewModel.refreshStates()
        }
        DisposableEffectWithLifeCycle(onResume = {
            subscribeStateViewModel.refreshStates()
        })

        val subscribeState by subscribeStateViewModel.state.collectAsState()

        AnimatedVisibility(visible = searchBarState.keyword.isBlank() && selectorState.recentActions.isNotEmpty()) {
            Column {
                ActionCategoryTitle(ActionCategory.Recent)
                RecentActions(
                    actions = selectorState,
                    subscribeState,
                    addAction = addAction
                )
                StandardSpacer()
            }
        }
        UiActions(searchBarState.keyword, subscribeState, addAction = addAction)
        StandardSpacer()

        AppActions(searchBarState.keyword, subscribeState, addAction = addAction)
        StandardSpacer()

        SystemActions(searchBarState.keyword, subscribeState, addAction = addAction)
        StandardSpacer()

        ExtActions(searchBarState.keyword, subscribeState, addAction = addAction)
        StandardSpacer()

        LifeActions(searchBarState.keyword, subscribeState, addAction = addAction)
        StandardSpacer()

        MiscActions(searchBarState.keyword, subscribeState, addAction = addAction)
        StandardSpacer()
    }
}

@Composable
fun ActionCategoryTitle(category: ActionCategory) {
    SmallTitle(
        modifier = Modifier.padding(vertical = 12.dp),
        text = LocalI18N.current[category.i18NKey]
    )
}