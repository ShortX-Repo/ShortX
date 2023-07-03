package tornaco.apps.shortx.core.common

import tornaco.apps.shortx.core.alarm.toLocalTime
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.util.DateUtils
import java.time.LocalTime
import java.util.Calendar
import java.util.Date

fun TimeOfADay.timeOfADayToString(): String = DateUtils.formatLocalTime(toLocalTime())

fun LocalTime.ofTodayDate(): Date {
    return toTimeOfADay().timeOfADayToTodayDate()
}

fun LocalTime.toTimeOfADay(): TimeOfADay {
    return TimeOfADay.newBuilder().setHour(hour).setMinutes(minute).setSeconds(second).build()
}

fun TimeOfADay.timeOfADayToTodayDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    calendar[Calendar.HOUR_OF_DAY] = this.hour
    calendar[Calendar.MINUTE] = this.minutes
    calendar[Calendar.SECOND] = this.seconds
    return calendar.time
}