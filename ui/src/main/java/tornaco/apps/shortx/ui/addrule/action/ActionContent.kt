@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.proto.common.ActionOnError
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.res.isBase64Image
import tornaco.apps.shortx.ui.ClipboardDialog
import tornaco.apps.shortx.ui.UiClipboard
import tornaco.apps.shortx.ui.addrule.ContextDataIcon
import tornaco.apps.shortx.ui.addrule.ContextDataInfoDialog
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.mapper.labelLinesForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.ActionSelectSource
import tornaco.apps.shortx.ui.addrule.action.selector.ActionSelectorActivity
import tornaco.apps.shortx.ui.addrule.action.selector.ActionSelectorActivity.Companion.INTENT_EXTRA_ARG1_ACTION
import tornaco.apps.shortx.ui.addrule.action.selector.ActionSelectorActivity.Companion.toActionOrNull
import tornaco.apps.shortx.ui.addrule.action.selector.appActionSelectHandler
import tornaco.apps.shortx.ui.addrule.action.selector.extActionSelectHandler
import tornaco.apps.shortx.ui.addrule.action.selector.funcActionSelectHandler
import tornaco.apps.shortx.ui.addrule.action.selector.lifeActionSelectHandler
import tornaco.apps.shortx.ui.addrule.action.selector.miscActionSelectHandler
import tornaco.apps.shortx.ui.addrule.action.selector.notificationActionSelectHandler
import tornaco.apps.shortx.ui.addrule.action.selector.pluginActionSelectHandler
import tornaco.apps.shortx.ui.addrule.action.selector.systemActionSelectHandler
import tornaco.apps.shortx.ui.addrule.action.selector.uiActionSelectHandler
import tornaco.apps.shortx.ui.addrule.action.selector.varActionSelectHandler
import tornaco.apps.shortx.ui.addrule.base.BatchOpToolbar
import tornaco.apps.shortx.ui.addrule.base.batchOpSelectedItemHighlightColor
import tornaco.apps.shortx.ui.addrule.base.rememberBatchOpState
import tornaco.apps.shortx.ui.addrule.rememberContextDataInfoDialogState
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.ErrorCard
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.NoteRow
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipIds
import tornaco.apps.shortx.ui.base.UserTip
import tornaco.apps.shortx.ui.base.VerticalDivider
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.base.thenIf
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.record.EvaRecordActivity
import tornaco.apps.shortx.ui.rememberClipboardDialogState
import tornaco.apps.shortx.ui.theme.Dimens
import tornaco.apps.shortx.ui.theme.ErrorRed
import kotlin.reflect.KClass

@Composable
fun ActionContent(
    header: @Composable() (ColumnScope.() -> Unit) = {
        MediumTitle(LocalI18N.current["ui.actions.title"])
    },
    // Rule Or DA id.
    srcId: String? = null,
    actions: List<Action>,
    evaluatingActions: List<String>,
    warnings: Map<String, List<String>>,
    readOnly: Boolean = false,
    handler: ActionHandler
) {
    Actions(
        header = header,
        warnings = warnings,
        srcId = srcId,
        actions = actions,
        evaluatingActions = evaluatingActions,
        readOnly = readOnly,
        handler = handler
    )
}

