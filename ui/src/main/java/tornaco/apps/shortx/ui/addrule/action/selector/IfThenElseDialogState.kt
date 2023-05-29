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
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectors
import tornaco.apps.shortx.ui.addrule.condition.asConditionHandler
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


class IfThenElseSelectorDialogDialogState(
    val onSelected: (Action.IfThenElse) -> Unit
) {
    var isShowing by mutableStateOf(false)

    var id: String = defaultNewActionId()
    var note: String = ""

    val ifActionHandler = ActionContentStateHandler()
    val elseActionHandler = ActionContentStateHandler()

    val conditionHandler = ConditionContentStateHandler()

    fun show(currentSettings: Action.IfThenElse? = null) {
        id = currentSettings?.id ?: defaultNewActionId()
        note = currentSettings?.note ?: ""

        conditionHandler.init(
            currentSettings?.ifConditions ?: emptyList(),
            currentSettings?.ifConditionOp ?: Op.All
        )
        ifActionHandler.init(currentSettings?.ifActions ?: emptyList())
        elseActionHandler.init(currentSettings?.elseActions ?: emptyList())
        isShowing = true
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
    val conditionState by dialogState.conditionHandler.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
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
                                    Action.IfThenElse(
                                        id = dialogState.id,
                                        note = dialogState.note,

                                        ifConditionOp = conditionState.conditionOp,
                                        ifConditions = conditionState.conditions,
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
                    val conditions = conditionState.conditions
                    val conditionOp = conditionState.conditionOp
                    val warning = conditionState.conditionWarningMessages
                    ConditionSelectors(
                        header = {
                            MediumTitle(LocalI18N.current["ui.action.if"])
                        },
                        conditions = conditions,
                        warnings = warning,
                        conditionOp = conditionOp,
                        handler = dialogState.conditionHandler.asConditionHandler()
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        header = {
                            MediumTitle(LocalI18N.current["ui.action.then"])
                        },
                        actions = ifActionState.actions,
                        handler = dialogState.ifActionHandler.asActionHandler(),
                        warnings = ifActionState.actionWarningMessages
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        header = {
                            MediumTitle(LocalI18N.current["ui.action.else"])
                        },
                        actions = elseActionState.actions,
                        handler = dialogState.elseActionHandler.asActionHandler(),
                        warnings = elseActionState.actionWarningMessages
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    SuggestedNavEntry()
                }
            }
        }
    }
}