package tornaco.apps.shortx.ui.addrule.fact

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.alarm.isEveryDay
import tornaco.apps.shortx.core.alarm.isNo
import tornaco.apps.shortx.core.proto.fact.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.models.Fact
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.PREFIX_APP_ICON
import java.util.*


@Composable
fun labelForFactSelector(fact: Fact): String {
    return labelForFactSelector(LocalI18N.current, fact)
}

fun labelForFactSelector(i18n: I18N, fact: Fact): String {
    return when (fact) {
        is Fact.ActivityStarted -> i18n["ui.fact.activity.started"]
        is Fact.EdgeGesture -> i18n["ui.fact.edge.gesture"]
        is Fact.KeyEvent -> i18n["ui.fact.key.event"]
        Fact.ScreenOff -> i18n["ui.fact.screen.off"]
        Fact.ScreenOn -> i18n["ui.fact.screen.on"]
        Fact.UserPresent -> i18n["ui.fact.screen.user.present"]
        is Fact.Alarm -> i18n["ui.fact.time.of.a.day"]
        is Fact.AppBecomeBackground -> i18n["ui.fact.app.become.bg"]
        is Fact.AppBecomeForeground -> i18n["ui.fact.app.become.fg"]
    }
}


@Composable
fun labelForFact(fact: Fact): List<String> {
    return when (fact) {
        is Fact.ActivityStarted -> listOf(LocalI18N.current["ui.fact.activity.started"], fact.name)
        is Fact.EdgeGesture -> listOf(
            LocalI18N.current["ui.fact.edge.gesture"],
            "${fact.edge}",
            "${fact.gesture}"
        )
        is Fact.KeyEvent -> listOf(
            LocalI18N.current["ui.fact.key.event"],
            getLabelForKeyCode(keyCode = fact.keyCode)
        )
        Fact.ScreenOff -> listOf(LocalI18N.current["ui.fact.screen.off"])
        Fact.ScreenOn -> listOf(LocalI18N.current["ui.fact.screen.on"])
        Fact.UserPresent -> listOf(LocalI18N.current["ui.fact.screen.user.present"])
        is Fact.Alarm -> {
            val i18N = LocalI18N.current
            listOf(
                LocalI18N.current["ui.fact.time.of.a.day.at", mapOf("time" to fact.timeOfADay.toDisplayTime())],
                if (fact.repeatDays.isNo) {
                    LocalI18N.current["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to LocalI18N.current["ui.fact.time.of.a.day.repeat.no"]
                    )]
                } else if (fact.repeatDays.isEveryDay) {
                    LocalI18N.current["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to LocalI18N.current["ui.fact.time.of.a.day.repeat.everyday"]
                    )]
                } else {
                    LocalI18N.current["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to fact.repeatDays.daysList.joinToString(
                            " "
                        ) {
                            getLongLabelForWeekDay(weekDay = it, i18n = i18N)
                        })]
                }
            )
        }
        is Fact.AppBecomeBackground -> mutableListOf(
            LocalI18N.current["ui.fact.app.become.bg"]
        ).apply {
            addAll(fact.apps.map {
                "${it.label}${PREFIX_APP_ICON}${it.pkg.pkgName}"
            })
        }
        is Fact.AppBecomeForeground -> mutableListOf(
            LocalI18N.current["ui.fact.app.become.fg"]
        ).apply {
            addAll(fact.apps.map {
                "${it.label}${PREFIX_APP_ICON}${it.pkg.pkgName}"
            })
        }
    }
}

@Composable
private fun getLabelForKeyCode(keyCode: Int): String {
    return when (keyCode) {
        KeyEvent.KEYCODE_VOLUME_DOWN -> LocalI18N.current["ui.keycode.label.volume.down"]
        KeyEvent.KEYCODE_VOLUME_UP -> LocalI18N.current["ui.keycode.label.volume.up"]
        KeyEvent.KEYCODE_BACK -> LocalI18N.current["ui.keycode.label.back"]
        KeyEvent.KEYCODE_HOME -> LocalI18N.current["ui.keycode.label.home"]
        KeyEvent.KEYCODE_POWER -> LocalI18N.current["ui.keycode.label.power"]
        else -> keyCode.toString()
    }
}

@Composable
fun getShortLabelForWeekDay(weekDay: WeekDay): String {
    return when (weekDay) {
        WeekDay.MONDAY -> LocalI18N.current["ui.fact.weekday.monday.short"]
        WeekDay.TUESDAY -> LocalI18N.current["ui.fact.weekday.tuesday.short"]
        WeekDay.WEDNESDAY -> LocalI18N.current["ui.fact.weekday.wednesday.short"]
        WeekDay.THURSDAY -> LocalI18N.current["ui.fact.weekday.thursday.short"]
        WeekDay.FRIDAY -> LocalI18N.current["ui.fact.weekday.friday.short"]
        WeekDay.SATURDAY -> LocalI18N.current["ui.fact.weekday.saturday.short"]
        WeekDay.SUNDAY -> LocalI18N.current["ui.fact.weekday.sunday.short"]
        else -> {
            "N/A"
        }
    }
}

fun getLongLabelForWeekDay(i18n: I18N, weekDay: WeekDay): String {
    return when (weekDay) {
        WeekDay.MONDAY -> i18n["ui.fact.weekday.monday.long"]
        WeekDay.TUESDAY -> i18n["ui.fact.weekday.tuesday.long"]
        WeekDay.WEDNESDAY -> i18n["ui.fact.weekday.wednesday.long"]
        WeekDay.THURSDAY -> i18n["ui.fact.weekday.thursday.long"]
        WeekDay.FRIDAY -> i18n["ui.fact.weekday.friday.long"]
        WeekDay.SATURDAY -> i18n["ui.fact.weekday.saturday.long"]
        WeekDay.SUNDAY -> i18n["ui.fact.weekday.sunday.long"]
        else -> {
            "N/A"
        }
    }
}

fun TimeOfADay.toDisplayTime(): String {
    val date = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, 0)
    }.time
    return DateUtils.formatShortForMessageTime(date.time)
}