package tornaco.apps.shortx.ui.addrule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import dev.enro.core.NavigationKey
import dev.enro.core.closeWithResult
import dev.enro.core.compose.navigationHandle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.ui.addrule.action.ShellCommandSelector
import tornaco.apps.shortx.ui.addrule.action.rememberShellCommandSelectorState
import tornaco.apps.shortx.ui.addrule.condition.CurrentPkgSelector
import tornaco.apps.shortx.ui.addrule.condition.rememberCurrentPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.KeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.rememberKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.addrule.models.Condition
import tornaco.apps.shortx.ui.addrule.models.Fact
import tornaco.apps.shortx.ui.base.*
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
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = if (state.isEditRule) "Edit Rule" else "Add Rule") },
                        navigationIcon = {
                            IconButton(onClick = {
                                navigation.closeWithResult(false)
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                bottomBar = {
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .padding(vertical = 16.dp),
                        enabled = state.ruleChanged,
                        onClick = { vm.save() }) {
                        Text(if (state.isEditRule) "Update" else "Save")
                    }
                }
            ) {
                // Fact selectors
                val keyEventSelectorState = rememberKeyEventSelectorState(selected = { code ->
                    vm.setFact(Fact.KeyEvent(code))
                })
                KeyEventSelector(keyEventSelectorState)

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(it)
                        .padding(horizontal = 16.dp)
                ) {

                    TitleAndDescription(state, vm)
                    Spacer(modifier = Modifier.size(16.dp))

                    FactSelector(state) { fact ->
                        if (fact is Fact.KeyEvent) {
                            keyEventSelectorState.show()
                        } else if (fact is Fact.ActivityStarted) {
                            // noop
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))

                    // Condition selectors
                    val currentPkgSelectorState =
                        rememberCurrentPkgSelectorState(state.installedApps) { pkg ->
                            vm.addCondition(Condition.CurrentPkg(pkg))
                        }
                    CurrentPkgSelector(currentPkgSelectorState)
                    Conditions(state = state) { con ->
                        if (con is Condition.CurrentPkg) {
                            currentPkgSelectorState.show()
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))

                    // Actions
                    val shellCommandSelectorState = rememberShellCommandSelectorState { cmd ->
                        vm.addAction(Action.ShellCommand(cmd))
                    }
                    ShellCommandSelector(state = shellCommandSelectorState)
                    Actions(state = state) { action ->
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
                    }
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
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = "Title")
        },
        value = state.title, onValueChange = { title ->
            vm.setTitle(title)
        })
    Spacer(modifier = Modifier.size(16.dp))
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = "Description")
        },
        value = state.description, onValueChange = { description ->
            vm.setDescription(description)
        })
}

@Composable
private fun FactSelector(state: AddRuleState, selectFact: (Fact) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle("Fact")

        Box {
            val selectorState = rememberDropdownSelectorState()
            TextButton(onClick = {
                selectorState.open()
            }) {
                Text(text = state.fact?.label?.invoke() ?: "Select Fact")
            }

            DropdownSelector(
                state = selectorState,
                items = state.selectableFacts.map {
                    DropdownItem(label = it.label(), data = it, icon = it.icon)
                }
            ) {
                selectFact(it.data)
            }
        }
    }
}

@Composable
fun Conditions(state: AddRuleState, selectCondition: (Condition) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle("Conditions")

        val context = LocalContext.current
        Box {
            val selectorState = rememberDropdownSelectorState()

            Column(modifier = Modifier.fillMaxWidth()) {
                state.conditions.forEach {
                    tornaco.apps.shortx.ui.base.ListItem(title = it.label(LocalI18N.current)) {

                    }
                }
                TextButton(onClick = {
                    selectorState.open()
                }) {
                    Text("Add Condition")
                }
            }

            DropdownSelector(
                state = selectorState,
                items = state.selectableConditions.map {
                    DropdownItem(label = it.label(LocalI18N.current), data = it, icon = it.icon)
                }
            ) {
                selectCondition(it.data)
            }
        }
    }
}

@Composable
fun Actions(state: AddRuleState, selectAction: (Action) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle("Actions")

        val context = LocalContext.current
        Box {
            val selectorState = rememberDropdownSelectorState()

            Column(modifier = Modifier.fillMaxWidth()) {
                state.actions.forEach {
                    tornaco.apps.shortx.ui.base.ListItem(title = it.label(context)) {

                    }
                }
                TextButton(onClick = {
                    selectorState.open()
                }) {
                    Text("Add Action")
                }
            }

            DropdownSelector(
                state = selectorState,
                items = state.selectableActions.map {
                    DropdownItem(label = it.label(context), data = it, icon = it.icon)
                }
            ) {
                selectAction(it.data)
            }
        }
    }
}