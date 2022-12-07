package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

            Column {
                state.fact?.let {
                    ListItem(title = labelForFact(fact = it), icon = { IconForFact(fact = it) })
                }

                TextButton(onClick = {
                    selectorState.open()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings, contentDescription = "Select"
                    )
                    TinySpacer()
                    Text(text = LocalI18N.current["ui.fact.select"])
                }
            }

            DropdownSelector(state = selectorState, items = state.selectableFacts.map {
                DropdownItem(labelLines = listOf(labelForFactSelector(it)), data = it)
            }) {
                selectFact(it.data)
            }
        }
    }
}

@Composable
private fun labelForFactSelector(fact: Fact): String {
    return when (fact) {
        is Fact.ActivityStarted -> "Activity started"
        is Fact.EdgeGesture -> "Edge gesture"
        is Fact.KeyEvent -> "Key event"
    }
}


@Composable
private fun labelForFact(fact: Fact): String {
    return when (fact) {
        is Fact.ActivityStarted -> "Activity ${fact.name} started"
        is Fact.EdgeGesture -> "Edge gesture: ${fact.edge} ${fact.gesture}"
        is Fact.KeyEvent -> "Key event: ${fact.keyCode}"
    }
}

@Composable
private fun IconForFact(fact: Fact) {
    when (fact) {
        is Fact.ActivityStarted -> {
            Icon(
                imageVector = Icons.Filled.Update, contentDescription = "ActivityStarted"
            )
        }
        is Fact.EdgeGesture -> {
            Icon(
                imageVector = Icons.Filled.Gesture, contentDescription = "EdgeGesture"
            )
        }
        is Fact.KeyEvent -> {
            Icon(
                imageVector = Icons.Filled.Key, contentDescription = "KeyEvent"
            )
        }
    }
}