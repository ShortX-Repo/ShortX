@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.allActionClasses
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class ActionCategory(val i18NKey: String, val icon: String) {
    Recent("ui.action.recent", Remix.System.history_line),
    Ui("UI", Remix.Design.layout_2_fill),
    Apps("ui.action.category.app", Remix.System.apps_2_fill),
    Settings("ui.action.system.settings", Remix.System.settings_2_fill),
    Life("ui.action.life", Remix.Map.riding_fill),
    Advanced("ui.action.advanced", Remix.System.function_fill),
    Vars("ui.action.var", Remix.System.function_fill),
    Misc("ui.action.misc", Remix.Others.box_1_fill),
}

enum class TabItems(val i18NKey: String, val icon: String) {
    All("ui.action.all", Remix.Buildings.store_2_line),
    Remote("ui.action.remote", Remix.Business.cloud_line)
}

val initialPageIndex = TabItems.All.ordinal

@Composable
fun ActionSelector(close: () -> Unit, onSelected: (Action) -> Unit) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = LocalI18N.current["ui.title.select.action"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                close()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {},
                        scrollBehavior = scrollBehavior
                    )
                }) { paddingValues ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    ClickOutsideClearFocusBox {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            val selectorVM = hiltViewModel<ActionSelectorViewModel>()
                            val selectorState by selectorVM.state.collectAsState()
                            val addAction: (Action) -> Unit = {
                                onSelected(it)
                                selectorVM.onActionAdd(it)
                            }
                            AllActions(selectorState, addAction)
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
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val searchBarState = rememberSearchBarState(
            itemCount = allActionClasses.size,
            keywordChanged = { })
        SearchBar(searchBarState = searchBarState)

        val subscribeState by LVLStateHolder.collectAsState()

        AnimatedVisibility(visible = searchBarState.keyword.isBlank() && selectorState.recentActions.isNotEmpty()) {
            Column {
                val categoryState =
                    rememberCategoryState(categoryTitle = LocalI18N.current[ActionCategory.Recent.i18NKey])
                ActionCategory(
                    LocalI18N.current[ActionCategory.Recent.i18NKey],
                    selectorState.recentActions.size,
                    categoryState
                )
                AnimatedVisibility(visible = categoryState.isExpand) {
                    RecentActions(
                        actions = selectorState,
                        subscribeState = subscribeState,
                        addAction = addAction
                    )
                }
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

        VarActions(searchBarState.keyword, subscribeState, addAction = addAction)
        StandardSpacer()

        LifeActions(searchBarState.keyword, subscribeState, addAction = addAction)
        StandardSpacer()

        MiscActions(searchBarState.keyword, subscribeState, addAction = addAction)
        StandardSpacer()
    }
}

