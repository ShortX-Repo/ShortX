package tornaco.apps.shortx.ui.main.rule

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.main.RuleSortBy

@Composable
fun RuleSortSelector(
    currentSortBy: RuleSortBy,
    onSortByChanged: (RuleSortBy) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val i18n = LocalI18N.current

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            RemixIcon(
                remixName = Remix.Editor.sort_desc,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            RuleSortBy.entries.forEach { sortBy ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = when (sortBy) {
                                RuleSortBy.Name -> i18n["ui.sort.by_name"]
                                RuleSortBy.NameReverse -> i18n["ui.sort.by_name_desc"]
                                RuleSortBy.CreateTime -> i18n["ui.sort.by_create_time"]
                                RuleSortBy.CreateTimeReverse -> i18n["ui.sort.by_create_time_desc"]
                                RuleSortBy.LastUpdateTime -> i18n["ui.sort.by_update_time"]
                                RuleSortBy.LastUpdateTimeReverse -> i18n["ui.sort.by_update_time_desc"]
                                RuleSortBy.EnableState -> i18n["ui.sort.by_priority"]
                                RuleSortBy.EnableStateReverse -> i18n["ui.sort.by_priority_desc"]
                            },
                            color = if (sortBy == currentSortBy) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        onSortByChanged(sortBy)
                        expanded = false
                    }
                )
            }
        }
    }
}