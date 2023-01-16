package tornaco.apps.shortx.ui.addrule

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.ui.addrule.action.ActionSelectors
import tornaco.apps.shortx.ui.addrule.action.MVELSelector
import tornaco.apps.shortx.ui.addrule.action.rememberMVELSelectorState
import tornaco.apps.shortx.ui.addrule.condition.Conditions
import tornaco.apps.shortx.ui.addrule.condition.CurrentPkgSelector
import tornaco.apps.shortx.ui.addrule.condition.rememberCurrentPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.*
import tornaco.apps.shortx.ui.addrule.models.Condition
import tornaco.apps.shortx.ui.addrule.models.Fact
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleScreen(ruleId: String? = null) {
    val vm = hiltViewModel<AddRuleViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.initFromRuleId(ruleId)
        vm.loadInstalledApps()
        vm.ruleSaved.collectLatest {
        }
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = if (state.isEditRule) LocalI18N.current["ui.title.edit.rule"] else LocalI18N.current["ui.title.add.rule"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                // TODO
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(
                                enabled = state.ruleChanged,
                                onClick = {
                                    vm.save()
                                }) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Done")
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(it)
                        .padding(horizontal = 16.dp)
                        .animateContentSize()
                ) {

                    TitleAndDescription(state, vm)
                    Spacer(modifier = Modifier.size(24.dp))

                    FactSelectors(vm, state)
                    Spacer(modifier = Modifier.size(24.dp))

                    ConditionSelectors(state, vm)
                    Spacer(modifier = Modifier.size(24.dp))

                    ActionSelectors(
                        addAction = { action ->
                            vm.addAction(
                                action
                            )
                        },
                        removeAction = { action ->
                            vm.removeAction(action)
                        },
                        selectableActions = state.selectableActions,
                        actions = state.actions
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ConditionSelectors(
    state: AddRuleState,
    vm: AddRuleViewModel
) {
    // Condition selectors
    val currentPkgSelectorState =
        rememberCurrentPkgSelectorState(state.installedApps) { pkg ->
            vm.addCondition(Condition.CurrentForegroundApp(pkg[0]))
        }
    CurrentPkgSelector(currentPkgSelectorState)

    val mvelConditionSelectorState = rememberMVELSelectorState { expression ->
        vm.addCondition(Condition.MVEL(expression))
    }
    MVELSelector(state = mvelConditionSelectorState)

    Conditions(state = state, selectCondition = { con ->
        when (con) {
            is Condition.CurrentForegroundApp -> {
                currentPkgSelectorState.show()
            }
            is Condition.MVEL -> {
                mvelConditionSelectorState.show()
            }
        }
    }, removeCondition = { con ->
        vm.removeCondition(con)
    })
}

@Composable
private fun FactSelectors(
    vm: AddRuleViewModel,
    state: AddRuleState
) {
    // Fact selectors
    val keyEventSelectorState = rememberKeyEventSelectorState(selected = { code ->
        vm.setFact(Fact.KeyEvent(code))
    })
    KeyEventSelector(keyEventSelectorState)

    val edgeGestureSelectorState = rememberEdgeGestureSelectorState(
        selected = { eg ->
            vm.setFact(Fact.EdgeGesture(eg.first, eg.second))
        }, showEdgeHint = {
            vm.showEdgeHint(it)
        })
    EdgeGestureSelector(edgeGestureSelectorState)

    val timeOfADaySelector = rememberAlarmSelectorState { alarm ->
        // Use rule id as alarm tag.
        vm.setFact(Fact.Alarm(state.id, alarm.triggerAt, alarm.repeat))
    }
    AlarmSelector(timeOfADaySelector)

    // Condition selectors
    val appBgPkgSelectorState =
        rememberCurrentPkgSelectorState(state.installedApps) { pkg ->
            vm.setFact(Fact.AppBecomeBackground(apps = pkg))
        }
    CurrentPkgSelector(appBgPkgSelectorState)

    val appFgPkgSelectorState =
        rememberCurrentPkgSelectorState(state.installedApps) { pkg ->
            vm.setFact(Fact.AppBecomeForeground(apps = pkg))
        }
    CurrentPkgSelector(appFgPkgSelectorState)

    FactSelector(state) { fact ->
        when (fact) {
            is Fact.KeyEvent -> {
                keyEventSelectorState.show()
            }
            is Fact.ActivityStarted -> {
            }
            is Fact.EdgeGesture -> {
                edgeGestureSelectorState.show()
            }
            Fact.ScreenOff -> {
                vm.setFact(Fact.ScreenOff)
            }
            Fact.ScreenOn -> {
                vm.setFact(Fact.ScreenOn)
            }
            Fact.UserPresent -> {
                vm.setFact(Fact.UserPresent)
            }
            is Fact.Alarm -> {
                timeOfADaySelector.show()
            }
            is Fact.AppBecomeBackground -> {
                appBgPkgSelectorState.show()
            }
            is Fact.AppBecomeForeground -> {
                appFgPkgSelectorState.show()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TitleAndDescription(
    state: AddRuleState,
    vm: AddRuleViewModel,
) {
    OutlinedTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.rule.title"])
    }, value = state.title, onValueChange = { title ->
        vm.setTitle(title)
    })
    Spacer(modifier = Modifier.size(16.dp))
    OutlinedTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.rule.description"])
    }, value = state.description, onValueChange = { description ->
        vm.setDescription(description)
    })
}