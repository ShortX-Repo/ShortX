package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
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
    actions: List<Action>,
    handler: ActionHandler
) {
    val uiActionSubSelectorStates = uiActionSubSelectors { handler.addAction(it) }

    val mvelEditorState = rememberCodeEditorState(
        title = LocalI18N.current["ui.title.mvel"],
        onSelected = {
            handler.addAction(Action.MVEL(expression = it.src, note = it.note))
        }
    )
    CodeEditorDialog(state = mvelEditorState)

    val mapNavDialogState = rememberMapNavDialogState {
        handler.addAction(it)
    }
    MapNavDialog(state = mapNavDialogState)

    val ifThenElseDialogState = rememberIfThenElseSelectorDialogDialogState {
        handler.addAction(it)
    }
    IfThenElseSelectorDialogDialog(state = ifThenElseDialogState)

    Actions(
        header = header,
        actions = actions,
        handler = handler,
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
                is Action.IfThenElse -> {
                    ifThenElseDialogState.show(action)
                }
                else -> {}
            }
        })
}

@Composable
private fun Actions(
    header: @Composable ColumnScope.() -> Unit,
    actions: List<Action>,
    handler: ActionHandler,
    onActionClick: (Action) -> Unit = {},
) {
    val actionSortDialogState = rememberActionSortState(
        actions = actions,
        apply = {
            handler.applySort(it)
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

        val actionSelectorState =
            rememberActionSelectorDialogState<ActionSelectorDialogArgs>(onSelected = { action, args ->
                when (args) {
                    ActionSelectorDialogArgs.New -> {
                        handler.addAction(action)
                    }
                    is ActionSelectorDialogArgs.InsertAbove -> {
                        handler.insertAbove(action, args.anchor)

                    }
                    is ActionSelectorDialogArgs.InsertBelow -> {
                        handler.insertBelow(action, args.anchor)
                    }
                    else -> {
                        handler.addAction(action)
                    }
                }
            })
        ActionSelectorDialog(state = actionSelectorState)


        Column(modifier = Modifier.fillMaxWidth()) {
            actions.forEachIndexed { _, action ->
                Box(modifier = Modifier) {
                    val longClickDropdownState = rememberDropdownSelectorState()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ShortXCardRoundedCornerShape)
                            .clickable {
                                onActionClick(action)
                            }
                    ) {
                        ListItem(
                            modifier = Modifier,
                            titleLines = labelLinesForAction(action = action),
                            icon = {
                                ColoredIcon(size = 36.dp) {
                                    RemixIcon(remixName = imageVectorForAction(action = action::class))
                                }
                            },
                            action1 = {
                                if (action.contextData.isNotEmpty()) {
                                    IconButton(onClick = {
                                        contextDataInfoDialogState.show(
                                            labelAndDescriptionForActionSelector(
                                                i18N,
                                                action::class
                                            ).first,
                                            action.contextData
                                        )
                                    }) {
                                        ContextDataIcon()
                                    }
                                    VerticalDivider(modifier = Modifier.height(24.dp))
                                }

                                IconButton(onClick = {
                                    handler.removeAction(action)
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            onClick = { onActionClick(action) },
                            onLongClick = { longClickDropdownState.open() }
                        )

                        if (action.note.isNotBlank()) {
                            NoteRow(
                                modifier = Modifier
                                    .padding(start = 68.dp, end = 16.dp)
                                    .padding(bottom = 16.dp),
                                text = action.note
                            )
                        }
                    }


                    DropdownSelector(
                        state = longClickDropdownState,
                        items = listOf(
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.insert.above"]),
                                data = action to "ia",
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.insert.below"]),
                                data = action to "ib",
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.paste.above"]),
                                data = action to "pa",
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.paste.below"]),
                                data = action to "pb",
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.copy"]),
                                data = action to "cp",
                            )
                        ), onSelect = {
                            when (it.data.second) {
                                "ia" -> {
                                    actionSelectorState.show(ActionSelectorDialogArgs.InsertAbove(it.data.first))
                                }
                                "ib" -> {
                                    actionSelectorState.show(ActionSelectorDialogArgs.InsertBelow(it.data.first))
                                }
                                "pa" -> {
                                    handler.pasteAbove(it.data.first)
                                }
                                "pb" -> {
                                    handler.pasteBelow(it.data.first)
                                }
                                "cp" -> {
                                    handler.copy(action)
                                }
                            }
                        })
                }
            }


            StandardSpacer()
            Box {
                FilledTonalButton(onClick = {
                    actionSelectorState.show(ActionSelectorDialogArgs.New)
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


class ActionHandler(
    val addAction: (Action) -> Unit = {},
    val removeAction: (Action) -> Unit = {},
    val applySort: (List<Action>) -> Unit = {},
    val insertAbove: (action: Action, anchor: Action) -> Unit = { _, _ -> {} },
    val insertBelow: (action: Action, anchor: Action) -> Unit = { _, _ -> {} },
    val pasteAbove: (anchor: Action) -> Unit = { },
    val pasteBelow: (anchor: Action) -> Unit = { },
    val copy: (action: Action) -> Unit = {},
)

sealed interface ActionSelectorDialogArgs {
    object New : ActionSelectorDialogArgs
    data class InsertAbove(val anchor: Action) : ActionSelectorDialogArgs
    data class InsertBelow(val anchor: Action) : ActionSelectorDialogArgs
}