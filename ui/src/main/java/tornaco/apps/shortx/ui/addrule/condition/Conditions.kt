@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.ConditionHandler
import tornaco.apps.shortx.ui.addrule.action.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState
import kotlin.reflect.KClass


@Composable
fun ConditionSelectors(
    header: @Composable () -> Unit = {
        MediumTitle(LocalI18N.current["ui.conditions.title"])
    },
    conditions: List<Condition>,
    warnings: Map<String, List<String>>,
    conditionOp: Op,
    handler: ConditionHandler
) {
    // Condition selectors
    val currentPkgSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { appList ->
                handler.addCondition(
                    Condition.CurrentForegroundApp(
                        apps = appList,
                        pkgSets = emptyList()
                    )
                )
            },
            onPkgSetSelected = { pkgSets ->
                handler.addCondition(
                    Condition.CurrentForegroundApp(
                        apps = emptyList(),
                        pkgSets = pkgSets
                    )
                )
            })
    AppPkgSelectorDialog(currentPkgSelectorState)

    val mvelConditionSelectorState =
        rememberCodeEditorState(title = LocalI18N.current["ui.title.mvel"]) { expression ->
            handler.addCondition(Condition.MVEL(expression.src))
        }
    CodeEditorDialog(state = mvelConditionSelectorState)

    val evaluateGlobalVarSelectorDialogState = rememberEvaluateGlobalVarSelectorState {
        handler.addCondition(it)
    }
    EvaluateGlobalVarSelectorDialog(state = evaluateGlobalVarSelectorDialogState)

    Conditions(
        header = header,
        conditions = conditions,
        warnings = warnings,
        conditionOp = conditionOp,
        selectCondition = { con ->
            when (con) {
                Condition.CurrentForegroundApp::class -> {
                    currentPkgSelectorState.show()
                }
                Condition.MVEL::class -> {
                    mvelConditionSelectorState.show()
                }
                Condition.TRUE::class -> {
                    handler.addCondition(Condition.TRUE())
                }
                Condition.EvaluateGlobalVar::class -> {
                    evaluateGlobalVarSelectorDialogState.show()
                }
            }
        },
        removeCondition = { con ->
            handler.removeCondition(con)
        },
        setConditionOp = {
            handler.setConditionOp(it)
        })
}

@Composable
fun Conditions(
    header: @Composable () -> Unit,
    conditions: List<Condition>,
    warnings: Map<String, List<String>>,
    conditionOp: Op,
    selectCondition: (KClass<out Condition>) -> Unit,
    removeCondition: (Condition) -> Unit,
    setConditionOp: (Op) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        header()
        Box {
            val conditionSelectorState = rememberDropdownSelectorState()
            val opSelectorState = rememberDropdownSelectorState()

            Column(modifier = Modifier.fillMaxWidth()) {
                conditions.forEach {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ShortXCardRoundedCornerShape)
                            .combinedClickable(
                                onClick = { },
                                onLongClick = { }
                            )
                    ) {
                        ListItem(
                            titleLines = labelForCondition(condition = it),
                            icon = {
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
                            },
                            action1 = {
                                IconButton(onClick = {
                                    removeCondition(it)
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            })

                        // Warning.
                        val actionWarning: List<String>? = warnings[it.id]
                        AnimatedVisibility(visible = !actionWarning.isNullOrEmpty()) {
                            actionWarning?.let {
                                WarningCard(
                                    LocalI18N.current["ui.condition.warning.message"],
                                    it
                                )
                            }
                        }
                    }
                }

                // Add button
                TinySpacer()
                Box {
                    FilledTonalButton(onClick = {
                        conditionSelectorState.open()
                    }) {
                        RemixIcon(
                            modifier = Modifier.size(16.dp),
                            remixName = Remix.System.add_line
                        )
                        TinySpacer()
                        Text(LocalI18N.current["ui.conditions.add"])
                    }
                    DropdownSelector(
                        state = conditionSelectorState,
                        items = listOf(
                            Condition.CurrentForegroundApp::class,
                            Condition.MVEL::class,
                            Condition.TRUE::class,
                            Condition.EvaluateGlobalVar::class,
                        ).map {
                            DropdownItem(
                                labelLines = labelForConditionSelector(it),
                                data = it,
                                icon = {
                                    RemixIcon(
                                        modifier = Modifier.size(16.dp),
                                        remixName = iconForCondition(condition = it)
                                    )
                                }
                            )
                        }
                    ) {
                        selectCondition(it.data)
                    }
                }


                StandardSpacer()

                // OP button
                AnimatedVisibility(visible = conditions.size > 1) {
                    OpSelector(conditionOp, opSelectorState, setConditionOp)
                }
            }
        }
    }
}

@Composable
private fun OpSelector(
    conditionOp: Op,
    opSelectorState: DropdownSelectorState,
    setConditionOp: (Op) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SmallTitle(LocalI18N.current["ui.condition.op"])
        ListItem(
            modifier = Modifier,
            titleLines = mutableListOf(
                opTitle(op = conditionOp),
            ).apply {
                addAll(opDescription(op = conditionOp))
            },
            icon = {
                ColoredIcon(size = 28.dp) {
                    RemixIcon(modifier = Modifier.size(12.dp), remixName = iconForOp(conditionOp))
                }
            })

        Box {
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

            val mvelSelectorState = rememberCodeEditorState(
                title = LocalI18N.current["ui.title.mvel"],
                message = LocalI18N.current["ui.condition.op.mvel"],
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