@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class IfThenElseSelectorActivity : BaseSelectorActivity<Action.IfThenElse>() {
    @Composable
    override fun BaseSelectorActivity<Action.IfThenElse>.SelectorContent(currentAction: Action.IfThenElse?) {
        IfThenElseSelectorDialogSelector(currentAction)
    }

}

class IfThenElseSelectorViewModel : StateViewModel<IfThenElseSelectorDialogDialogState>(
    initState = {
        IfThenElseSelectorDialogDialogState()
    }
) {
    private val logger = Logger("IfThenElseSelectorViewModel")

    val ifActionHandler: ActionContentStateHandler = ActionContentStateHandler()
    val elseActionHandler: ActionContentStateHandler = ActionContentStateHandler()
    val conditionHandler: ConditionContentStateHandler = ConditionContentStateHandler()

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

    fun initFrom(currentSettings: Action.IfThenElse? = null) {
        updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }

        currentSettings?.let {
            updateState {
                copy(id = currentSettings.id, note = currentSettings.note)
            }
            conditionHandler.init(
                currentSettings.ifConditions,
                currentSettings.ifConditionOp
            )
            ifActionHandler.init(currentSettings.ifActions)
            elseActionHandler.init(currentSettings.elseActions)
        }
    }
}

data class IfThenElseSelectorDialogDialogState(
    val id: String = defaultNewActionId(),
    val note: String = "",
    val evaluatingActions: List<String> = emptyList()
)

@Composable
private fun BaseSelectorActivity<Action.IfThenElse>.IfThenElseSelectorDialogSelector(currentAction: Action.IfThenElse?) {
    val vm = viewModel<IfThenElseSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }
    val dialogState by vm.state.collectAsState()

    val ifHandler = remember(vm) { vm.ifActionHandler }
    val ifActionState by ifHandler.state.collectAsState()

    val elseHandler = remember(vm) { vm.elseActionHandler }
    val elseActionState by elseHandler.state.collectAsState()

    val condHandler = remember(vm) { vm.conditionHandler }
    val conditionState by condHandler.state.collectAsState()

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
                                cancelAndFinish()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                setResultAndFinish(
                                    Action.IfThenElse(
                                        id = dialogState.id,
                                        note = dialogState.note,

                                        ifConditionOp = conditionState.conditionOp,
                                        ifConditions = conditionState.conditions,
                                        ifActions = ifActionState.actions,
                                        elseActions = elseActionState.actions
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
                    ConditionContent(
                        header = {
                            MediumTitle(LocalI18N.current["ui.action.if"])
                        },
                        state = conditionState,
                        handler = condHandler.asConditionHandler()
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        header = {
                            MediumTitle(LocalI18N.current["ui.action.then"])
                        },
                        actions = ifActionState.actions,
                        evaluatingActions = dialogState.evaluatingActions,
                        handler = ifHandler.asActionHandler(),
                        warnings = ifActionState.actionWarningMessages
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        header = {
                            MediumTitle(LocalI18N.current["ui.action.else"])
                        },
                        actions = elseActionState.actions,
                        evaluatingActions = dialogState.evaluatingActions,
                        handler = elseHandler.asActionHandler(),
                        warnings = elseActionState.actionWarningMessages
                    )
                    Spacer(modifier = Modifier.size(24.dp))

                    SuggestedNavEntry()
                }
            }
        }
    }
}