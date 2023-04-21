package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.ConditionHandler
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

    Conditions(
        header = header,
        conditions = conditions,
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
                    handler.addCondition(Condition.TRUE)
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
                    ListItem(titleLines = labelForCondition(condition = it), icon = {
                        ColoredIcon(size = 36.dp) {
                            IconForCondition(it)
                        }
                    }, action1 = {
                        IconButton(onClick = {
                            removeCondition(it)
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
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
            }, icon = {
                IconForOp(conditionOp)
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