@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class ActionCategory(val i18NKey: String, val icon: String) {
    Ui("UI", Remix.Design.layout_2_line),
    Apps("ui.action.category.app", Remix.System.apps_2_line),
    Settings("ui.action.system.settings", Remix.System.settings_2_line),
    Life("ui.action.life", Remix.Map.riding_line),
    Advanced("ui.action.advanced", Remix.System.function_line),

}

enum class TabItems(val i18NKey: String, val icon: String) {
    Recent("Recent", Remix.System.history_line),
    All("All", Remix.Business.cloud_line),
    Remote("ui.action.remote", Remix.Business.cloud_line)
}

val initialPageIndex = TabItems.Recent.ordinal

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
                    TopAppBar(
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

                            HorizontalPager(
                                state = pagerState,
                                pageCount = ActionCategory.values().size,
                            ) { index ->
                                val addAction: (Action) -> Unit = {
                                    state.onSelected(it, state.args)
                                    selectorVM.onActionAdd(it)
                                    state.dismiss()
                                }

                                when (index) {
                                    TabItems.Recent.ordinal -> {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            RecentActions(addAction = addAction)
                                        }
                                    }
                                    TabItems.Remote.ordinal -> {
                                        RemoteActions(addAction = addAction)
                                    }
                                    TabItems.All.ordinal -> {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .verticalScroll(rememberScrollState())
                                        ) {
                                            ActionCategoryTitle(ActionCategory.Ui)
                                            UiActions(addAction = addAction)

                                            ActionCategoryTitle(ActionCategory.Apps)
                                            AppActions(addAction = addAction)

                                            ActionCategoryTitle(ActionCategory.Settings)
                                            SettingsActions(addAction = addAction)

                                            ActionCategoryTitle(ActionCategory.Advanced)
                                            ExtActions(addAction = addAction)

                                            ActionCategoryTitle(ActionCategory.Life)
                                            LifeActions(addAction = addAction)
                                        }
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
private fun ActionCategoryTitle(category: ActionCategory) {
    SmallTitle(text = LocalI18N.current[category.i18NKey])
}