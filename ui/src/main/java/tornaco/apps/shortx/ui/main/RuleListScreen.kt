package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.ui.base.ListItem

@Composable
fun RuleScreen(state: MainState) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.rules) {
            RuleItem(rule = it) {
            }
        }
    }
}

@Composable
private fun RuleItem(
    rule: Rule,
    onClick: () -> Unit,
) {
    ListItem(
        title = rule.title,
        text1 = rule.description,
        text2 = rule.updateTime,
        onClick = onClick
    )
}