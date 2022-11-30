package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ListItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RuleScreen(
    state: MainState,
    onDelete: (String) -> Unit,
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
                    setRuleEnabled = setRuleEnabled
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
) {
    ListItem(
        title = rule.title,
        text1 = rule.description,
        text2 = rule.updateTime,
        checked = rule.isEnabled,
        onClick = onClick,
        onCheckedChange = { setRuleEnabled(rule.id, it) }
    )
}