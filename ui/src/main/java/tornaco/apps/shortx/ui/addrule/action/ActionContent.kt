@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.ContextDataIcon
import tornaco.apps.shortx.ui.addrule.ContextDataInfoDialog
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.mapper.labelLinesForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.contextData
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.addrule.rememberContextDataInfoDialogState
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.composeLogger
import kotlin.reflect.KClass

@Composable
fun ActionContent(
    header: @Composable() (ColumnScope.() -> Unit) = {
        MediumTitle(LocalI18N.current["ui.actions.title"])
    },
    actions: List<Action>,
    warnings: Map<String, List<String>>,
    readOnly: Boolean = false,
    handler: ActionHandler
) {
    Actions(
        header = header,
        warnings = warnings,
        actions = actions,
        readOnly = readOnly,
        handler = handler
    )
}

@Composable
private fun Actions(
    header: @Composable ColumnScope.() -> Unit,
    actions: List<Action>,
    warnings: Map<String, List<String>>,
    readOnly: Boolean,
    handler: ActionHandler
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

            // Sort
            AnimatedVisibility(visible = !readOnly && actions.size > 1) {
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
                val currentAction by rememberUpdatedState(newValue = action)
                val editAction: (Action) -> Unit = { newAction ->
                    composeLogger.d("addAction, new: ${newAction.id}, old: ${currentAction.id}")
                    handler.addAction(
                        newAction.clone(
                            id = currentAction.id,
                            note = currentAction.note
                        )
                    )
                }

                val selectHandlers = listOf(
                    uiActionSelectHandler(addAction = editAction),
                    appActionSelectHandler(addAction = editAction),
                    systemActionSelectHandler(addAction = editAction),
                    extActionSelectHandler(addAction = editAction),
                    lifeActionSelectHandler(addAction = editAction),
                    miscActionSelectHandler(addAction = editAction),
                )

                val onActionClick = {
                    composeLogger.d("addAction, onActionClick: ${action.id}")
                    for (h in selectHandlers) {
                        if (h(ActionSelectSource.FromAction(action))) break
                    }
                }

                val noteDialogState =
                    rememberTextSelectorState(title = LocalI18N.current["ui.common.note"]) {
                        handler.setNote(action, it)
                    }
                InputTextSelector(state = noteDialogState)

                Box(modifier = Modifier) {
                    val clickDropdownState = rememberDropdownSelectorState()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ShortXCardRoundedCornerShape)
                            .combinedClickable(
                                enabled = !readOnly,
                                onClick = { onActionClick() },
                                onLongClick = { clickDropdownState.open() }
                            )
                    ) {
                        ListItem(
                            modifier = Modifier,
                            titleLines = mutableListOf<String>().apply {
                                addAll(labelLinesForAction(action = action))
                                if (BuildProp.APP_IS_DEBUG) {
                                    add(action.id)
                                }
                            },
                            icon = {
                                ActionIcon(action)
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

                                if (!readOnly) {
                                    IconButton(onClick = {
                                        handler.removeAction(action)
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                }
                            }
                        )

                        if (action.note.isNotBlank()) {
                            NoteRow(
                                modifier = Modifier
                                    .padding(start = 68.dp, end = 16.dp)
                                    .padding(bottom = 16.dp),
                                text = action.note
                            )
                        }

                        // Warning.
                        val actionWarning: List<String>? = warnings[action.id]
                        AnimatedVisibility(visible = !actionWarning.isNullOrEmpty()) {
                            actionWarning?.let {
                                WarningCard(
                                    LocalI18N.current["ui.action.warning.message"],
                                    it
                                )
                            }
                        }
                    }


                    DropdownSelector(
                        state = clickDropdownState,
                        items = listOf(
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.insert.above"]),
                                data = action to "ia",
                                icon = {
                                    RemixIcon(
                                        remixName = Remix.Editor.insert_row_top,
                                        tint = colorForAction(action::class)
                                    )
                                }
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.insert.below"]),
                                data = action to "ib",
                                icon = {
                                    RemixIcon(
                                        remixName = Remix.Editor.insert_row_bottom,
                                        tint = colorForAction(action::class)
                                    )
                                }
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.paste.above"]),
                                data = action to "pa",
                                icon = {
                                    RemixIcon(
                                        remixName = Remix.Editor.insert_row_top,
                                        tint = colorForAction(action::class)
                                    )
                                }
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.paste.below"]),
                                data = action to "pb",
                                icon = {
                                    RemixIcon(
                                        remixName = Remix.Editor.insert_row_bottom,
                                        tint = colorForAction(action::class)
                                    )
                                }
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.copy"]),
                                data = action to "cp",
                                icon = {
                                    RemixIcon(
                                        remixName = Remix.Document.file_copy_fill,
                                        tint = colorForAction(action::class)
                                    )
                                }
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.note"]),
                                data = action to "note",
                                icon = {
                                    RemixIcon(
                                        remixName = Remix.Document.sticky_note_fill,
                                        tint = colorForAction(action::class)
                                    )
                                }
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.sort"]),
                                data = action to "sort",
                                icon = {
                                    RemixIcon(
                                        remixName = Remix.Editor.sort_asc,
                                        tint = colorForAction(action::class)
                                    )
                                }
                            ),
                            DropdownItem(
                                labelLines = listOf(LocalI18N.current["ui.common.execute"]),
                                data = action to "execute",
                                icon = {
                                    RemixIcon(
                                        remixName = Remix.Media.play_line,
                                        tint = colorForAction(action::class)
                                    )
                                }
                            ),

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

                                "note" -> {
                                    noteDialogState.show(action.note)
                                }

                                "sort" -> {
                                    actionSortDialogState.show()
                                }

                                "execute" -> {
                                    handler.execute(action)
                                }
                            }
                        })
                }
            }

            // Add
            TinySpacer()
            Box {
                if (!readOnly) {
                    FilledTonalButton(onClick = {
                        actionSelectorState.show(ActionSelectorDialogArgs.New)
                    }) {
                        RemixIcon(
                            modifier = Modifier.size(16.dp),
                            remixName = Remix.System.add_line
                        )
                        TinySpacer()
                        Text(LocalI18N.current["ui.actions.add"])
                    }
                }

                ActionSortDialog(state = actionSortDialogState)
            }
        }
    }
}

