@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.ConditionHandler
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.ActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectors
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


class IfThenElseSelectorDialogDialogState(
    val onSelected: (Action.IfThenElse) -> Unit
) {
    var isShowing by mutableStateOf(false)

    val ifThenElse = mutableStateOf(Action.IfThenElse())

    val ifActionHandler = ActionContentStateHandler()
    val elseActionHandler = ActionContentStateHandler()

    fun show(currentSettings: Action.IfThenElse? = null) {
        this.ifThenElse.value = currentSettings ?: Action.IfThenElse()
        isShowing = true
    }

    private fun updateState(block: Action.IfThenElse.() -> Action.IfThenElse) {
        ifThenElse.value = block.invoke(ifThenElse.value)
    }

    fun addCondition(condition: Condition) {
        updateState {
            copy(ifConditions = ifConditions.toMutableList().apply {
                add(condition)
            })
        }
    }

    fun removeCondition(condition: Condition) {
        updateState {
            copy(ifConditions = ifConditions.toMutableList().apply {
                remove(condition)
            })
        }
    }

    fun setConditionOp(op: Op) {
        if (op != ifThenElse.value.ifConditionOp) {
            updateState {
                copy(
                    ifConditionOp = op
                )
            }
        }
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberIfThenElseSelectorDialogDialogState(
    onSelected: (Action.IfThenElse) -> Unit
): IfThenElseSelectorDialogDialogState {
    return remember {
        IfThenElseSelectorDialogDialogState(onSelected)
    }
}

@Composable
fun IfThenElseSelectorDialogDialog(state: IfThenElseSelectorDialogDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            IfThenElseSelectorDialogSelector(state)
        }
    }
}

@Composable
private fun IfThenElseSelectorDialogSelector(dialogState: IfThenElseSelectorDialogDialogState) {
    val ifActionState by dialogState.ifActionHandler.state.collectAsState()
    val elseActionState by dialogState.elseActionHandler.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.if.then.else"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(
                                    dialogState.ifThenElse.value.copy(
                                        ifActions = ifActionState.actions,
                                        elseActions = elseActionState.actions
                                    )
                                )
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    val conditions = dialogState.ifThenElse.value.ifConditions
                    val conditionOp = dialogState.ifThenElse.value.ifConditionOp
                    ConditionSelectors(
                        header = {
                            MediumTitle(LocalI18N.current["ui.action.if"])
                        },
                        conditions = conditions,
                        conditionOp = conditionOp,
                        handler = ConditionHandler(
                            addCondition = dialogState::addCondition,
                            removeCondition = dialogState::removeCondition,
                            setConditionOp = dialogState::setConditionOp
                        )
                    )
                    Spacer(modifier = Modifier.size(24.dp))


                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        header = {
                            MediumTitle(LocalI18N.current["ui.action.then"])
                        },
                        actions = ifActionState.actions,
                        handler = ActionHandler(
                            addAction = { action ->
                                dialogState.ifActionHandler.addAction(action)
                            },
                            insertAbove = { action, anchor ->
                                dialogState.ifActionHandler.insertActionAbove(action, anchor)
                            },
                            insertBelow = { action, anchor ->
                                dialogState.ifActionHandler.insertActionBelow(action, anchor)
                            },
                            copy = { action ->
                                dialogState.ifActionHandler.copy(action)
                            },
                            pasteAbove = { anchor ->
                                dialogState.ifActionHandler.pasteActionAbove(anchor)
                            },
                            pasteBelow = { anchor ->
                                dialogState.ifActionHandler.pasteActionBelow(anchor)
                            },
                            removeAction = { action ->
                                dialogState.ifActionHandler.removeAction(action)
                            },
                            applySort = { sorted ->
                                dialogState.ifActionHandler.sortActions(sorted)
                            }
                        )
                    )
                    Spacer(modifier = Modifier.size(24.dp))


                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        header = {
                            MediumTitle(LocalI18N.current["ui.action.else"])
                        },
                        actions = elseActionState.actions,
                        handler = ActionHandler(
                            addAction = { action ->
                                dialogState.elseActionHandler.addAction(action)
                            },
                            insertAbove = { action, anchor ->
                                dialogState.elseActionHandler.insertActionAbove(action, anchor)
                            },
                            insertBelow = { action, anchor ->
                                dialogState.elseActionHandler.insertActionBelow(action, anchor)
                            },
                            copy = { action ->
                                dialogState.elseActionHandler.copy(action)
                            },
                            pasteAbove = { anchor ->
                                dialogState.elseActionHandler.pasteActionAbove(anchor)
                            },
                            pasteBelow = { anchor ->
                                dialogState.elseActionHandler.pasteActionBelow(anchor)
                            },
                            removeAction = { action ->
                                dialogState.elseActionHandler.removeAction(action)
                            },
                            applySort = { sorted ->
                                dialogState.elseActionHandler.sortActions(sorted)
                            }
                        )
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}