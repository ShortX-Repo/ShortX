package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.addrule.AddRuleState
import tornaco.apps.shortx.ui.addrule.models.Condition
import tornaco.apps.shortx.ui.base.*

@Composable
fun Conditions(
    state: AddRuleState, selectCondition: (Condition) -> Unit, removeCondition: (Condition) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle("Conditions")

        Box {
            val selectorState = rememberDropdownSelectorState()

            Column(modifier = Modifier.fillMaxWidth()) {
                state.conditions.forEach {
                    ListItem(titleLines = labelForCondition(condition = it), icon = {
                        IconForCondition(it)
                    }, action1 = {
                        IconButton(onClick = {
                            removeCondition(it)
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
                    Text("Add Condition")
                }
            }

            DropdownSelector(state = selectorState, items = state.selectableConditions.map {
                DropdownItem(
                    labelLines = labelForConditionSelector(condition = it),
                    data = it,
                    icon = {
                        IconForConditionSelector(condition = it)
                    }
                )
            }) {
                selectCondition(it.data)
            }
        }
    }
}

@Composable
private fun labelForConditionSelector(condition: Condition): List<String> {
    return when (condition) {
        is Condition.CurrentForegroundApp -> {
            listOf(LocalI18N.current["ui.dialog.title.current.app"])
        }
    }
}

@Composable
private fun labelForCondition(condition: Condition): List<String> {
    return when (condition) {
        is Condition.CurrentForegroundApp -> {
            listOf(LocalI18N.current["ui.dialog.title.current.app.with.app.label", mapOf("app" to condition.app.label)])
        }
    }
}

@Composable
private fun IconForCondition(condition: Condition) {
    if (condition is Condition.CurrentForegroundApp) {
        AppIcon(modifier = Modifier.size(24.dp), condition.app.pkg)
    }
}


@Composable
private fun IconForConditionSelector(condition: Condition) {
    when (condition) {
        is Condition.CurrentForegroundApp -> {
            Icon(
                imageVector = Icons.Filled.Android, contentDescription = "App"
            )
        }
    }
}
