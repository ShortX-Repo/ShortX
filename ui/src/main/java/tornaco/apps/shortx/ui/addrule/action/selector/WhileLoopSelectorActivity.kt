@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.condition.ConditionContent
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
import tornaco.apps.shortx.ui.addrule.condition.asConditionHandler
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry

@AndroidEntryPoint
class WhileLoopSelectorActivity : BaseSelectorActivity<Action.WhileLoop>() {
    @Composable
    override fun BaseSelectorActivity<Action.WhileLoop>.SelectorContent(currentData: Action.WhileLoop?) {
        WhileLoopSelectorDialogSelector(currentData)
    }
}

class WhileLoopSelectorViewModel : StateViewModel<WhileLoopSelectorDialogDialogState, Unit>(
    initState = {
        WhileLoopSelectorDialogDialogState()
    }
) {
    private val logger = Logger("WhileLoopSelectorViewModel")

    val actionHandler = ActionContentStateHandler()
    val conditionHandler = ConditionContentStateHandler()

    private val actionEvaluateListener = object : IActionEvaluateListener.Stub() {
        override fun onStart(id: String) {
            logger.d("actionEvaluateListener#onStart: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }

        override fun onComplete(id: String) {
            logger.d("actionEvaluateListener#onComplete: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }
    }

    init {
        shortXManager.registerActionEvaluateListener(actionEvaluateListener)
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterActionEvaluateListener(actionEvaluateListener)
    }

    fun initFrom(currentSettings: Action.WhileLoop? = null) {
        updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }

        currentSettings?.let {
            updateState {
                copy(
                    id = currentSettings.id,
                    note = currentSettings.note,
                    delay = currentSettings.delay,
                    repeat = currentSettings.repeat
                )
            }
            conditionHandler.init(
                currentSettings.conditions,
                currentSettings.op
            )
            actionHandler.init(
                actions = currentSettings.actions,
                actionAsyncMode = currentSettings.actionAsyncMode
            )
        }
    }

    fun setDelay(delay: Long) {
        updateState {
            copy(delay = delay)
        }
    }

    fun setRepeat(repeat: Int) {
        updateState {
            copy(repeat = repeat)
        }
    }
}

data class WhileLoopSelectorDialogDialogState(
    val id: String = defaultNewActionId(),
    val note: String = "",
    val evaluatingActions: List<String> = emptyList(),
    val delay: Long = 0L,
    val repeat: Int = 0
)

@Composable
private fun BaseSelectorActivity<Action.WhileLoop>.WhileLoopSelectorDialogSelector(currentAction: Action.WhileLoop?) {
    val vm = viewModel<WhileLoopSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }
    val dialogState by vm.state.collectAsState()

    val actionHandler = remember(vm) { vm.actionHandler }
    val actionState by actionHandler.state.collectAsState()

    val condHandler = remember(vm) { vm.conditionHandler }
    val conditionState by condHandler.state.collectAsState()

    Scaffold(modifier = Modifier,
        topBar = {
            ShortXTopAppBar(
                modifier = Modifier,
                title = { Text(LocalI18N.current["ui.action.while.loop"]) },
                navigationIcon = {
                    IconButton(onClick = {
                        cancelAndFinish()
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        setResultAndFinish(
                            Action.WhileLoop(
                                conditions = conditionState.conditions,
                                op = conditionState.conditionOp,
                                actions = actionState.actions,
                                repeat = dialogState.repeat,
                                delay = dialogState.delay,
                                actionAsyncMode = actionState.asyncMode
                            )
                        )
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
            SmallTitle(
                modifier = Modifier.padding(vertical = 12.dp),
                text = LocalI18N.current["ui.action.while.loop.repeat"]
            )
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dialogState.repeat.toString(),
                label = {
                    Text(LocalI18N.current["ui.action.while.loop.repeat.message"])
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                showSymbolButton = false,
                onValueChange = {
                    vm.setRepeat(it.toIntOrNull() ?: 0)
                })
            StandardSpacer()

            SmallTitle(
                modifier = Modifier.padding(vertical = 12.dp),
                text = LocalI18N.current["ui.action.while.loop.delay"]
            )
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dialogState.delay.toString(),
                label = {
                    Text(LocalI18N.current["ui.action.while.loop.delay.message"])
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                showSymbolButton = false,
                onValueChange = {
                    vm.setDelay(it.toLongOrNull() ?: 0L)
                })
            StandardSpacer()

            ConditionContent(
                header = {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current["ui.action.while.loop.condition.title"]
                    )
                    TipRow(text = LocalI18N.current["ui.action.while.loop.condition.message"])
                    MediumSpacer()
                },
                state = conditionState,
                handler = condHandler.asConditionHandler()
            )
            Spacer(modifier = Modifier.size(24.dp))

            tornaco.apps.shortx.ui.addrule.action.ActionContent(
                header = {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current["ui.action.while.loop.action.message"]
                    )
                },
                actions = actionState.actions,
                evaluatingActions = dialogState.evaluatingActions,
                handler = actionHandler.asActionHandler(),
                warnings = actionState.actionWarningMessages,
                canSelectActionAsyncMode = true,
                actionAsyncMode = actionState.asyncMode
            )
            Spacer(modifier = Modifier.size(24.dp))

            SuggestedNavEntry()
        }
    }
}