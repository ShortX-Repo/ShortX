package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                TextButton(onClick = {
                    conditionSelectorState.open()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add, contentDescription = "Select"
                    )
                    TinySpacer()
                    Text(LocalI18N.current["ui.conditions.add"])
                }

                StandardSpacer()
                FilledTonalButton(onClick = {
                    opSelectorState.open()
                }) {
                    Text(text = state.conditionOp.javaClass.simpleName)
                }
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

            val mvelSelectorState = rememberMVELSelectorState(
                title = LocalI18N.current["ui.title.mvel"],
                message = LocalI18N.current["ui.condition.op.mvel"],
                onSelected = {
                    setConditionOp(Op.MVEL(it))
                })
            MVELSelector(state = mvelSelectorState)
            DropdownSelector(
                state = opSelectorState, items = listOf(
                    DropdownItem(labelLines = listOf("ALL"), data = "All"),
                    DropdownItem(labelLines = listOf("ANY"), data = "Any"),
                    DropdownItem(labelLines = listOf("NONE"), data = "None"),
                    DropdownItem(labelLines = listOf("MVEL"), data = "MVEL"),
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
            listOf(LocalI18N.current["ui.dialog.title.current.app"],
                condition.app.let {
                    "${it.label}${PREFIX_APP_ICON}${it.pkg.pkgName}"
                })
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