@Composable
private fun Actions(
    header: @Composable ColumnScope.() -> Unit,
    srcId: String?,
    actions: List<Action>,
    evaluatingActions: List<String>,
    warnings: Map<String, List<String>>,
    readOnly: Boolean,
    handler: ActionHandler
) {
    val clipboardDialogState = rememberClipboardDialogState()
    ClipboardDialog(state = clipboardDialogState)

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sort
                AnimatedVisibility(visible = !readOnly && actions.size > 1) {
                    IconButton(onClick = {
                        actionSortDialogState.show()
                    }) {
                        RemixIcon(Remix.Editor.sort_asc)
                    }
                }

                val i18N = LocalI18N.current
                val context = LocalContext.current
                val moreDropdownState = rememberDropdownSelectorState()
                val scope = rememberCoroutineScope()
                DropdownSelector(state = moreDropdownState, items = listOf(
                    DropdownItem(
                        labelLines = listOf(i18N["ui.common.execute.actions"]),
                        data = "exe",
                    ),
                    DropdownItem(
                        labelLines = listOf(i18N["ui.settings.action.record"]),
                        data = "history",
                    )
                ), onSelect = {
                    if (it.data == "exe") {
                        scope.launch { handler.executeAll() }
                    } else if (it.data == "history") {
                        EvaRecordActivity.start(context = context, pinSrcId = srcId)
                    }
                })


                IconButton(onClick = {
                    moreDropdownState.open()
                }) {
                    RemixIcon(Remix.System.more_2_fill)
                }
            }
        }

        UserTip(
            modifier = Modifier,
            tipId = TipIds.actionContent,
            message = LocalI18N.current["ui.actions.content.tip"]
        )

        val contextDataInfoDialogState = rememberContextDataInfoDialogState()
        ContextDataInfoDialog(state = contextDataInfoDialogState)
        val i18N = LocalI18N.current
        val context = LocalContext.current

        val newActionSelectActivityLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                val action = it.toActionOrNull()
                composeLogger.d("newActionSelectActivityLauncher, onResult: $it $action")
                action?.let { a -> handler.addAction(a) }
            }
        )
        val insertAboveActionSelectActivityLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                val action = it.toActionOrNull()
                val anchorId = it.data?.getStringExtra(INTENT_EXTRA_ARG1_ACTION)
                composeLogger.d("insertAboveActionSelectActivityLauncher, onResult: $it $action $anchorId")
                if (action != null && anchorId != null) {
                    handler.insertAbove(action, anchorId)
                }
            }
        )
        val insertBelowActionSelectActivityLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                val action = it.toActionOrNull()
                val anchorId = it.data?.getStringExtra(INTENT_EXTRA_ARG1_ACTION)
                composeLogger.d("insertBelowActionSelectActivityLauncher, onResult: $it $action $anchorId")
                if (action != null && anchorId != null) {
                    handler.insertBelow(action, anchorId)
                }
            }
        )


        val actionContentCardMenuState = rememberDropdownSelectorState()


        Box(modifier = Modifier) {
            ShortXCard(
                modifier = Modifier,
                shape = ShortXCardRoundedCornerShapeXL,
                onClick = {
                    actionContentCardMenuState.open()
                }
            ) {
                val updatedActions by rememberUpdatedState(newValue = actions)
                val batchOpState = rememberBatchOpState(
                    onCopy = { idList ->
                        idList.forEach { handler.copy(it) }
                    },
                    onToggleItems = { idList ->
                        handler.batchEnableDisable(idList)
                    },
                    onDeleteConfirmed = { idList ->
                        idList.forEach { handler.removeAction(it) }
                    },
                    selectAll = { currentSelections ->
                        val isAllSelected =
                            currentSelections.isNotEmpty() && currentSelections.size == updatedActions.size
                        if (isAllSelected) {
                            removeAllItem()
                        } else {
                            addAllItem(updatedActions.map { it.id })
                        }
                    }
                )
                Column {
                    BatchOpToolbar(batchOpState)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                    ) {
                        actions.forEachIndexed { _, action ->
                            val currentAction by rememberUpdatedState(newValue = action)
                            val editAction: (Action) -> Unit = { newAction ->
                                composeLogger.d("addAction, new: ${newAction.id}, old: ${currentAction.id}")
                                handler.addAction(
                                    newAction.clone(
                                        id = currentAction.id,
                                        note = currentAction.note,
                                        isEnabled = currentAction.isEnabled,
                                        actionOnError = currentAction.actionOnError,
                                        contextData = currentAction.contextData
                                    )
                                )
                            }

                            val selectHandlers = listOf(
                                uiActionSelectHandler(addAction = editAction),
                                appActionSelectHandler(addAction = editAction),
                                notificationActionSelectHandler(addAction = editAction),
                                systemActionSelectHandler(addAction = editAction),
                                extActionSelectHandler(addAction = editAction),
                                lifeActionSelectHandler(addAction = editAction),
                                funcActionSelectHandler(addAction = editAction),
                                varActionSelectHandler(addAction = editAction),
                                miscActionSelectHandler(addAction = editAction),
                                pluginActionSelectHandler(addAction = editAction),
                            )

                            val onActionClick = {
                                composeLogger.d("addAction, onActionClick: ${currentAction.id}")
                                selectHandlers.any { it(ActionSelectSource.FromAction(currentAction)) }
                            }

                            val noteDialogState =
                                rememberTextInputState(
                                    title = LocalI18N.current["ui.common.note"],
                                    message = null,
                                    inputValidator = {
                                        InputValidateResult.OK
                                    }
                                ) {
                                    handler.setNote(currentAction.id, it)
                                }
                            TextInputDialog(state = noteDialogState)

                            val onErrorMenuState = rememberMenuDialogState<String>(
                                title = LocalI18N.current["ui.actions.on.error"],
                                menuItems = listOf(
                                    MenuDialogItem(
                                        id = ActionOnError.Break.name,
                                        title = LocalI18N.current[ActionOnError.Break.labelKey()],
                                        iconName = Remix.System.forbid_2_fill
                                    ),
                                    MenuDialogItem(
                                        id = ActionOnError.Continue.name,
                                        title = LocalI18N.current[ActionOnError.Continue.labelKey()],
                                        iconName = Remix.System.checkbox_circle_fill
                                    ),
                                ),
                                onItemSelected = { _, id ->
                                    val policy = ActionOnError.valueOf(id)
                                    composeLogger.d("setOnError: $policy ${currentAction.id}")
                                    handler.setOnError(currentAction.id, policy)
                                }
                            )
                            MenuDialog(state = onErrorMenuState)

                            Box(modifier = Modifier) {
                                val clickDropdownState = rememberDropdownSelectorState()
                                val isSelected = batchOpState.batchSelectedItems.contains(
                                    action.id
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(ShortXCardRoundedCornerShape)
                                        .combinedClickable(
                                            enabled = !readOnly,
                                            onClick = {
                                                if (batchOpState.isEnable) {
                                                    batchOpState.addOrRemoveItem(action.id)
                                                } else {
                                                    onActionClick()
                                                }
                                            },
                                            onLongClick = {
                                                batchOpState.enable()
                                                batchOpState.addOrRemoveItem(action.id)
                                            }
                                        )
                                ) {
                                    ListItem(
                                        modifier = Modifier.thenIf(
                                            Modifier.background(batchOpSelectedItemHighlightColor()),
                                            isSelected
                                        ),
                                        titleLines = mutableListOf<String>().apply {
                                            addAll(labelLinesForAction(action = action))
                                            onDebugBuild { add(action.id) }
                                        },
                                        textDecoration = if (action.isEnabled) TextDecoration.None else TextDecoration.LineThrough,
                                        titleColor = if (action.isEnabled) Color.Unspecified else ErrorRed,
                                        textColor = if (action.isEnabled) Color.Unspecified else ErrorRed,
                                        icon = {
                                            ActionIcon(
                                                action,
                                                evaluatingActions.contains(action.id),
                                            ) {
                                                clickDropdownState.open()
                                            }
                                        },
                                        action1 = {
                                            if (action.contextData.isNotEmpty()
                                            ) {
                                                IconButton(onClick = {
                                                    contextDataInfoDialogState.show(
                                                        objName = labelAndDescriptionForActionSelector(
                                                            i18N,
                                                            action::class
                                                        ).first,
                                                        contextData = currentAction.contextData,
                                                        canSetCustomKey = true,
                                                        setCustomKey = {
                                                            handler.setCustomContextDataKey(
                                                                currentAction.id,
                                                                it
                                                            )
                                                        }
                                                    )
                                                }) {
                                                    ContextDataIcon()
                                                }
                                                VerticalDivider(modifier = Modifier.height(24.dp))
                                            }

                                            if (!readOnly) {
                                                IconButton(onClick = {
                                                    handler.removeAction(action.id)
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
                                            ErrorCard(
                                                modifier = Modifier
                                                    .padding(16.dp),
                                                title = LocalI18N.current["ui.action.warning.message"],
                                                warnings = it
                                            )
                                        }
                                    }
                                }

                                val scope = rememberCoroutineScope()
                                DropdownSelector(
                                    state = clickDropdownState,
                                    items = listOf(
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.common.insert.above"]),
                                            data = currentAction.id to "ia",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.Editor.insert_row_top,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.common.insert.below"]),
                                            data = currentAction.id to "ib",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.Editor.insert_row_bottom,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.common.paste.above"]),
                                            data = currentAction.id to "pa",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.Editor.insert_row_top,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.common.paste.below"]),
                                            data = currentAction.id to "pb",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.Editor.insert_row_bottom,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.common.copy"]),
                                            data = currentAction.id to "cp",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.Document.file_copy_fill,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.common.note"]),
                                            data = currentAction.id to "note",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.Document.sticky_note_line,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.common.sort"]),
                                            data = currentAction.id to "sort",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.Editor.sort_asc,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.common.execute"]),
                                            data = currentAction.id to "execute",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.Media.play_line,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(
                                                LocalI18N.current["ui.actions.on.error"],
                                                LocalI18N.current[action.actionOnError.labelKey()]
                                            ),
                                            data = currentAction.id to "onError",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.System.error_warning_line,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(
                                                LocalI18N.current["ui.common.toggle.enable.disable"],
                                                LocalI18N.current["ui.common.toggle.enable.disable.summary"]
                                            ),
                                            data = currentAction.id to "enableDisable",
                                            icon = {
                                                RemixIcon(
                                                    remixName = Remix.System.toggle_line,
                                                    tint = colorForAction(action::class)
                                                )
                                            }
                                        ),

                                        ),
                                    onSelect = { dropdownItem ->
                                        when (dropdownItem.data.second) {
                                            "ia" -> {
                                                insertAboveActionSelectActivityLauncher.launch(
                                                    ActionSelectorActivity.newIntent(
                                                        context,
                                                        dropdownItem.data.first
                                                    )
                                                )
                                            }

                                            "ib" -> {
                                                insertBelowActionSelectActivityLauncher.launch(
                                                    ActionSelectorActivity.newIntent(
                                                        context,
                                                        dropdownItem.data.first
                                                    )
                                                )
                                            }

                                            "pa" -> {
                                                val clipboardActions = UiClipboard.getActions()
                                                if (clipboardActions.size == 1) {
                                                    handler.insertAbove(
                                                        clipboardActions[0].action,
                                                        dropdownItem.data.first
                                                    )
                                                } else {
                                                    clipboardDialogState.show { contentList ->
                                                        contentList.filterIsInstance<UiClipboard.ClipboardContent.ActionContent>()
                                                            .forEach {
                                                                handler.insertAbove(
                                                                    it.action,
                                                                    dropdownItem.data.first
                                                                )
                                                            }
                                                    }
                                                }
                                            }

                                            "pb" -> {
                                                val clipboardActions = UiClipboard.getActions()
                                                if (clipboardActions.size == 1) {
                                                    handler.insertBelow(
                                                        clipboardActions[0].action,
                                                        dropdownItem.data.first
                                                    )
                                                } else {
                                                    clipboardDialogState.show { contentList ->
                                                        contentList.filterIsInstance<UiClipboard.ClipboardContent.ActionContent>()
                                                            .forEach {
                                                                handler.insertBelow(
                                                                    it.action,
                                                                    dropdownItem.data.first
                                                                )
                                                            }
                                                    }
                                                }
                                            }

                                            "cp" -> {
                                                handler.copy(dropdownItem.data.first)
                                            }

                                            "enableDisable" -> {
                                                handler.enableDisable(dropdownItem.data.first)
                                            }

                                            "onError" -> {
                                                onErrorMenuState.show()
                                            }

                                            "note" -> {
                                                noteDialogState.show(action.note)
                                            }

                                            "sort" -> {
                                                actionSortDialogState.show()
                                            }

                                            "execute" -> {
                                                scope.launch { handler.execute(dropdownItem.data.first) }
                                            }
                                        }
                                    })
                            }
                        }

                        // Add
                        TinySpacer()
                        Box {
                            if (!readOnly) {
                                Button(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = {
                                        newActionSelectActivityLauncher.launch(
                                            ActionSelectorActivity.newIntent(
                                                context
                                            )
                                        )
                                    }) {
                                    RemixIcon(
                                        modifier = Modifier.size(16.dp),
                                        remixName = Remix.System.add_line,
                                        tint = MaterialTheme.colorScheme.onPrimary
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

            DropdownSelector(
                state = actionContentCardMenuState,
                items = listOf(
                    DropdownItem(
                        labelLines = listOf(LocalI18N.current["ui.common.paste"]),
                        data = "paste"
                    )
                ),
                onSelect = {
                    val clipboardActions = UiClipboard.getActions()
                    if (clipboardActions.size == 1) {
                        handler.insertLast(clipboardActions[0].action)
                    } else {
                        clipboardDialogState.show { contentList ->
                            contentList.filterIsInstance<UiClipboard.ClipboardContent.ActionContent>()
                                .forEach {
                                    handler.insertLast(it.action)
                                }
                        }
                    }
                })
        }
    }
}

@Composable
fun ActionIcon(action: Action, isEvaluating: Boolean = false, onClick: (() -> Unit)? = null) {
    Box {
        Box(modifier = Modifier.align(Alignment.Center)) {
            if (isEvaluating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(37.dp),
                    strokeWidth = 2.dp
                )
            }

            val icon = imageVectorForAction(action = action)
            val isImage = icon.isBase64Image()
            ColoredIcon(
                size = Dimens.DAIconSize,
                color = if (isImage) Color.Transparent else colorForAction(action::class).copy(alpha = ActionColorBgAlpha),
                onClick = onClick
            ) {
                RemixIcon(
                    modifier = Modifier.size(if (isImage) Dimens.DAIconSize else 16.dp),
                    remixName = icon,
                    tint = colorForAction(action::class)
                )
            }
        }

        if (action.actionOnError == ActionOnError.Break) {
            RemixIcon(
                modifier = Modifier
                    .size(11.dp)
                    .align(Alignment.TopEnd),
                remixName = Remix.System.forbid_2_fill,
                tint = colorForAction(action::class)
            )
        }
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
    val name: String = "unnamed.",
    val addAction: (Action) -> Unit = {},
    val removeAction: (String) -> Unit = {},
    val applySort: (List<Action>) -> Unit = {},
    val insertAbove: (action: Action, anchorId: String) -> Unit = { _, _ -> run {} },
    val insertBelow: (action: Action, anchorId: String) -> Unit = { _, _ -> run {} },
    val insertLast: (action: Action) -> Unit = {},
    val copy: (actionId: String) -> Unit = {},
    val enableDisable: (actionId: String) -> Unit = {},
    val batchEnableDisable: (actionId: List<String>) -> Unit = {},
    val setOnError: (actionId: String, onError: ActionOnError) -> Unit = { _, _ -> run {} },
    val setNote: (actionId: String, note: String) -> Unit = { _, _ -> run {} },
    val execute: suspend (actionId: String) -> Unit = { },
    val executeAll: suspend () -> Unit = { },
    val setCustomContextDataKey: (actionId: String, StringPair) -> Unit = { _, _ -> run {} },
)