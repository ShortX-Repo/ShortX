package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.base.*

@Composable
fun RuleScreen(
    state: MainState,
    onDelete: (Rule) -> Unit,
    onRuleClick: (Rule) -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(
            items = state.rules,
            key = { listItem: Rule -> listItem.id + listItem.updateTime }) { rule ->
            Card(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                shape = RoundedCornerShape(12.dp),
                elevation = 0.dp
            ) {
                RuleItem(
                    rule = rule,
                    onClick = {
                        onRuleClick(rule)
                    },
                    setRuleEnabled = setRuleEnabled,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun RuleItem(
    rule: Rule,
    onClick: () -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
    onDelete: (Rule) -> Unit
) {
    ListItem(
        title = rule.title,
        text1 = rule.description,
        text2 = LocalI18N.current["ui.update.time", mapOf("date" to rule.updateTime)],
        overflow = {
            ListItemOverflowMenu(rule, onDelete)
        },
        checked = rule.isEnabled,
        onClick = onClick,
        onCheckedChange = { setRuleEnabled(rule.id, it) }
    )
}

@Composable
private fun ListItemOverflowMenu(rule: Rule, onDelete: (Rule) -> Unit) {
    Box {
        val menuState = rememberDropdownSelectorState()
        IconButton(
            modifier = Modifier,
            onClick = {
                menuState.open()
            }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
        }

        DropdownSelector(
            state = menuState,
            items = listOf(
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.delete"]),
                    data = "delete",
                    icon = {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                    })
            ), onSelect = {
                when (it.data) {
                    "delete" -> {
                        onDelete(rule)
                    }
                    else -> {}
                }
            })
    }
}