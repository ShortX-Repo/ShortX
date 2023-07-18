@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.condition.mapper.colorForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.iconForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.iconForOp
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForCondition
import tornaco.apps.shortx.ui.addrule.condition.mapper.opDescription
import tornaco.apps.shortx.ui.addrule.condition.mapper.opTitle
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.condition.selector.ConditionSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.selector.appConditionSelectHandler
import tornaco.apps.shortx.ui.addrule.condition.selector.otherConditionSelectHandler
import tornaco.apps.shortx.ui.addrule.condition.selector.rememberConditionSelectorDialogState
import tornaco.apps.shortx.ui.addrule.condition.selector.systemConditionSelectHandler
import tornaco.apps.shortx.ui.base.ClickableBadge
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.ErrorCard
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
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTipDialogState

class ConditionHandler(
    val addCondition: (Condition) -> Unit,
    val removeCondition: (Condition) -> Unit,
    val setConditionOp: (Op) -> Unit,
)

@Composable
fun ConditionContent(
    header: @Composable ColumnScope.() -> Unit = {
        MediumTitle(LocalI18N.current["ui.conditions.title"])
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
                            appConditionSelectHandler {
                                handler.addCondition(
                                    it.clone(
                                        id = currentCondition.id,
                                        note = currentCondition.note,
                                        isInvert = currentCondition.isInvert
                                    )
                                )
                            },
                            systemConditionSelectHandler {
                                handler.addCondition(
                                    it.clone(
                                        id = currentCondition.id,
                                        note = currentCondition.note,
                                        isInvert = currentCondition.isInvert
                                    )
                                )
                            },
                            otherConditionSelectHandler {
                                handler.addCondition(
                                    it.clone(
                                        id = currentCondition.id,
                                        note = currentCondition.note,
                                        isInvert = currentCondition.isInvert
                                    )
                                )
                            },
                        )

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
                                        handler.addCondition(
                                            currentCondition.clone(
                                                id = currentCondition.id,
                                                note = currentCondition.note,
                                                isInvert = !currentCondition.isInvert
                                            )
                                        )
                                    }
                                )
                        ) {
                            ListItem(
                                titleLines = mutableListOf<String>().apply {
                                    addAll(labelForCondition(condition = conditionItem))
                                    onDebugBuild { add(conditionItem.id) }
                                },
                                icon = {
                                    ConditionIcon(conditionItem)
                                },
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

                                    if (!readOnly) {
                                        IconButton(onClick = {
                                            handler.removeCondition(conditionItem)
                                        }) {
                                            RemixIcon(remixName = Remix.System.close_line)
                                        }
                                    }
                                })

                            // Warning.
                            val actionWarning: List<String>? = warnings[conditionItem.id]
                            AnimatedVisibility(visible = !actionWarning.isNullOrEmpty()) {
                                actionWarning?.let {
                                    ErrorCard(
                                        LocalI18N.current["ui.condition.warning.message"],
                                        it
                                    )
                                }
                            }
                        }
                    }

                    // Add button
                    TinySpacer()
                    ConditionSelector(readOnly) { condition ->
                        handler.addCondition(condition)
                    }

                    // OP button
                    AnimatedVisibility(visible = conditions.size > 1) {
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
    Box {
        val conditionSelectorState =
            rememberConditionSelectorDialogState<String>(onSelected = { condition: Condition, s: String? ->
                selectCondition(condition)
            })
        ConditionSelectorDialog(state = conditionSelectorState)

        if (!readOnly) {
            Button(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    conditionSelectorState.show()
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
fun ConditionIcon(it: Condition) {
    ColoredIcon(
        size = 36.dp,
        color = colorForCondition(it::class).copy(alpha = ActionColorBgAlpha)
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
                    shortXManager.executeMVEL(it)
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