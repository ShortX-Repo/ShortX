@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import tornaco.apps.shortx.ui.addrule.condition.ConditionContent
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
import tornaco.apps.shortx.ui.addrule.condition.asConditionHandler
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.SwitchItem
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class SwitchCaseCaseSelectorActivity : BaseSelectorActivity<Action.SwitchCase.Case>() {
    @Composable
    override fun BaseSelectorActivity<Action.SwitchCase.Case>.SelectorContent(currentData: Action.SwitchCase.Case?) {
        SwitchCaseCaseSelectorDialogSelector(currentData)
    }
}

class SwitchCaseCaseSelectorViewModel :
    StateViewModel<SwitchCaseCaseSelectorDialogDialogState, Unit>(
        initState = {
            SwitchCaseCaseSelectorDialogDialogState()
        }
    ) {
    private val logger = Logger("SwitchCaseCaseSelectorViewModel")

    val actionHandler: ActionContentStateHandler = ActionContentStateHandler()
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

    fun initFrom(currentSettings: Action.SwitchCase.Case? = null) {
        logger.d("initFrom: ${currentSettings?.id}")
        updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }

        currentSettings?.let {
            updateState {
                copy(
                    description = it.description,
                    id = it.id,
                    isBreak = it.isBreak,
                    isDisabled = it.isDisabled
                )
            }

            conditionHandler.init(
                currentSettings.conditions,
                currentSettings.conditionOp
            )
            actionHandler.init(currentSettings.actions)
        }
    }

    fun setDescription(description: String) {
        updateState {
            copy(description = description)
        }
    }

    fun setIsBreak(isBreak: Boolean) {
        updateState {
            copy(isBreak = isBreak)
        }
    }

    fun setIsDisabled(isDisabled: Boolean) {
        updateState {
            copy(isDisabled = isDisabled)
        }
    }
}

data class SwitchCaseCaseSelectorDialogDialogState(
    val evaluatingActions: List<String> = emptyList(),
    val id: String = Action.SwitchCase.defaultNewCaseId(),
    val description: String = "",
    val isBreak: Boolean = false,
    val isDisabled: Boolean = false,
)

@Composable
private fun BaseSelectorActivity<Action.SwitchCase.Case>.SwitchCaseCaseSelectorDialogSelector(
    currentAction: Action.SwitchCase.Case?
) {
    val vm = viewModel<SwitchCaseCaseSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }
    val dialogState by vm.state.collectAsState()

    val actionContentStateHandler = remember(vm) { vm.actionHandler }
    val actionContentState by actionContentStateHandler.state.collectAsState()

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
                        title = {
                            Text(
                                LocalI18N.current.get(
                                    key = "ui.action.switch.case.case",
                                    fallback = "Case"
                                )
                            )
                        },
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
                                    Action.SwitchCase.Case(
                                        id = dialogState.id,
                                        description = dialogState.description,
                                        isBreak = dialogState.isBreak,
                                        isDisabled = dialogState.isDisabled,
                                        conditionOp = conditionState.conditionOp,
                                        conditions = conditionState.conditions,
                                        actions = actionContentState.actions,
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
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    StandardSpacer()
                    SwitchItem(
                        title = LocalI18N.current["ui.action.switch.case.break"],
                        summary = LocalI18N.current["ui.action.switch.case.break.summary"],
                        isChecked = dialogState.isBreak,
                        onCheckChange = {
                            vm.setIsBreak(it)
                        }
                    )
                    StandardSpacer()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        SmallTitle(text = LocalI18N.current["ui.action.switch.case.description"])
                        TinySpacer()
                        ShortXTextField(
                            value = dialogState.description,
                            onValueChange = { vm.setDescription(it) })
                        LargeSpacer()

                        ConditionContent(
                            isOptional = false,
                            state = conditionState,
                            handler = condHandler.asConditionHandler()
                        )
                        LargeSpacer()

                        tornaco.apps.shortx.ui.addrule.action.ActionContent(
                            actions = actionContentState.actions,
                            evaluatingActions = dialogState.evaluatingActions,
                            handler = actionContentStateHandler.asActionHandler(),
                            warnings = actionContentState.actionWarningMessages
                        )
                        LargeSpacer()

                        SuggestedNavEntry()
                    }
                }
            }
        }
    }
}