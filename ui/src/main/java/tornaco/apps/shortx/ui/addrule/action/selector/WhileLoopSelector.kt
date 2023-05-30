@file:OptIn(
    ExperimentalMaterial3Api::class
)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectors
import tornaco.apps.shortx.ui.addrule.condition.asConditionHandler
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

class WhileLoopDialogState(
    val onSelected: (Action.WhileLoop) -> Unit
) {
    var isShowing by mutableStateOf(false)

    val actionHandler = ActionContentStateHandler()
    val conditionHandler = ConditionContentStateHandler()

    var delay by mutableStateOf(0L)
    var repeat by mutableStateOf(0)

    fun show(currentSettings: Action.WhileLoop? = null) {
        conditionHandler.init(
            currentSettings?.conditions ?: emptyList(),
            currentSettings?.op ?: Op.All
        )
        actionHandler.init(currentSettings?.actions ?: emptyList())
        delay = currentSettings?.delay ?: 0L
        repeat = currentSettings?.repeat ?: 0

        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberWhileLoopDialogState(
    onSelected: (Action.WhileLoop) -> Unit
): WhileLoopDialogState {
    return remember {
        WhileLoopDialogState(onSelected)
    }
}

@Composable
fun WhileLoopDialog(state: WhileLoopDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            WhileLoopSelector(state)
        }
    }
}

@Composable
private fun WhileLoopSelector(dialogState: WhileLoopDialogState) {
    val actionState by dialogState.actionHandler.state.collectAsState()
    val conditionState by dialogState.conditionHandler.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.while.loop"]) },
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
                                    Action.WhileLoop(
                                        conditions = conditionState.conditions,
                                        op = conditionState.conditionOp,
                                        actions = actionState.actions,
                                        repeat = dialogState.repeat,
                                        delay = dialogState.delay
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
                    SmallTitle(text = LocalI18N.current["ui.action.while.loop.repeat"])
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dialogState.repeat.toString(),
                        label = {
                            Text(LocalI18N.current["ui.action.while.loop.repeat.message"])
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        showSymbolButton = false,
                        onValueChange = {
                            dialogState.repeat = it.toIntOrNull() ?: 0
                        })
                    StandardSpacer()

                    SmallTitle(text = LocalI18N.current["ui.action.while.loop.delay"])
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dialogState.delay.toString(),
                        label = {
                            Text(LocalI18N.current["ui.action.while.loop.delay.message"])
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        showSymbolButton = false,
                        onValueChange = {
                            dialogState.delay = it.toLongOrNull() ?: 0L
                        })
                    StandardSpacer()

                    val conditions = conditionState.conditions
                    val conditionOp = conditionState.conditionOp
                    val warning = conditionState.conditionWarningMessages
                    ConditionSelectors(
                        header = {
                            SmallTitle(LocalI18N.current["ui.action.while.loop.condition.title"])
                            TipRow(LocalI18N.current["ui.action.while.loop.condition.message"])
                            MediumSpacer()
                        },
                        conditions = conditions,
                        warnings = warning,
                        conditionOp = conditionOp,
                        handler = dialogState.conditionHandler.asConditionHandler()
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        header = {
                            SmallTitle(LocalI18N.current["ui.action.while.loop.action.message"])
                        },
                        actions = actionState.actions,
                        handler = dialogState.actionHandler.asActionHandler(),
                        warnings = actionState.actionWarningMessages
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    SuggestedNavEntry()
                }
            }
        }
    }

}