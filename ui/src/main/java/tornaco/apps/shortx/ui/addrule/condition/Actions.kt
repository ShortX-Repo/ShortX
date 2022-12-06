package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.ui.addrule.AddRuleState
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.base.*

@Composable
fun Actions(state: AddRuleState, selectAction: (Action) -> Unit, removeAction: (Action) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle(LocalI18N.current["ui.actions.title"])
        Box {
            val selectorState = rememberDropdownSelectorState()

            Column(modifier = Modifier.fillMaxWidth()) {
                state.actions.forEach {
                    ListItem(titleLines = labelLinesForAction(action = it), icon = {
                        IconForAction(action = it)
                    }, action1 = {
                        IconButton(onClick = {
                            removeAction(it)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close, contentDescription = "Remove"
                            )
                        }
                    }, onClick = {})
                }
                TextButton(onClick = {
                    selectorState.open()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add, contentDescription = "Select"
                    )
                    Text(LocalI18N.current["ui.actions.add"])
                }
            }

            DropdownSelector(state = selectorState, items = state.selectableActions.map {
                DropdownItem(labelLines = labelForActionSelector(it), data = it, icon = {
                    IconForAction(action = it)
                })
            }) {
                selectAction(it.data)
            }
        }
    }
}

@Composable
private fun labelForActionSelector(action: Action): List<String> {
    return when (action) {
        is Action.ShowToast -> {
            listOf("Show Toast")
        }
        is Action.ShellCommand -> {
            listOf("Shell command")
        }
        is Action.Delay -> {
            listOf("Delay")
        }
        Action.TakeScreenshot -> {
            listOf("Take Screenshot")
        }
    }
}

@Composable
private fun labelLinesForAction(action: Action): List<String> {
    return when (action) {
        is Action.ShowToast -> {
            listOf("Show Toast", action.message)
        }
        is Action.ShellCommand -> {
            listOf("Shell command", action.command)
        }
        is Action.Delay -> {
            listOf("Delay", action.time.toString())
        }
        Action.TakeScreenshot -> {
            listOf("Take Screenshot")
        }
    }
}

@Composable
private fun IconForAction(action: Action) {
    when (action) {
        is Action.ShowToast -> {
            Icon(
                imageVector = Icons.Filled.Message, contentDescription = "Toast"
            )
        }
        is Action.ShellCommand -> {
            Icon(
                imageVector = Icons.Filled.Tag, contentDescription = "Shell"
            )
        }
        is Action.Delay -> {
            Icon(
                imageVector = Icons.Filled.Timer, contentDescription = "Delay"
            )
        }
        Action.TakeScreenshot -> {
            Icon(
                imageVector = Icons.Filled.Phone, contentDescription = "Take Screenshot"
            )
        }
    }
}