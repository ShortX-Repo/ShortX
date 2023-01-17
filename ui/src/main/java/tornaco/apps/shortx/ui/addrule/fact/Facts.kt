package tornaco.apps.shortx.ui.addrule.fact

import android.view.KeyEvent
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
        is Fact.Alarm -> LocalI18N.current["ui.fact.time.of.a.day"]
        is Fact.AppBecomeBackground -> LocalI18N.current["ui.fact.app.become.bg"]
        is Fact.AppBecomeForeground -> LocalI18N.current["ui.fact.app.become.fg"]
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
        is Fact.KeyEvent -> listOf(
            LocalI18N.current["ui.fact.key.event"],
            getLabelForKeyCode(keyCode = fact.keyCode)
        )
        Fact.ScreenOff -> listOf(LocalI18N.current["ui.fact.screen.off"])
        Fact.ScreenOn -> listOf(LocalI18N.current["ui.fact.screen.on"])
        Fact.UserPresent -> listOf(LocalI18N.current["ui.fact.screen.user.present"])
        is Fact.Alarm -> listOf(
            LocalI18N.current["ui.fact.time.of.a.day"],
            fact.repeatDays.toString(),
            fact.timeOfADay.toString()
        )
        is Fact.AppBecomeBackground -> listOf(
            LocalI18N.current["ui.fact.app.become.bg"],
            fact.apps.joinToString { it.label }
        )
        is Fact.AppBecomeForeground -> listOf(
            LocalI18N.current["ui.fact.app.become.fg"],
            fact.apps.joinToString { it.label }
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
                imageVector = Icons.Filled.Keyboard, contentDescription = "KeyEvent"
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
        is Fact.AppBecomeBackground -> Icon(
            imageVector = Icons.Filled.FiberDvr, contentDescription = "AppBecomeBackground"
        )
        is Fact.AppBecomeForeground -> Icon(
            imageVector = Icons.Filled.Aod, contentDescription = "AppBecomeForeground"
        )
    }
}

@Composable
private fun getLabelForKeyCode(keyCode: Int): String {
    return when (keyCode) {
        KeyEvent.KEYCODE_VOLUME_DOWN -> LocalI18N.current["ui.keycode.label.volume.down"]
        KeyEvent.KEYCODE_VOLUME_UP -> LocalI18N.current["ui.keycode.label.volume.up"]
        KeyEvent.KEYCODE_BACK -> LocalI18N.current["ui.keycode.label.back"]
        KeyEvent.KEYCODE_HOME -> LocalI18N.current["ui.keycode.label.home"]
        KeyEvent.KEYCODE_POWER -> LocalI18N.current["ui.keycode.label.power"]
        else -> keyCode.toString()
    }
}