@Composable
fun ActionIcon(action: Action) {
    ColoredIcon(
        size = 36.dp,
        color = colorForAction(action::class).copy(alpha = ActionColorBgAlpha)
    ) {
        RemixIcon(
            modifier = Modifier.size(16.dp),
            remixName = imageVectorForAction(action = action),
            tint = colorForAction(action::class)
        )
    }
}

@Composable
fun ActionIcon(actionClass: KClass<out Action>) {
    ColoredIcon(
        size = 36.dp,
        color = colorForAction(actionClass).copy(alpha = ActionColorBgAlpha)
    ) {
        RemixIcon(
            modifier = Modifier.size(16.dp),
            remixName = imageVectorForAction(action = actionClass),
            tint = colorForAction(actionClass)
        )
    }
}


class ActionHandler(
    val addAction: (Action) -> Unit = {},
    val removeAction: (Action) -> Unit = {},
    val applySort: (List<Action>) -> Unit = {},
    val insertAbove: (action: Action, anchor: Action) -> Unit = { _, _ -> run {} },
    val insertBelow: (action: Action, anchor: Action) -> Unit = { _, _ -> run {} },
    val pasteAbove: (anchor: Action) -> Unit = { },
    val pasteBelow: (anchor: Action) -> Unit = { },
    val copy: (action: Action) -> Unit = {},
    val setNote: (action: Action, note: String) -> Unit = { _, _ -> run {} },
    val execute: (action: Action) -> Unit = { },
)

sealed interface ActionSelectorDialogArgs {
    object New : ActionSelectorDialogArgs
    data class InsertAbove(val anchor: Action) : ActionSelectorDialogArgs
    data class InsertBelow(val anchor: Action) : ActionSelectorDialogArgs
}