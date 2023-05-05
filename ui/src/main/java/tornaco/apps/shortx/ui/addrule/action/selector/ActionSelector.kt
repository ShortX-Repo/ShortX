@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class ActionCategory(val i18NKey: String, val icon: String) {
    Ui("UI", Remix.Design.layout_2_line),
    Apps("ui.action.category.app", Remix.System.apps_2_line),
    Settings("ui.action.system.settings", Remix.System.settings_2_line),
    Life("ui.action.life", Remix.Map.riding_line),
    Advanced("ui.action.advanced", Remix.System.function_line),
    Remote("ui.action.remote", Remix.Business.cloud_line)
}

val initialPageIndex = ActionCategory.Ui.ordinal

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

                    val items = ActionCategory.values().map { it.i18NKey }
                    val icons = ActionCategory.values().map { it.icon }

                    ShortXScrollableTabRow(
                        modifier = Modifier,
                        edgePadding = 16.dp,
                        selectedTabIndex = pagerState.currentPage
                    ) {
                        items.forEachIndexed { index, item ->
                            Tab(
                                icon = { RemixIcon(remixName = icons[index]) },
                                text = { Text(LocalI18N.current[item]) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }

                    StandardSpacer()

                    HorizontalPager(
                        state = pagerState,
                        pageCount = ActionCategory.values().size,
                    ) { index ->
                        val addAction: (Action) -> Unit = {
                            state.onSelected(it, state.args)
                            state.dismiss()
                        }

                        when (index) {
                            ActionCategory.Ui.ordinal -> {
                                UiActions(addAction = addAction)
                            }
                            ActionCategory.Apps.ordinal -> {
                                AppActions(addAction = addAction)
                            }
                            ActionCategory.Settings.ordinal -> {
                                SettingsActions(addAction = addAction)
                            }
                            ActionCategory.Advanced.ordinal -> {
                                ExtActions(addAction = addAction)
                            }
                            ActionCategory.Life.ordinal -> {
                                LifeActions(addAction = addAction)
                            }
                            ActionCategory.Remote.ordinal -> {
                                RemoteActions(addAction = addAction)
                            }
                            else -> {}
                        }
                    }
                }


            }
        }
    }
}