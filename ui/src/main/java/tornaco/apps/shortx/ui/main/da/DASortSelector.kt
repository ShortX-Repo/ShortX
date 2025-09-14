package tornaco.apps.shortx.ui.main.da

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.main.DASortBy

@Composable
fun DASortSelector(
    currentSortBy: DASortBy,
    onSortByChanged: (DASortBy) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val i18n = LocalI18N.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { expanded = true }
        ) {
            RemixIcon(remixName = Remix.Editor.sort_desc)
        }
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = ShortXCardRoundedCornerShape)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DASortBy.entries.forEach { sortBy ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = when (sortBy) {
                                    DASortBy.Name -> i18n["ui.sort.by_name"]
                                    DASortBy.NameReverse -> i18n["ui.sort.by_name_desc"]
                                    DASortBy.CreateTime -> i18n["ui.sort.by_create_time"]
                                    DASortBy.CreateTimeReverse -> i18n["ui.sort.by_create_time_desc"]
                                    DASortBy.LastUpdateTime -> i18n["ui.sort.by_update_time"]
                                    DASortBy.LastUpdateTimeReverse -> i18n["ui.sort.by_update_time_desc"]
                                }
                            )
                        },
                        onClick = {
                            onSortByChanged(sortBy)
                            expanded = false
                        },
                        leadingIcon = if (currentSortBy == sortBy) {
                            {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        } else null
                    )
                }
            }
        }
    }
}