@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.ClipboardDialog
import tornaco.apps.shortx.ui.UiClipboard
import tornaco.apps.shortx.ui.addrule.ContextDataIcon
import tornaco.apps.shortx.ui.addrule.ContextDataInfoDialog
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.condition.mapper.colorForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.iconForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.iconForOp
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.mapper.opDescription
import tornaco.apps.shortx.ui.addrule.condition.mapper.opTitle
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.contextData
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.condition.selector.ConditionSelectorActivity
import tornaco.apps.shortx.ui.addrule.condition.selector.ConditionSelectorActivity.Companion.toConditionOrNull
import tornaco.apps.shortx.ui.addrule.condition.selector.appConditionSelectHandler
import tornaco.apps.shortx.ui.addrule.condition.selector.otherConditionSelectHandler
import tornaco.apps.shortx.ui.addrule.condition.selector.systemConditionSelectHandler
import tornaco.apps.shortx.ui.addrule.condition.selector.uiConditionSelectHandler
import tornaco.apps.shortx.ui.addrule.rememberContextDataInfoDialogState
import tornaco.apps.shortx.ui.base.ClickableBadge
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.ErrorCard
import tornaco.apps.shortx.ui.base.LargeTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.VerticalDivider
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTipDialogState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.rememberClipboardDialogState
import tornaco.apps.shortx.ui.theme.ErrorRed

class ConditionHandler(
    val addCondition: (Condition) -> Unit,
    val removeCondition: (Condition) -> Unit,
    val setConditionOp: (Op) -> Unit,
    val insertAbove: (condition: Condition, anchorId: String) -> Unit = { _, _ -> run {} },
    val insertBelow: (condition: Condition, anchorId: String) -> Unit = { _, _ -> run {} },
    val copy: (conditionId: String) -> Unit = {},
    val enableDisable: (conditionId: String) -> Unit = {},
    val invert: (conditionId: String) -> Unit = {},
)

@Composable
fun ConditionContent(
    isOptional: Boolean = true,
    header: @Composable ColumnScope.() -> Unit = {
        LargeTitle(
            LocalI18N.current[isOptional.select(
                "ui.conditions.title.optional",
                "ui.conditions.title"
            )]
        )
    },
    state: ConditionContentState,
    readOnly: Boolean = false,
    handler: ConditionHandler
) {
    val conditions = state.conditions
    val conditionOp = state.conditionOp
    val warnings = state.conditionWarningMessages

    ConditionList(
        readOnly = readOnly,
        header = header,
        conditions = conditions,
        warnings = warnings,
        conditionOp = conditionOp,
        handler = handler,
    )
}

