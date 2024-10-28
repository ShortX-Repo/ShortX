@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.condition.ConditionContent
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
import tornaco.apps.shortx.ui.addrule.condition.asConditionHandler
import tornaco.apps.shortx.ui.addrule.fact.FactContentStateHandler
import tornaco.apps.shortx.ui.addrule.fact.FactSelectors
import tornaco.apps.shortx.ui.addrule.fact.asFactHandler
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.MediumTitleWithTip
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class WaitUtilConditionMatchSelectorActivity :
    BaseSelectorActivity<Action.WaitUtilConditionMatch>() {
    @Composable
    override fun BaseSelectorActivity<Action.WaitUtilConditionMatch>.SelectorContent(currentData: Action.WaitUtilConditionMatch?) {
        WaitUtilConditionMatchSelectorDialogSelector(currentData)
    }
}

class WaitUtilConditionMatchSelectorViewModel :
    StateViewModel<WaitUtilConditionMatchSelectorDialogDialogState, Unit>(
        initState = {
            WaitUtilConditionMatchSelectorDialogDialogState()
        }
    ) {
    private val logger = Logger("WaitUtilConditionMatchSelectorViewModel")

    val conditionHandler: ConditionContentStateHandler = ConditionContentStateHandler()

    val quitFactContentStateHandler = FactContentStateHandler()
    val quitConditionHandler: ConditionContentStateHandler = ConditionContentStateHandler()

    fun initFrom(currentSettings: Action.WaitUtilConditionMatch? = null) {
        currentSettings?.let {
            updateState {
                copy(
                    id = currentSettings.id,
                    note = currentSettings.note,
                    timeoutStr = currentSettings.timeoutStr.takeIf { it.isNotEmpty() }
                        ?: currentSettings.timeout.toString(),
                    isQuitEnabled = currentSettings.isQuitEnabled
                )
            }
            conditionHandler.init(
                currentSettings.conditions,
                currentSettings.op
            )
            quitFactContentStateHandler.init(currentSettings.quitFacts)
            quitConditionHandler.init(
                currentSettings.quitConditions,
                currentSettings.quitOp
            )
        }
    }

    fun toggleQuitEnable() {
        updateState { copy(isQuitEnabled = !isQuitEnabled) }
    }

    fun setTimeout(l: String) {
        updateState {
            copy(timeoutStr = l)
        }
    }
}

data class WaitUtilConditionMatchSelectorDialogDialogState(
    val id: String = defaultNewActionId(),
    val note: String = "",
    val timeoutStr: String = "5000",
    val isQuitEnabled: Boolean = false
)

@Composable
private fun BaseSelectorActivity<Action.WaitUtilConditionMatch>.WaitUtilConditionMatchSelectorDialogSelector(
    currentAction: Action.WaitUtilConditionMatch?
) {
    val vm = viewModel<WaitUtilConditionMatchSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }
    val vmState by vm.state.collectAsState()

    val condHandler = remember(vm) { vm.conditionHandler }
    val conditionState by condHandler.state.collectAsState()

    val quitCondHandler = remember(vm) { vm.quitConditionHandler }
    val quitConditionState by quitCondHandler.state.collectAsState()

    val quitFactHandler = remember(vm) { vm.quitFactContentStateHandler }
    val quitFactContentState by quitFactHandler.state.collectAsState()

    ShortXTheme {

        Scaffold(modifier = Modifier,
            topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(LocalI18N.current["ui.action.wait.util.condition.match"]) },
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
                                Action.WaitUtilConditionMatch(
                                    id = vmState.id,
                                    note = vmState.note,
                                    op = conditionState.conditionOp,
                                    conditions = conditionState.conditions,

                                    quitFacts = quitFactContentState.facts,
                                    quitOp = quitConditionState.conditionOp,
                                    quitConditions = quitConditionState.conditions,
                                    isQuitEnabled = vmState.isQuitEnabled,
                                    timeoutStr = vmState.timeoutStr,
                                    timeout = 0L
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
                        MediumTitle(LocalI18N.current["ui.conditions.add"])
                    },
                    state = conditionState,
                    handler = condHandler.asConditionHandler()
                )
                StandardSpacer()

                // Timeout
                FieldTitleWithTip(
                    text = LocalI18N.current["ui.action.wait.util.condition.match.timeout"],
                    tipText = LocalI18N.current["ui.action.wait.util.condition.match.timeout.summary"]
                )
                TinySpacer()
                ShortXTextField(value = vmState.timeoutStr, onValueChange = {
                    vm.setTimeout(it)
                })

                SectionSpacer()

                // Quit.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MediumTitleWithTip(
                        LocalI18N.current["ui.action.wait.util.condition.match.quit.title"],
                        LocalI18N.current["ui.action.wait.util.condition.match.quit.description"]
                    )
                    ShortXSwitch(checked = vmState.isQuitEnabled, onCheckedChange = {
                        vm.toggleQuitEnable()
                    })
                }
                AnimatedVisibility(visible = vmState.isQuitEnabled) {
                    Column {
                        StandardSpacer()
                        FactSelectors(
                            header = {
                                FieldTitle(
                                    modifier = Modifier.padding(bottom = 12.dp),
                                    text = LocalI18N.current["ui.fact.title"]
                                )
                            },
                            state = quitFactContentState,
                            handler = quitFactHandler.asFactHandler(),
                            readOnly = false
                        )

                        SectionSpacer()

                        ConditionContent(
                            header = {
                                FieldTitle(
                                    modifier = Modifier.padding(bottom = 12.dp),
                                    text = LocalI18N.current["ui.conditions.title"]
                                )
                            },
                            state = quitConditionState,
                            readOnly = false,
                            handler = quitCondHandler.asConditionHandler()
                        )

                        SectionSpacer()
                    }
                }

                SectionSpacer()
                SectionSpacer()
                SuggestedNavEntry()
            }
        }
    }
}