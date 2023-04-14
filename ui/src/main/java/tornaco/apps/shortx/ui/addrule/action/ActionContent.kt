package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.tornaco.shortx.android.icon.Remix
import tornaco.apps.shortx.ui.addrule.ContextDataIcon
import tornaco.apps.shortx.ui.addrule.ContextDataInfoDialog
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.contextData
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.addrule.rememberContextDataInfoDialogState
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState

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
    val uiActionSubSelectorStates = uiActionSubSelectors(addAction)

    val mvelEditorState = rememberCodeEditorState(
        title = LocalI18N.current["ui.title.mvel"],
        onSelected = {
            addAction(Action.MVEL(expression = it))
        }
    )
    CodeEditorDialog(state = mvelEditorState)

    val mapNavDialogState = rememberMapNavDialogState {
        addAction(it)
    }
    MapNavDialog(state = mapNavDialogState)

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
                        id = action.id,
                        autoCollapse = action.autoCollapse,
                        items = action.settings.map { it.toOverlayButtonItem() },
                        tag = action.tag
                    )
                    uiActionSubSelectorStates.overlayButtonState.show(currentSettings)
                }
                is Action.ShowAlertDialog -> {
                    uiActionSubSelectorStates.showDialogSettingsState.show(action)
                }
                is Action.ShowDanmu -> {
                    uiActionSubSelectorStates.showDanmuSettingsState.show(action)
                }
                is Action.MVEL -> {
                    mvelEditorState.show(action.expression)
                }
                is Action.MapNav -> {
                    mapNavDialogState.show(action)
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

            AnimatedVisibility(visible = actions.size > 1) {
                IconButton(onClick = {
                    actionSortDialogState.show()
                }) {
                    RemixIcon(Remix.Editor.sort_asc)
                }
            }
        }

        val contextDataInfoDialogState = rememberContextDataInfoDialogState()
        ContextDataInfoDialog(state = contextDataInfoDialogState)
        val i18N = LocalI18N.current

        Column(modifier = Modifier.fillMaxWidth()) {
            actions.forEach {
                ListItem(
                    modifier = Modifier,
                    titleLines = labelLinesForAction(action = it),
                    icon = {
                        ColoredIcon(size = 36.dp) {
                            RemixIcon(remixName = imageVectorForAction(action = it::class))
                        }
                    },
                    action1 = {
                        if (it.contextData.isNotEmpty()) {
                            IconButton(onClick = {
                                contextDataInfoDialogState.show(
                                    labelAndDescriptionForActionSelector(i18N, it::class).first,
                                    it.contextData
                                )
                            }) {
                                ContextDataIcon()
                            }
                            VerticalDivider(modifier = Modifier.height(24.dp))
                        }

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