@Composable
fun ConditionList(
    readOnly: Boolean,
    header: @Composable ColumnScope.() -> Unit,
    conditions: List<Condition>,
    warnings: Map<String, List<String>>,
    handler: ConditionHandler,
    conditionOp: Op,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        header()

        ShortXCard(
            modifier = Modifier,
            shape = ShortXCardRoundedCornerShapeXL,
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                ) {
                    conditions.forEach { conditionItem ->
                        val currentCondition by rememberUpdatedState(newValue = conditionItem)
                        val allHandlers = listOf(
                            uiConditionSelectHandler {
                                handler.addCondition(
                                    it.clone(
                                        id = currentCondition.id,
                                        note = currentCondition.note,
                                        isInvert = currentCondition.isInvert,
                                        isDisabled = currentCondition.isDisabled,
                                        customContextDataKey = currentCondition.customContextDataKey
                                    )
                                )
                            },
                            appConditionSelectHandler {
                                handler.addCondition(
                                    it.clone(
                                        id = currentCondition.id,
                                        note = currentCondition.note,
                                        isInvert = currentCondition.isInvert,
                                        isDisabled = currentCondition.isDisabled,
                                        customContextDataKey = currentCondition.customContextDataKey
                                    )
                                )
                            },
                            systemConditionSelectHandler {
                                handler.addCondition(
                                    it.clone(
                                        id = currentCondition.id,
                                        note = currentCondition.note,
                                        isInvert = currentCondition.isInvert,
                                        isDisabled = currentCondition.isDisabled,
                                        customContextDataKey = currentCondition.customContextDataKey
                                    )
                                )
                            },
                            otherConditionSelectHandler {
                                handler.addCondition(
                                    it.clone(
                                        id = currentCondition.id,
                                        note = currentCondition.note,
                                        isInvert = currentCondition.isInvert,
                                        isDisabled = currentCondition.isDisabled,
                                        customContextDataKey = currentCondition.customContextDataKey
                                    )
                                )
                            },
                        )

                        Box(modifier = Modifier) {
                            val clickDropdownState = rememberDropdownSelectorState()

                            val contextDataInfoDialogState = rememberContextDataInfoDialogState()
                            ContextDataInfoDialog(state = contextDataInfoDialogState)
                            val i18N = LocalI18N.current
                            val context = LocalContext.current

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(ShortXCardRoundedCornerShape)
                                    .combinedClickable(
                                        onClick = {
                                            allHandlers.any {
                                                it(
                                                    ConditionSelectSource.FromCondition(
                                                        currentCondition
                                                    )
                                                )
                                            }
                                        },
                                        onLongClick = {
                                            clickDropdownState.open()
                                        }
                                    )
                            ) {

                                ListItem(
                                    titleLines = mutableListOf<String>().apply {
                                        addAll(labelForCondition(condition = conditionItem))
                                        if (BuildPropInternal.APP_IS_DEBUG) {
                                            add(conditionItem.id)
                                        }
                                    },
                                    icon = {
                                        ConditionIcon(conditionItem) {
                                            clickDropdownState.open()
                                        }
                                    },
                                    textDecoration = if (conditionItem.isDisabled.not()) TextDecoration.None else TextDecoration.LineThrough,
                                    titleColor = if (conditionItem.isDisabled.not()) Color.Unspecified else ErrorRed,
                                    textColor = if (conditionItem.isDisabled.not()) Color.Unspecified else ErrorRed,
                                    action1 = {
                                        val invertDialog = rememberTipDialogState(
                                            title = LocalI18N.current["ui.condition.invert"],
                                            tip = LocalI18N.current["ui.condition.invert.description"]
                                        )
                                        TipDialog(state = invertDialog)
                                        AnimatedVisibility(visible = conditionItem.isInvert) {
                                            ClickableBadge(text = LocalI18N.current["ui.condition.invert"]) {
                                                invertDialog.show()
                                            }
                                        }

                                        if (conditionItem.contextData.isNotEmpty()) {
                                            IconButton(onClick = {
                                                contextDataInfoDialogState.show(
                                                    labelForConditionSelector(
                                                        i18N,
                                                        conditionItem::class
                                                    )[0],
                                                    conditionItem.contextData
                                                )
                                            }) {
                                                ContextDataIcon()
                                            }
                                            VerticalDivider(modifier = Modifier.height(24.dp))
                                        }

                                        if (!readOnly) {
                                            IconButton(onClick = {
                                                handler.removeCondition(conditionItem)
                                            }) {
                                                RemixIcon(remixName = Remix.System.close_line)
                                            }
                                        }
                                    })
                                // Warning.
                                val conditionWarning: List<String>? = warnings[conditionItem.id]
                                AnimatedVisibility(visible = !conditionWarning.isNullOrEmpty()) {
                                    conditionWarning?.let {
                                        ErrorCard(
                                            modifier = Modifier
                                                .padding(16.dp),
                                            title = LocalI18N.current["ui.condition.warning.message"],
                                            warnings = it
                                        )
                                    }
                                }
                            }


                            val clipboardDialogState = rememberClipboardDialogState()
                            ClipboardDialog(state = clipboardDialogState)

                            DropdownSelector(
                                state = clickDropdownState,
                                items = listOf(
                                    DropdownItem(
                                        labelLines = listOf(LocalI18N.current["ui.common.paste.above"]),
                                        data = currentCondition.id to "pa",
                                        icon = {
                                            RemixIcon(
                                                remixName = Remix.Editor.insert_row_top,
                                                tint = colorForCondition(conditionItem::class)
                                            )
                                        }
                                    ),
                                    DropdownItem(
                                        labelLines = listOf(LocalI18N.current["ui.common.paste.below"]),
                                        data = currentCondition.id to "pb",
                                        icon = {
                                            RemixIcon(
                                                remixName = Remix.Editor.insert_row_bottom,
                                                tint = colorForCondition(conditionItem::class)
                                            )
                                        }
                                    ),
                                    DropdownItem(
                                        labelLines = listOf(LocalI18N.current["ui.common.copy"]),
                                        data = currentCondition.id to "cp",
                                        icon = {
                                            RemixIcon(
                                                remixName = Remix.Document.file_copy_fill,
                                                tint = colorForCondition(conditionItem::class)
                                            )
                                        }
                                    ),
                                    DropdownItem(
                                        labelLines = listOf(LocalI18N.current["ui.condition.invert"]),
                                        data = currentCondition.id to "invert",
                                        icon = {
                                            RemixIcon(
                                                remixName = Remix.Arrows.arrow_go_back_line,
                                                tint = colorForCondition(conditionItem::class)
                                            )
                                        }
                                    ),
                                    DropdownItem(
                                        labelLines = listOf(
                                            LocalI18N.current["ui.common.toggle.enable.disable"],
                                            LocalI18N.current["ui.common.toggle.enable.disable.summary"]
                                        ),
                                        data = currentCondition.id to "enableDisable",
                                        icon = {
                                            RemixIcon(
                                                remixName = Remix.System.toggle_line,
                                                tint = colorForCondition(conditionItem::class)
                                            )
                                        }
                                    ),
                                ),
                                onSelect = { dropdownItem: DropdownItem<Pair<String, String>> ->
                                    when (dropdownItem.data.second) {
                                        "pa" -> {
                                            val conditionContents = UiClipboard.getConditions()
                                            if (conditionContents.size == 1) {
                                                handler.insertAbove(
                                                    conditionContents[0].condition,
                                                    dropdownItem.data.first
                                                )
                                            } else {
                                                clipboardDialogState.show { contentList ->
                                                    contentList.filterIsInstance<UiClipboard.ClipboardContent.ConditionContent>()
                                                        .forEach {
                                                            handler.insertAbove(
                                                                it.condition,
                                                                dropdownItem.data.first
                                                            )
                                                        }
                                                }
                                            }
                                        }

                                        "pb" -> {
                                            val conditionContents = UiClipboard.getConditions()
                                            if (conditionContents.size == 1) {
                                                handler.insertBelow(
                                                    conditionContents[0].condition,
                                                    dropdownItem.data.first
                                                )
                                            } else {
                                                clipboardDialogState.show { contentList ->
                                                    contentList.filterIsInstance<UiClipboard.ClipboardContent.ConditionContent>()
                                                        .forEach {
                                                            handler.insertBelow(
                                                                it.condition,
                                                                dropdownItem.data.first
                                                            )
                                                        }
                                                }
                                            }
                                        }

                                        "cp" -> {
                                            handler.copy(dropdownItem.data.first)
                                        }

                                        "invert" -> {
                                            handler.invert(dropdownItem.data.first)
                                        }

                                        "enableDisable" -> {
                                            handler.enableDisable(dropdownItem.data.first)
                                        }
                                    }
                                })
                        }


                    }

                    // Add button
                    TinySpacer()
                    ConditionSelector(readOnly) { condition ->
                        handler.addCondition(condition)
                    }

                    // OP button
                    AnimatedVisibility(visible = conditions.size > 1 || conditionOp != Op.All) {
                        Column {
                            StandardSpacer()
                            OpSelector(
                                conditionOp = conditionOp,
                                readOnly = readOnly,
                                setConditionOp = {
                                    handler.setConditionOp(it)
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun ConditionSelector(
    readOnly: Boolean,
    selectCondition: (Condition) -> Unit
) {
    val context = LocalContext.current
    Box {
        val conditionSelectActivityLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                val condition = it.toConditionOrNull()
                composeLogger.d("conditionSelectActivityLauncher, onResult: $it $condition")
                condition?.let { it1 -> selectCondition(it1) }
            }
        )

        if (!readOnly) {
            Button(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    conditionSelectActivityLauncher.launch(
                        ConditionSelectorActivity.newIntent(context)
                    )
                }) {
                RemixIcon(
                    modifier = Modifier.size(16.dp),
                    remixName = Remix.System.add_line,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                TinySpacer()
                Text(LocalI18N.current["ui.conditions.add"])
            }
        }
    }
}

@Composable
fun ConditionIcon(it: Condition, onClick: (() -> Unit)? = null) {
    ColoredIcon(
        size = 36.dp,
        color = colorForCondition(it::class).copy(alpha = ActionColorBgAlpha),
        onClick = onClick
    ) {
        RemixIcon(
            modifier = Modifier.size(16.dp),
            remixName = iconForCondition(it::class),
            tint = colorForCondition(it::class)
        )
    }
}

@Composable
private fun OpSelector(
    conditionOp: Op,
    readOnly: Boolean,
    setConditionOp: (Op) -> Unit
) {
    val opSelectorState = rememberDropdownSelectorState()
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            LocalI18N.current["ui.condition.op"]
        )
        ListItem(
            modifier = Modifier,
            titleLines = mutableListOf(
                opTitle(op = conditionOp),
            ).apply {
                addAll(opDescription(op = conditionOp, i18N = LocalI18N.current))
            },
            icon = {
                ColoredIcon(size = 28.dp) {
                    RemixIcon(modifier = Modifier.size(12.dp), remixName = iconForOp(conditionOp))
                }
            })

        Box {
            if (!readOnly) {
                FilledTonalButton(onClick = {
                    opSelectorState.open()
                }) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = Remix.Design.edit_circle_line
                    )
                    TinySpacer()
                    Text(text = LocalI18N.current["ui.condition.op.edit"])
                }
            }

            val mvelSelectorState = rememberCodeEditorState(
                title = LocalI18N.current["ui.title.mvel"],
                message = LocalI18N.current["ui.condition.op.mvel"],
                execute = {
                    shortXManager.executeMVEL(it, CoroutineContext.CoroutineContext_Default)
                },
                onSelected = {
                    setConditionOp(Op.MVEL(it.src))
                })
            CodeEditorDialog(state = mvelSelectorState)
            DropdownSelector(
                state = opSelectorState, items = listOf(
                    DropdownItem(
                        labelLines = listOf(
                            opTitle(op = Op.All),
                            opDescription(op = Op.All).first()
                        ), data = "All"
                    ),
                    DropdownItem(
                        labelLines = listOf(
                            opTitle(op = Op.Any),
                            opDescription(op = Op.Any).first()
                        ), data = "Any"
                    ),
                    DropdownItem(
                        labelLines = listOf(
                            opTitle(op = Op.None),
                            opDescription(op = Op.None).first()
                        ), data = "None"
                    ),
                    DropdownItem(
                        labelLines = listOf(
                            opTitle(op = Op.MVEL("")),
                            opDescription(op = Op.MVEL("")).first()
                        ), data = "MVEL"
                    ),
                )
            ) {
                when (it.data) {
                    "All" -> {
                        setConditionOp(Op.All)
                    }

                    "Any" -> {
                        setConditionOp(Op.Any)
                    }

                    "None" -> {
                        setConditionOp(Op.None)
                    }

                    "MVEL" -> {
                        mvelSelectorState.show()
                    }

                    else -> {
                        setConditionOp(Op.All)
                    }
                }
            }
        }
    }
}