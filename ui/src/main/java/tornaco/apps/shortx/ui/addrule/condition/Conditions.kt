package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.addrule.AddRuleState
import tornaco.apps.shortx.ui.addrule.action.MVELSelector
import tornaco.apps.shortx.ui.addrule.action.rememberMVELSelectorState
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.models.Condition
import tornaco.apps.shortx.ui.base.*

@Composable
fun Conditions(
    state: AddRuleState,
    selectCondition: (Condition) -> Unit,
    removeCondition: (Condition) -> Unit,
    setConditionOp: (Op) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle(LocalI18N.current["ui.conditions.title"])

        Box {
            val conditionSelectorState = rememberDropdownSelectorState()
            val opSelectorState = rememberDropdownSelectorState()

            Column(modifier = Modifier.fillMaxWidth()) {
                state.conditions.forEach {
                    ListItem(titleLines = labelForCondition(condition = it), icon = {
                        IconForCondition(it)
                    }, action1 = {
                        IconButton(onClick = {
                            removeCondition(it)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close, contentDescription = "Remove"
                            )
                        }
                    }, onClick = {})
                }

                // Add button
                Box {
                    FilledTonalButton(onClick = {
                        conditionSelectorState.open()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add, contentDescription = "Select"
                        )
                        TinySpacer()
                        Text(LocalI18N.current["ui.conditions.add"])
                    }
                    DropdownSelector(
                        state = conditionSelectorState,
                        items = state.selectableConditions.map {
                            DropdownItem(labelLines = labelForConditionSelector(condition = it),
                                data = it,
                                icon = {
                                    IconForConditionSelector(condition = it)
                                })
                        }) {
                        selectCondition(it.data)
                    }
                }


                StandardSpacer()

                // OP button
                AnimatedVisibility(visible = state.conditions.size > 1) {
                    OpSelector(state, opSelectorState, setConditionOp)
                }
            }
        }
    }
}

@Composable
private fun OpSelector(
    state: AddRuleState,
    opSelectorState: DropdownSelectorState,
    setConditionOp: (Op) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SmallTitle(LocalI18N.current["ui.condition.op"])
        ListItem(titleLines = mutableListOf(
            opTitle(op = state.conditionOp),
        ).apply {
            addAll(opDescription(op = state.conditionOp))
        }, icon = {
            IconForOp(state.conditionOp)
        })

        Box {
            FilledTonalButton(onClick = {
                opSelectorState.open()
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit, contentDescription = "Edit"
                )
                TinySpacer()
                Text(text = LocalI18N.current["ui.condition.op.edit"])
            }

            val mvelSelectorState = rememberMVELSelectorState(
                title = LocalI18N.current["ui.title.mvel"],
                message = LocalI18N.current["ui.condition.op.mvel"],
                onSelected = {
                    setConditionOp(Op.MVEL(it))
                })
            MVELSelector(state = mvelSelectorState)
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

@Composable
private fun labelForConditionSelector(condition: Condition): List<String> {
    return when (condition) {
        is Condition.CurrentForegroundApp -> {
            listOf(LocalI18N.current["ui.dialog.title.current.app"])
        }
        is Condition.MVEL -> {
            listOf("MVEL expression")
        }
        Condition.TRUE -> {
            listOf(
                LocalI18N.current["ui.conditions.true"]
            )
        }
    }
}

@Composable
private fun labelForCondition(condition: Condition): List<String> {
    return when (condition) {
        is Condition.CurrentForegroundApp -> {
            mutableListOf(LocalI18N.current["ui.dialog.title.current.app"]).apply {
                addAll(condition.apps.map {
                    "${it.label}${PREFIX_APP_ICON}${it.pkg.pkgName}"
                })
            }
        }
        is Condition.MVEL -> {
            listOf("MVEL expression", condition.expression)
        }
        Condition.TRUE -> {
            listOf(
                LocalI18N.current["ui.conditions.true"],
                LocalI18N.current["ui.conditions.true.desc"]
            )
        }
    }
}

@Composable
private fun IconForCondition(condition: Condition) {
    when (condition) {
        is Condition.CurrentForegroundApp -> {
            Icon(
                imageVector = Icons.Filled.Android, contentDescription = "CurrentForegroundApp"
            )
        }
        is Condition.MVEL -> {
            Icon(
                imageVector = Icons.Filled.Code, contentDescription = "MVEL"
            )
        }
        Condition.TRUE -> {
            Icon(
                imageVector = Icons.Filled.CheckCircle, contentDescription = "True"
            )
        }
    }
}


@Composable
private fun IconForConditionSelector(condition: Condition) {
    when (condition) {
        is Condition.CurrentForegroundApp -> {
            Icon(
                imageVector = Icons.Filled.Android, contentDescription = "CurrentForegroundApp"
            )
        }
        is Condition.MVEL -> {
            Icon(
                imageVector = Icons.Filled.Code, contentDescription = "MVEL"
            )
        }
        Condition.TRUE -> {
            Icon(
                imageVector = Icons.Filled.CheckCircle, contentDescription = "True"
            )
        }
    }
}

@Composable
private fun opTitle(op: Op): String {
    return when (op) {
        Op.All -> {
            "ALL"
        }
        Op.Any -> {
            "ANY"
        }

        Op.None -> {
            "NONE"
        }
        is Op.MVEL -> {
            "MVEL"
        }
    }
}


@Composable
private fun opDescription(op: Op): List<String> {
    return when (op) {
        Op.All -> {
            listOf(LocalI18N.current["ui.condition.op.all.desc"])
        }
        Op.Any -> {
            listOf(LocalI18N.current["ui.condition.op.any.desc"])
        }

        Op.None -> {
            listOf(LocalI18N.current["ui.condition.op.none.desc"])
        }

        is Op.MVEL -> {
            listOf(LocalI18N.current["ui.condition.op.mvel.desc"], op.expression)
        }
    }
}

@Composable
private fun IconForOp(op: Op) {
    when (op) {
        Op.All -> {
            Icon(
                imageVector = Icons.Filled.SelectAll, contentDescription = "All"
            )
        }
        Op.Any -> {
            Icon(
                imageVector = Icons.Filled.Analytics, contentDescription = "Any"
            )
        }

        Op.None -> {
            Icon(
                imageVector = Icons.Filled.InsertInvitation, contentDescription = "None"
            )
        }

        is Op.MVEL -> {
            Icon(
                imageVector = Icons.Filled.Code, contentDescription = "MVEL"
            )
        }
    }
}