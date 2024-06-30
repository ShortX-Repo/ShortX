@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.allActionClasses
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.SearchBarState
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXFilterChip
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.theme.ShortXTheme
import kotlin.reflect.KClass

@DoNotStrip
enum class ActionCategory(val i18NKey: String, val icon: String) {
    All("ui.action.all", Remix.Buildings.store_2_line),
    Recent("ui.action.recent", Remix.System.history_line),
    Ui("UI", Remix.Design.layout_2_fill),
    Apps("ui.action.category.app", Remix.System.apps_2_fill),
    Notifications("ui.action.category.notification", Remix.System.notification_badge_fill),
    Settings("ui.action.system.settings", Remix.System.settings_2_fill),
    Life("ui.action.life", Remix.Map.riding_fill),
    Advanced("ui.action.advanced", Remix.System.external_link_fill),
    Vars("ui.action.var", Remix.System.function_fill),
    Functions("ui.action.functions", Remix.System.function_fill),
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
        ShortXAppBarScaffold(
            title = LocalI18N.current["ui.title.select.action"],
            onBackPressed = { close() }) {
            ClickOutsideClearFocusBox {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    val selectorVM = hiltViewModel<ActionSelectorViewModel>()
                    val selectorState by selectorVM.state.collectAsState()
                    val addAction: (Action) -> Unit = {
                        onSelected(it)
                        selectorVM.onActionAdd(it)
                    }

                    val searchBarState = rememberSearchBarState(
                        itemCount = allActionClasses.size,
                        keywordChanged = { })
                    SearchBar(searchBarState = searchBarState)

                    val selectedFilter = selectorState.selectedFilter
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        ActionCategory.entries.forEach {
                            ShortXFilterChip(
                                selected = selectedFilter == it,
                                label = {
                                    Text(text = LocalI18N.current[it.i18NKey])
                                },
                                onClick = {
                                    selectorVM.selectFilterCategory(it)
                                })
                            MediumSpacer()
                        }
                    }

                    StandardSpacer()
                    AllActions(
                        selectedFilter = selectedFilter,
                        recentActions = selectorState.recentActions,
                        searchBarState = searchBarState,
                        addAction = addAction
                    )
                }
            }
        }
    }
}

@Composable
private fun AllActions(
    selectedFilter: ActionCategory,
    recentActions: List<KClass<out Action>>,
    searchBarState: SearchBarState,
    addAction: (Action) -> Unit,
) {
    val subscribeState by LVLStateHolder.collectAsState()

    RecentActions(
        searchBarState = searchBarState,
        subscribeState = subscribeState,
        selectedFilter = selectedFilter,
        recentActions = recentActions,
        addAction = addAction
    )

    UiActions(searchBarState.keyword, subscribeState, selectedFilter, addAction = addAction)
    AppActions(searchBarState.keyword, subscribeState, selectedFilter, addAction = addAction)

    SystemActions(searchBarState.keyword, subscribeState, selectedFilter, addAction = addAction)

    NotificationActions(
        searchBarState.keyword,
        subscribeState,
        selectedFilter,
        addAction = addAction
    )

    ExtActions(searchBarState.keyword, subscribeState, selectedFilter, addAction = addAction)
    VarActions(searchBarState.keyword, subscribeState, selectedFilter, addAction = addAction)
    LifeActions(searchBarState.keyword, subscribeState, selectedFilter, addAction = addAction)
    FuncActions(searchBarState.keyword, subscribeState, selectedFilter, addAction = addAction)
    MiscActions(searchBarState.keyword, subscribeState, selectedFilter, addAction = addAction)
}

@Composable
private fun RecentActions(
    searchBarState: SearchBarState,
    subscribeState: LVLStateHolder.State,
    selectedFilter: ActionCategory,
    recentActions: List<KClass<out Action>>,
    addAction: (Action) -> Unit
) {
    val categoryState =
        rememberCategoryState(categoryTitle = LocalI18N.current[ActionCategory.Recent.i18NKey])
    if (recentActions.isNotEmpty() && selectedFilter == ActionCategory.All) {
        ActionCategory(
            LocalI18N.current[ActionCategory.Recent.i18NKey],
            recentActions.size,
            categoryState
        )
    }

    val isVisible = selectedFilter == ActionCategory.All || selectedFilter == ActionCategory.Recent
    AnimatedVisibility(
        visible = isVisible && searchBarState.keyword.isBlank()
                && recentActions.isNotEmpty()
    ) {
        Column {
            AnimatedVisibility(visible = categoryState.isExpand) {
                RecentActions(
                    recentActions = recentActions,
                    subscribeState = subscribeState,
                    addAction = addAction
                )
            }
            StandardSpacer()
        }
    }
}

