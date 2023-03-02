package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.KEY_ACTION
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.main.AppRoute

@Composable
fun ActionSelectors(
    header: @Composable ColumnScope.() -> Unit = {
        MediumTitle(LocalI18N.current["ui.actions.title"])
    },
    addAction: (Action) -> Unit,
    removeAction: (Action) -> Unit,
    actions: List<Action>,
    installedApps: List<App>,
    sortActions: (List<Action>) -> Unit = {},
) {
    val showOverlayButtonSelectorState =
        rememberShowOverlayButtonSelectorState(
            installedApps = installedApps
        ) { settings ->
            val action = Action.ShowOverlay(
                autoCollapse = settings.autoCollapse,
                settings = settings.items.map { it.toOverlayButtonSetting() }
            )
            addAction(action)
        }
    ShowOverlayButtonSelector(state = showOverlayButtonSelectorState)

    Actions(
        header = header,
        actions = actions,
        addAction = addAction,
        removeAction = { action ->
            removeAction(action)
        },
        onActionClick = { action ->
            when (action) {
                is Action.ShowOverlay -> {
                    showOverlayButtonSelectorState.show(
                        ShowOverlayButtonSettings(
                            action.autoCollapse,
                            action.settings.map { it.toOverlayButtonItem() })
                    )
                }
                else -> {}
            }
        },
        sortActions = sortActions
    )
}

@Composable
private fun Actions(
    header: @Composable ColumnScope.() -> Unit,
    actions: List<Action>,
    addAction: (Action) -> Unit,
    removeAction: (Action) -> Unit,
    onActionClick: (Action) -> Unit = {},
    sortActions: (List<Action>) -> Unit = {},
) {
    val actionSortDialogState = rememberActionSortState(actions = actions, apply = {
        sortActions(it)
    })

    val navHost = LocalNavHostController.current
    val result =
        navHost.getStateFlow<Action?>(KEY_ACTION, null)?.collectAsState()
    LaunchedEffect(result) {
        result?.value?.let {
            addAction(it)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                header()
            }

            IconButton(onClick = {
                actionSortDialogState.show()
            }) {
                Icon(imageVector = Icons.Outlined.Sort)
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            actions.forEach {
                ListItem(
                    modifier = Modifier,
                    titleLines = labelLinesForAction(action = it),
                    icon = {
                        Icon(imageVector = imageVectorForAction(action = it::class))
                    },
                    action1 = {
                        IconButton(onClick = {
                            removeAction(it)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close, contentDescription = "Remove"
                            )
                        }
                    },
                    onClick = { onActionClick(it) })
            }

            Box {
                FilledTonalButton(onClick = {
                    navHost.removeData<Action>(KEY_ACTION)
                    navHost.navigate(AppRoute.SelectAction.name)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add, contentDescription = "Select"
                    )
                    TinySpacer()
                    Text(LocalI18N.current["ui.actions.add"])
                }

                ActionSortDialog(state = actionSortDialogState)
            }
        }
    }
}