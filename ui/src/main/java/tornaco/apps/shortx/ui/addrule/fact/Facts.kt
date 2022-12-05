package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.ui.addrule.AddRuleState
import tornaco.apps.shortx.ui.addrule.models.Fact
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState

@Composable
fun FactSelector(state: AddRuleState, selectFact: (Fact) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle("Fact")

        Box {
            val selectorState = rememberDropdownSelectorState()
            TextButton(onClick = {
                selectorState.open()
            }) {
                Text(text = state.fact?.label?.invoke() ?: "Select Fact")
            }

            DropdownSelector(state = selectorState, items = state.selectableFacts.map {
                DropdownItem(labelLines = listOf(it.label()), data = it)
            }) {
                selectFact(it.data)
            }
        }
    }
}
