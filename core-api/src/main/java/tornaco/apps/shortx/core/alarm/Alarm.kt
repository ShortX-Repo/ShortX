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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

private const val MAX_SCHEDULE_LOOKAHEAD_DAYS = 370

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

fun Alarm.getTriggerTimeOffset(
    dateFilter: ((LocalDate) -> Boolean)? = null,
    now: LocalDateTime = LocalDateTime.now(),
): Long {
    val today = now.toLocalDate()
    for (dayOffset in 0..MAX_SCHEDULE_LOOKAHEAD_DAYS) {
        if (repeat.isNo && dayOffset > 0) {
            break
        }

        val candidateDate = today.plusDays(dayOffset.toLong())
        if (!repeat.isNo && !repeat.matchesDate(candidateDate)) {
            continue
        }
        if (dateFilter != null && !dateFilter(candidateDate)) {
            continue
        }

        val candidateDateTime = candidateDate.atTime(triggerAt.hour, triggerAt.minutes, triggerAt.seconds)
        if (!candidateDateTime.isAfter(now)) {
            continue
        }

        return Duration.between(now, candidateDateTime).toMillis()
    }

    return -1L
}

private fun RepeatDays.matchesDate(date: LocalDate): Boolean {
    return sortedDays.any { it.toJavaDayOfWeek() == date.dayOfWeek }
}

private fun WeekDay.toJavaDayOfWeek() = when (this) {
    WeekDay.SUNDAY -> java.time.DayOfWeek.SUNDAY
    WeekDay.SATURDAY -> java.time.DayOfWeek.SATURDAY
    WeekDay.FRIDAY -> java.time.DayOfWeek.FRIDAY
    WeekDay.THURSDAY -> java.time.DayOfWeek.THURSDAY
    WeekDay.WEDNESDAY -> java.time.DayOfWeek.WEDNESDAY
    WeekDay.TUESDAY -> java.time.DayOfWeek.TUESDAY
    WeekDay.MONDAY -> java.time.DayOfWeek.MONDAY
    else -> java.time.DayOfWeek.MONDAY
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