/*
 * (C) Copyright 2022 Thanox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package tornaco.apps.shortx.core.alarm

import tornaco.apps.shortx.core.common.sortedDays
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import java.time.Duration
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

val Alarm.triggerDateToday: Date
    get() = android.icu.util.Calendar.getInstance().apply {
        time = Date()
        set(android.icu.util.Calendar.HOUR_OF_DAY, triggerAt.hour)
        set(android.icu.util.Calendar.MINUTE, triggerAt.minutes)
        set(android.icu.util.Calendar.SECOND, triggerAt.seconds)
    }.time

val RepeatDays.isNo: Boolean get() = sortedDays.isEmpty()
val RepeatDays.isEveryDay
    get() = sortedDays.containsAll(
        listOf(
            WeekDay.SUNDAY,
            WeekDay.MONDAY,
            WeekDay.TUESDAY,
            WeekDay.WEDNESDAY,
            WeekDay.THURSDAY,
            WeekDay.FRIDAY,
            WeekDay.SATURDAY
        )
    )

fun RepeatDays.isRepeatToday(): Boolean {
    val today = Calendar.getInstance().apply {
        time = Date()
    }.get(Calendar.DAY_OF_WEEK)

    return sortedDays.firstOrNull {
        it.toCalendarInt() == today
    } != null
}


fun WeekDay.toCalendarInt() = when (this) {
    WeekDay.SUNDAY -> Calendar.SUNDAY
    WeekDay.SATURDAY -> Calendar.SATURDAY
    WeekDay.FRIDAY -> Calendar.FRIDAY
    WeekDay.THURSDAY -> Calendar.THURSDAY
    WeekDay.WEDNESDAY -> Calendar.WEDNESDAY
    WeekDay.TUESDAY -> Calendar.TUESDAY
    WeekDay.MONDAY -> Calendar.MONDAY
    else -> {
        Calendar.MONDAY
    }
}

fun Alarm.getTriggerTimeOffset(): Long {
    val timeMillisOffset = (triggerDateToday.time - System.currentTimeMillis())
    val nextTriggerDayOfWeekOffset = getNextTriggerDayOfWeekOffset()
    return timeMillisOffset + (nextTriggerDayOfWeekOffset * 24 * 60 * 60 * 1000)
}

fun Alarm.getNextTriggerDayOfWeekOffset(): Int {
    // No repeat
    if (repeat.isNo) {
        return 0
    }

    val todayWeekToday =
        Calendar.getInstance().apply { time = Date() }.get(Calendar.DAY_OF_WEEK)

    // Today will trigger
    val hasRepeatToday =
        repeat.sortedDays.firstOrNull { it.toCalendarInt() == todayWeekToday } != null
    val isCurrentBeforeTodayTriggerTime = System.currentTimeMillis() < triggerDateToday.time
    if (hasRepeatToday && isCurrentBeforeTodayTriggerTime) {
        return 0
    }

    // Today will not, find next day
    val nextRepeatDay = repeat.sortedDays.firstOrNull { it.toCalendarInt() > todayWeekToday }
    return if (nextRepeatDay != null) {
        // nextDayOffsetFromToday
        nextRepeatDay.toCalendarInt() - todayWeekToday
    } else {
        // This is last day of repeat, just find the min one.
        // 1 2 3 4 5 6 7
        // 5 -> 1 = 3 = 7 - 5 + 1
        // 3 -> 2 = 6 = 7 - 3 + 2
        val minRepeatDay = repeat.sortedDays.minBy { it.toCalendarInt() }.toCalendarInt()
        // nextWeekDayOffset
        7 - todayWeekToday + minRepeatDay
    }
}


fun formatDuration(duration: Duration): String {
    val seconds = duration.seconds
    val absSeconds = abs(seconds)
    val positive = String.format(
        Locale.ENGLISH,
        "%d:%02d:%02d", absSeconds / 3600, absSeconds % 3600 / 60, absSeconds % 60
    )
    return if (seconds < 0) "-$positive" else positive
}

fun TimeOfADay.toLocalTime(): LocalTime = LocalTime.of(hour, minutes, seconds)