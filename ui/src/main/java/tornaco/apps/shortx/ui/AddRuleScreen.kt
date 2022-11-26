package tornaco.apps.shortx.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.enro.annotations.NavigationDestination
import dev.enro.core.NavigationKey
import dev.enro.core.closeWithResult
import dev.enro.core.compose.navigationHandle
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.ui.addrule.AddRuleState
import tornaco.apps.shortx.ui.addrule.AddRuleViewModel
import tornaco.apps.shortx.ui.addrule.Fact
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme

@Parcelize
object AddRule : NavigationKey.SupportsPresent.WithResult<Boolean>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@NavigationDestination(AddRule::class)
fun AddRuleScreen() {
    val navigation = navigationHandle<AddRule>()
    ShortXTheme {
        Surface(modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Add Rule") },
                        navigationIcon = {
                            IconButton(onClick = {
                                navigation.closeWithResult(false)
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        })
                },
            ) {

                val vm = hiltViewModel<AddRuleViewModel>()
                val state by vm.state.collectAsState()
                Column(modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 16.dp)) {
                    FactSelector(state) { fact ->
                        vm.setFact(fact)
                    }
                }
            }
        }
    }
}

@Composable
private fun FactSelector(state: AddRuleState, selectFact: (Fact) -> Unit) {
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