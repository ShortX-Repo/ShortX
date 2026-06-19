package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.core.i18n.I18N_R

fun calendarDayTypeDetailsText(i18N: I18N): String {
    return listOf(
        i18N[I18N_R.UI_CONDITIONS_CALENDAR_DAY_TYPE_HELP_SUMMARY],
        i18N[I18N_R.UI_CONDITIONS_CALENDAR_DAY_TYPE_HELP_ANY],
        i18N[I18N_R.UI_CONDITIONS_CALENDAR_DAY_TYPE_HELP_WORKDAY],
        i18N[I18N_R.UI_CONDITIONS_CALENDAR_DAY_TYPE_HELP_NON_WORKDAY],
        i18N[I18N_R.UI_CONDITIONS_CALENDAR_DAY_TYPE_HELP_HOLIDAY],
        i18N[I18N_R.UI_CONDITIONS_CALENDAR_DAY_TYPE_HELP_WEEKEND],
        i18N[I18N_R.UI_CONDITIONS_CALENDAR_DAY_TYPE_HELP_TRANSFER_WORKDAY],
    ).joinToString(separator = "\n")
}

@Composable
fun CalendarDayTypeExplanation(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = calendarDayTypeDetailsText(LocalI18N.current),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        overflow = TextOverflow.Clip,
    )
}