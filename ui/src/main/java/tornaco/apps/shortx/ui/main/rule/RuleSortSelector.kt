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
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.main.RuleSortBy
import tornaco.apps.shortx.core.i18n.I18N_R

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
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = ShortXCardRoundedCornerShape)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                RuleSortBy.entries.forEach { sortBy ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = when (sortBy) {
                                    RuleSortBy.Name -> i18n[I18N_R.UI_SORT_BY_NAME]
                                    RuleSortBy.NameReverse -> i18n[I18N_R.UI_SORT_BY_NAME_DESC]
                                    RuleSortBy.CreateTime -> i18n[I18N_R.UI_SORT_BY_CREATE_TIME]
                                    RuleSortBy.CreateTimeReverse -> i18n[I18N_R.UI_SORT_BY_CREATE_TIME_DESC]
                                    RuleSortBy.LastUpdateTime -> i18n[I18N_R.UI_SORT_BY_UPDATE_TIME]
                                    RuleSortBy.LastUpdateTimeReverse -> i18n[I18N_R.UI_SORT_BY_UPDATE_TIME_DESC]
                                    RuleSortBy.EnableState -> i18n[I18N_R.UI_SORT_BY_ENABLE_STATE]
                                    RuleSortBy.EnableStateReverse -> i18n[I18N_R.UI_SORT_BY_ENABLE_STATE_DESC]
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