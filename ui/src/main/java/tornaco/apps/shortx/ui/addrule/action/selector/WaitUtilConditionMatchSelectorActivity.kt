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
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.condition.ConditionContent
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
import tornaco.apps.shortx.ui.addrule.condition.asConditionHandler
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class WaitUtilConditionMatchSelectorActivity : BaseSelectorActivity<Action.WaitUtilConditionMatch>() {
    @Composable
    override fun BaseSelectorActivity<Action.WaitUtilConditionMatch>.SelectorContent(currentData: Action.WaitUtilConditionMatch?) {
        WaitUtilConditionMatchSelectorDialogSelector(currentData)
    }
}

class WaitUtilConditionMatchSelectorViewModel :
    StateViewModel<WaitUtilConditionMatchSelectorDialogDialogState>(
        initState = {
            WaitUtilConditionMatchSelectorDialogDialogState()
        }
    ) {
    private val logger = Logger("WaitUtilConditionMatchSelectorViewModel")

    val conditionHandler: ConditionContentStateHandler = ConditionContentStateHandler()

    fun initFrom(currentSettings: Action.WaitUtilConditionMatch? = null) {
        currentSettings?.let {
            updateState {
                copy(id = currentSettings.id, note = currentSettings.note)
            }
            conditionHandler.init(
                currentSettings.conditions,
                currentSettings.op
            )
        }
    }
}

data class WaitUtilConditionMatchSelectorDialogDialogState(
    val id: String = defaultNewActionId(),
    val note: String = "",
)

@Composable
private fun BaseSelectorActivity<Action.WaitUtilConditionMatch>.WaitUtilConditionMatchSelectorDialogSelector(
    currentAction: Action.WaitUtilConditionMatch?
) {
    val vm = viewModel<WaitUtilConditionMatchSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }
    val dialogState by vm.state.collectAsState()

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
                                        id = dialogState.id,
                                        note = dialogState.note,

                                        op = conditionState.conditionOp,
                                        conditions = conditionState.conditions,
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
                            MediumTitle(LocalI18N.current["ui.conditions.title"])
                        },
                        state = conditionState,
                        handler = condHandler.asConditionHandler()
                    )

                    SectionSpacer()
                    SectionSpacer()
                    SuggestedNavEntry()
                }
            }
        }
    }
}