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
                    ListItem(
                        titleLines = labelForFact(fact = it),
                        icon = { IconForFact(fact = it) })
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
                DropdownItem(
                    labelLines = listOf(labelForFactSelector(it)),
                    data = it,
                    icon = { IconForFact(fact = it) })
            }) {
                selectFact(it.data)
            }
        }
    }
}

@Composable
private fun labelForFactSelector(fact: Fact): String {
    return when (fact) {
        is Fact.ActivityStarted -> LocalI18N.current["ui.fact.activity.started"]
        is Fact.EdgeGesture -> LocalI18N.current["ui.fact.edge.gesture"]
        is Fact.KeyEvent -> LocalI18N.current["ui.fact.key.event"]
        Fact.ScreenOff -> LocalI18N.current["ui.fact.screen.off"]
        Fact.ScreenOn -> LocalI18N.current["ui.fact.screen.on"]
        Fact.UserPresent -> LocalI18N.current["ui.fact.screen.user.present"]
        is Fact.Alarm -> "Time of a day"
    }
}


@Composable
private fun labelForFact(fact: Fact): List<String> {
    return when (fact) {
        is Fact.ActivityStarted -> listOf(LocalI18N.current["ui.fact.activity.started"], fact.name)
        is Fact.EdgeGesture -> listOf(
            LocalI18N.current["ui.fact.edge.gesture"],
            "${fact.edge}",
            "${fact.gesture}"
        )
        is Fact.KeyEvent -> listOf(LocalI18N.current["ui.fact.key.event"], "${fact.keyCode}")
        Fact.ScreenOff -> listOf(LocalI18N.current["ui.fact.screen.off"])
        Fact.ScreenOn -> listOf(LocalI18N.current["ui.fact.screen.on"])
        Fact.UserPresent -> listOf(LocalI18N.current["ui.fact.screen.user.present"])
        is Fact.Alarm -> listOf(
            "Time of a day",
            fact.label,
            fact.repeatDays.toString(),
            fact.timeOfADay.toString()
        )
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
        Fact.ScreenOff -> {
            Icon(
                imageVector = Icons.Filled.Lock, contentDescription = "ScreenOff"
            )
        }
        Fact.ScreenOn -> {
            Icon(
                imageVector = Icons.Filled.Smartphone, contentDescription = "ScreenOn"
            )
        }
        Fact.UserPresent -> {
            Icon(
                imageVector = Icons.Filled.LockOpen, contentDescription = "UserPresent"
            )
        }
        is Fact.Alarm -> {
            Icon(
                imageVector = Icons.Filled.Alarm, contentDescription = "Alarm"
            )
        }
    }
}