package tornaco.apps.shortx.ui.addrule.fact

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.alarm.isEveryDay
import tornaco.apps.shortx.core.alarm.isNo
import tornaco.apps.shortx.core.proto.fact.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import java.util.*


fun labelAndDescriptionForFactSelector(i18n: I18N, fact: Class<out Fact>): Pair<String, String?> {
    return when (fact) {
        Fact.ActivityStarted::class.java -> i18n["ui.fact.activity.started"] to null
        Fact.EdgeGesture::class.java -> i18n["ui.fact.edge.gesture"] to null
        Fact.KeyEvent::class.java -> i18n["ui.fact.key.event"] to null
        Fact.ScreenOff::class.java -> i18n["ui.fact.screen.off"] to null
        Fact.ScreenOn::class.java -> i18n["ui.fact.screen.on"] to null
        Fact.UserPresent::class.java -> i18n["ui.fact.screen.user.present"] to null
        Fact.Alarm::class.java -> i18n["ui.fact.time.of.a.day"] to null
        Fact.AppBecomeBackground::class.java -> i18n["ui.fact.app.become.bg"] to null
        Fact.AppBecomeForeground::class.java -> i18n["ui.fact.app.become.fg"] to null
        else -> "N/A" to null
    }
}

@Composable
fun labelForFact(fact: Fact): List<String> {
    val i18N = LocalI18N.current
    return labelForFact(i18N, fact)
}

fun labelForFact(i18N: I18N, fact: Fact): List<String> {
    return when (fact) {
        is Fact.ActivityStarted -> listOf(i18N["ui.fact.activity.started"], fact.name)
        is Fact.EdgeGesture -> listOf(
            i18N["ui.fact.edge.gesture"],
            "${fact.edge}",
            "${fact.gesture}"
        )
        is Fact.KeyEvent -> listOf(
            i18N["ui.fact.key.event"],
            getLabelForKeyCode(i18N, keyCode = fact.keyCode)
        )
        Fact.ScreenOff -> listOf(i18N["ui.fact.screen.off"])
        Fact.ScreenOn -> listOf(i18N["ui.fact.screen.on"])
        Fact.UserPresent -> listOf(i18N["ui.fact.screen.user.present"])
        is Fact.Alarm -> {
            listOf(
                i18N["ui.fact.time.of.a.day.at", mapOf("time" to fact.timeOfADay.toDisplayTime())],
                if (fact.repeatDays.isNo) {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to i18N["ui.fact.time.of.a.day.repeat.no"]
                    )]
                } else if (fact.repeatDays.isEveryDay) {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to i18N["ui.fact.time.of.a.day.repeat.everyday"]
                    )]
                } else {
                    i18N["ui.fact.time.of.a.day.repeat.at", mapOf(
                        "days" to fact.repeatDays.daysList.joinToString(
                            " "
                        ) {
                            getLongLabelForWeekDay(weekDay = it, i18n = i18N)
                        })]
                }
            )
        }
        is Fact.AppBecomeBackground -> mutableListOf(
            i18N["ui.fact.app.become.bg"]
        ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)

        is Fact.AppBecomeForeground -> mutableListOf(
            i18N["ui.fact.app.become.fg"]
        ).labelLinesFromAppsAndPkgSets(fact.apps, fact.pkgSets)
    }
}

@Composable
private fun getLabelForKeyCode(keyCode: Int): String {
    return getLabelForKeyCode(LocalI18N.current, keyCode)
}

private fun getLabelForKeyCode(i18N: I18N, keyCode: Int): String {
    return when (keyCode) {
        KeyEvent.KEYCODE_VOLUME_DOWN -> i18N["ui.keycode.label.volume.down"]
        KeyEvent.KEYCODE_VOLUME_UP -> i18N["ui.keycode.label.volume.up"]
        KeyEvent.KEYCODE_BACK -> i18N["ui.keycode.label.back"]
        KeyEvent.KEYCODE_HOME -> i18N["ui.keycode.label.home"]
        KeyEvent.KEYCODE_POWER -> i18N["ui.keycode.label.power"]
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