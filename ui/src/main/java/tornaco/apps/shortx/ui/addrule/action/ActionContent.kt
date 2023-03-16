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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.base.*

@Composable
fun ActionContent(
    header: @Composable() (ColumnScope.() -> Unit) = {
        MediumTitle(LocalI18N.current["ui.actions.title"])
    },
    addAction: (Action) -> Unit,
    removeAction: (Action) -> Unit,
    actions: List<Action>,
    sortActions: (List<Action>) -> Unit = {},
) {
    val subSelectorStates = uiActionSubSelectors(addAction)

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
                    val currentSettings = ShowOverlayButtonSettings(
                        action.autoCollapse,
                        action.settings.map { it.toOverlayButtonItem() }
                    )
                    subSelectorStates.overlayButtonState.show(currentSettings)
                }
                is Action.ShowAlertDialog -> {
                    subSelectorStates.showDialogSettingsState.show(action)
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

            val actionSelectorState = rememberActionSelectorDialogState(onSelected = {
                addAction(it)
            })
            ActionSelectorDialog(state = actionSelectorState)

            Box {
                FilledTonalButton(onClick = {
                    actionSelectorState.show()
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