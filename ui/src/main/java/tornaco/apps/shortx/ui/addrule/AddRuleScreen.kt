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
import dev.enro.core.NavigationKey
import dev.enro.core.closeWithResult
import dev.enro.core.compose.navigationHandle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.ui.addrule.action.ShellCommandSelector
import tornaco.apps.shortx.ui.addrule.action.rememberShellCommandSelectorState
import tornaco.apps.shortx.ui.addrule.condition.Actions
import tornaco.apps.shortx.ui.addrule.condition.Conditions
import tornaco.apps.shortx.ui.addrule.condition.CurrentPkgSelector
import tornaco.apps.shortx.ui.addrule.condition.rememberCurrentPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.*
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.addrule.models.Condition
import tornaco.apps.shortx.ui.addrule.models.Fact
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.theme.ShortXTheme

@Parcelize
data class AddRule(val ruleId: String? = null) : NavigationKey.SupportsPresent.WithResult<Boolean>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleScreen() {
    val navigation = navigationHandle<AddRule>()

    val vm = hiltViewModel<AddRuleViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        val ruleId = navigation.key.ruleId
        vm.initFromRuleId(ruleId)
        vm.loadInstalledApps()
        vm.ruleSaved.collectLatest {
            navigation.closeWithResult(true)
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
                                navigation.closeWithResult(false)
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
                // Fact selectors
                val keyEventSelectorState = rememberKeyEventSelectorState(selected = { code ->
                    vm.setFact(Fact.KeyEvent(code))
                })
                KeyEventSelector(keyEventSelectorState)

                val edgeGestureSelectorState = rememberEdgeGestureSelectorState { eg ->
                    vm.setFact(Fact.EdgeGesture(eg.first, eg.second))
                }
                EdgeGestureSelector(edgeGestureSelectorState)

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(it)
                        .padding(horizontal = 16.dp)
                        .animateContentSize()
                ) {

                    TitleAndDescription(state, vm)
                    Spacer(modifier = Modifier.size(16.dp))

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
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))

                    // Condition selectors
                    val currentPkgSelectorState =
                        rememberCurrentPkgSelectorState(state.installedApps) { pkg ->
                            vm.addCondition(Condition.CurrentForegroundApp(pkg))
                        }
                    CurrentPkgSelector(currentPkgSelectorState)
                    Conditions(state = state, selectCondition = { con ->
                        if (con is Condition.CurrentForegroundApp) {
                            currentPkgSelectorState.show()
                        }
                    }, removeCondition = { con ->
                        vm.removeCondition(con)
                    })
                    Spacer(modifier = Modifier.size(16.dp))

                    // Actions
                    val shellCommandSelectorState = rememberShellCommandSelectorState { cmd ->
                        vm.addAction(Action.ShellCommand(cmd))
                    }
                    ShellCommandSelector(state = shellCommandSelectorState)

                    Actions(state = state, selectAction = { action: Action ->
                        when (action) {
                            is Action.TakeScreenshot -> {
                                vm.addAction(action)
                            }
                            is Action.ShellCommand -> {
                                shellCommandSelectorState.show()
                            }
                            is Action.ShowToast -> {
                                vm.addAction(Action.ShowToast("Hello"))
                            }
                            is Action.Delay -> {
                                vm.addAction(Action.Delay(1000))
                            }
                        }
                    }, removeAction = { action ->
                        vm.removeAction(action)
                    })
                    Spacer(modifier = Modifier.size(16.dp))
                }
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