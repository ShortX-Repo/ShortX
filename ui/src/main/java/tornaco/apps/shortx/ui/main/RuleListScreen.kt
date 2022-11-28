package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }
        .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Filled.Commit, contentDescription = "Rule")
        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier) {
            Text(text = rule.label, style = MaterialTheme.typography.titleMedium)
            Text(text = rule.updateTime, style = MaterialTheme.typography.bodyMedium)
        }
    }
}