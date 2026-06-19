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
import tornaco.apps.shortx.core.i18n.I18N_R

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
                                    DASortBy.Name -> i18n[I18N_R.UI_SORT_BY_NAME]
                                    DASortBy.NameReverse -> i18n[I18N_R.UI_SORT_BY_NAME_DESC]
                                    DASortBy.CreateTime -> i18n[I18N_R.UI_SORT_BY_CREATE_TIME]
                                    DASortBy.CreateTimeReverse -> i18n[I18N_R.UI_SORT_BY_CREATE_TIME_DESC]
                                    DASortBy.LastUpdateTime -> i18n[I18N_R.UI_SORT_BY_UPDATE_TIME]
                                    DASortBy.LastUpdateTimeReverse -> i18n[I18N_R.UI_SORT_BY_UPDATE_TIME_DESC]
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