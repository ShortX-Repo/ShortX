package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.base.LocalI18N

fun calendarDayTypeDetailsText(i18N: I18N): String {
    return listOf(
        i18N["ui.conditions.calendar.day.type.help.summary"],
        i18N["ui.conditions.calendar.day.type.help.any"],
        i18N["ui.conditions.calendar.day.type.help.workday"],
        i18N["ui.conditions.calendar.day.type.help.non.workday"],
        i18N["ui.conditions.calendar.day.type.help.holiday"],
        i18N["ui.conditions.calendar.day.type.help.weekend"],
        i18N["ui.conditions.calendar.day.type.help.transfer.workday"],
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