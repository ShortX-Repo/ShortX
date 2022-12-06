package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.ui.addrule.AddRuleState
import tornaco.apps.shortx.ui.addrule.models.Fact
import tornaco.apps.shortx.ui.base.*

@Composable
fun FactSelector(state: AddRuleState, selectFact: (Fact) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle(LocalI18N.current["ui.fact.title"])

        Box {
            val selectorState = rememberDropdownSelectorState()

            TextButton(onClick = {
                selectorState.open()
            }) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Select"
                )
                Text(text = state.fact?.label?.invoke() ?: LocalI18N.current["ui.fact.select"])
            }

            DropdownSelector(state = selectorState, items = state.selectableFacts.map {
                DropdownItem(labelLines = listOf(it.label()), data = it)
            }) {
                selectFact(it.data)
            }
        }
    }
}
