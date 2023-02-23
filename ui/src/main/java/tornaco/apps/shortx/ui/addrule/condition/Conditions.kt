package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
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
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.*
import kotlin.reflect.KClass

@Composable
fun Conditions(
    state: AddRuleState,
    selectCondition: (KClass<out Condition>) -> Unit,
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
                        items = listOf(
                            Condition.CurrentForegroundApp::class,
                            Condition.MVEL::class,
                            Condition.TRUE::class
                        ).map {
                            DropdownItem(
                                labelLines = labelForConditionSelector(it),
                                data = it,
                                icon = { IconForConditionSelector(condition = it) }
                            )
                        }
                    ) {
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
        ListItem(
            modifier = Modifier,
            titleLines = mutableListOf(
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