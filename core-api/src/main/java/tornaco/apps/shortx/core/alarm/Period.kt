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
import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

private const val MAX_SCHEDULE_LOOKAHEAD_DAYS = 370


fun RandomInPeriod.getTriggerTimeOffset(
    dateFilter: ((LocalDate) -> Boolean)? = null,
    now: LocalDateTime = LocalDateTime.now(),
): Long {
    return getTriggerTimeOffsetInternal(
        dateFilter = dateFilter,
        now = now,
        startDayOffset = 0,
    )
}

fun RandomInPeriod.getTriggerTimeOffsetForRepeat(
    dateFilter: ((LocalDate) -> Boolean)? = null,
    now: LocalDateTime = LocalDateTime.now(),
): Long {
    return getTriggerTimeOffsetInternal(
        dateFilter = dateFilter,
        now = now,
        startDayOffset = 1,
    )
}

private fun RandomInPeriod.getTriggerTimeOffsetInternal(
    dateFilter: ((LocalDate) -> Boolean)?,
    now: LocalDateTime,
    startDayOffset: Int,
): Long {
    val today = now.toLocalDate()
    for (dayOffset in startDayOffset..MAX_SCHEDULE_LOOKAHEAD_DAYS) {
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

        val candidateDateTime = randomTriggerDateOn(candidateDate, now, dayOffset == 0) ?: continue
        if (!candidateDateTime.isAfter(now)) {
            continue
        }

        return Duration.between(now, candidateDateTime).toMillis()
    }

    return -1L
}

private fun RandomInPeriod.randomTriggerDateOn(
    date: LocalDate,
    now: LocalDateTime,
    clampToNow: Boolean,
): LocalDateTime? {
    val zoneId = ZoneId.systemDefault()
    val startDateTime = date.atTime(start.hour, start.minutes, start.seconds)
    val endDateTime = date.atTime(end.hour, end.minutes, end.seconds)
    val effectiveStart = if (clampToNow && now.isAfter(startDateTime)) now else startDateTime
    if (!endDateTime.isAfter(effectiveStart)) {
        return null
    }

    val startMillis = effectiveStart.atZone(zoneId).toInstant().toEpochMilli()
    val endMillis = endDateTime.atZone(zoneId).toInstant().toEpochMilli()
    val randomMillis = (startMillis..endMillis).random()
    return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(randomMillis), zoneId)
}

private fun tornaco.apps.shortx.core.proto.fact.RepeatDays.matchesDate(date: LocalDate): Boolean {
    return sortedDays.any { it.toJavaDayOfWeek() == date.dayOfWeek }
}

private fun tornaco.apps.shortx.core.proto.fact.WeekDay.toJavaDayOfWeek() = when (this) {
    tornaco.apps.shortx.core.proto.fact.WeekDay.SUNDAY -> java.time.DayOfWeek.SUNDAY
    tornaco.apps.shortx.core.proto.fact.WeekDay.SATURDAY -> java.time.DayOfWeek.SATURDAY
    tornaco.apps.shortx.core.proto.fact.WeekDay.FRIDAY -> java.time.DayOfWeek.FRIDAY
    tornaco.apps.shortx.core.proto.fact.WeekDay.THURSDAY -> java.time.DayOfWeek.THURSDAY
    tornaco.apps.shortx.core.proto.fact.WeekDay.WEDNESDAY -> java.time.DayOfWeek.WEDNESDAY
    tornaco.apps.shortx.core.proto.fact.WeekDay.TUESDAY -> java.time.DayOfWeek.TUESDAY
    tornaco.apps.shortx.core.proto.fact.WeekDay.MONDAY -> java.time.DayOfWeek.MONDAY
    else -> java.time.DayOfWeek.MONDAY
}