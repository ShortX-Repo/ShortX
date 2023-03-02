package tornaco.apps.shortx.ui.addrule

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.ui.addrule.action.ActionSelectors
import tornaco.apps.shortx.ui.addrule.action.MVELSelector
import tornaco.apps.shortx.ui.addrule.action.rememberMVELSelectorState
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelector
import tornaco.apps.shortx.ui.addrule.condition.Conditions
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.*
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleScreen(ruleId: String? = null, back: () -> Unit) {
    val vm = hiltViewModel<AddRuleViewModel>()
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val savedMessage = LocalI18N.current["ui.toast.rule.saved"]
    val showToast = {
        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(vm) {
        vm.initFromRuleId(ruleId)
        vm.loadInstalledApps()
        vm.ruleSaved.collectLatest {
            showToast()
        }
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = if (state.isEditRule) LocalI18N.current["ui.title.edit.rule"] else LocalI18N.current["ui.title.add.rule"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(enabled = state.ruleChanged, onClick = {
                                vm.save()
                                back()
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
                        actions = state.actions
                    ) { sorted ->
                        vm.sortActions(sorted)
                    }
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ConditionSelectors(
    state: AddRuleState, vm: AddRuleViewModel
) {
    // Condition selectors
    val currentPkgSelectorState =
        rememberAppPkgSelectorState(
            apps = state.installedApps,
            onAppListSelected = { appList ->
                vm.addCondition(
                    Condition.CurrentForegroundApp(
                        apps = appList,
                        pkgSets = emptyList()
                    )
                )
            }, onPkgSetSelected = { pkgSets ->
                vm.addCondition(
                    Condition.CurrentForegroundApp(
                        apps = emptyList(),
                        pkgSets = pkgSets
                    )
                )
            })
    AppPkgSelector(currentPkgSelectorState)

    val mvelConditionSelectorState =
        rememberMVELSelectorState(title = LocalI18N.current["ui.title.mvel"]) { expression ->
            vm.addCondition(Condition.MVEL(expression))
        }
    MVELSelector(state = mvelConditionSelectorState)

    Conditions(state = state, selectCondition = { con ->
        when (con) {
            Condition.CurrentForegroundApp::class -> {
                currentPkgSelectorState.show()
            }
            Condition.MVEL::class -> {
                mvelConditionSelectorState.show()
            }
            Condition.TRUE::class -> {
                vm.addCondition(Condition.TRUE)
            }
        }
    }, removeCondition = { con ->
        vm.removeCondition(con)
    }, setConditionOp = {
        vm.setConditionOp(it)
    })
}

@Composable
private fun FactSelectors(
    vm: AddRuleViewModel, state: AddRuleState
) {
    // Fact selectors
    val keyEventSelectorState = rememberKeyEventSelectorState(selected = { code ->
        vm.setFact(Fact.KeyEvent(code))
    })
    KeyEventSelector(keyEventSelectorState)

    val edgeGestureSelectorState = rememberEdgeGestureSelectorState(selected = { eg ->
        vm.setFact(Fact.EdgeGesture(eg.first, eg.second))
    }, showEdgeHint = {
        vm.showEdgeHint(it)
    })
    EdgeGestureSelector(edgeGestureSelectorState)

    val timeOfADaySelector = rememberAlarmSelectorState(
        initialRepeat = emptyArray(),
        initialTime = LocalTime.now()
    ) { alarm ->
        // Use rule id as alarm tag.
        vm.setFact(Fact.Alarm(state.id, alarm.triggerAt, alarm.repeat))
    }
    AlarmSelector(timeOfADaySelector)

    // Condition selectors
    val appBgPkgSelectorState =
        rememberAppPkgSelectorState(apps = state.installedApps, onAppListSelected = { pkg ->
            vm.setFact(Fact.AppBecomeBackground(apps = pkg))
        }, onPkgSetSelected = {
            vm.setFact(Fact.AppBecomeBackground(pkgSets = it))
        })
    AppPkgSelector(appBgPkgSelectorState)

    val appFgPkgSelectorState =
        rememberAppPkgSelectorState(apps = state.installedApps, onAppListSelected = { pkg ->
            vm.setFact(Fact.AppBecomeForeground(apps = pkg))
        }, onPkgSetSelected = {
            vm.setFact(Fact.AppBecomeForeground(pkgSets = it))
        })
    AppPkgSelector(appFgPkgSelectorState)

    FactSelector(state) { fact ->
        when (fact) {
            Fact.KeyEvent::class -> {
                keyEventSelectorState.show()
            }
            Fact.ActivityStarted::class -> {
            }
            Fact.EdgeGesture::class -> {
                edgeGestureSelectorState.show()
            }
            Fact.ScreenOff::class -> {
                vm.setFact(Fact.ScreenOff)
            }
            Fact.ScreenOn::class -> {
                vm.setFact(Fact.ScreenOn)
            }
            Fact.UserPresent::class -> {
                vm.setFact(Fact.UserPresent)
            }
            Fact.Alarm::class -> {
                timeOfADaySelector.show()
            }
            Fact.AppBecomeBackground::class -> {
                appBgPkgSelectorState.show()
            }
            Fact.AppBecomeForeground::class -> {
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