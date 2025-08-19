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
import tornaco.apps.shortx.core.util.Logger
import java.util.Calendar
import java.util.Date

private val logger = Logger("RandomInPeriodExt")

typealias JCalendar = java.util.Calendar

val RandomInPeriod.triggerDateToday: Date
    get() {
        val current = Date()
        val start = JCalendar.getInstance().apply {
            time = Date()
            set(JCalendar.HOUR_OF_DAY, start.hour)
            set(JCalendar.MINUTE, start.minutes)
            set(JCalendar.SECOND, start.seconds)
        }.time

        val end = JCalendar.getInstance().apply {
            time = Date()
            set(JCalendar.HOUR_OF_DAY, end.hour)
            set(JCalendar.MINUTE, end.minutes)
            set(JCalendar.SECOND, end.seconds)
        }.time

        val startTime = if (current >= end) {
            // Missed today.
            start.time
        } else {
            maxOf(current.time, start.time)
        }

        val randomTime = (startTime..end.time).random()
        return Date(randomTime).also {
            logger.d("triggerDateToday, current: $current")
            logger.d("triggerDateToday, start: $start")
            logger.d("triggerDateToday, end: $end")
            logger.d("triggerDateToday, rand: $it")
        }
    }


val RandomInPeriod.triggerDateForRepeat: Date
    get() {
        val start = JCalendar.getInstance().apply {
            time = Date()
            set(JCalendar.HOUR_OF_DAY, start.hour)
            set(JCalendar.MINUTE, start.minutes)
            set(JCalendar.SECOND, start.seconds)
        }.time

        val end = JCalendar.getInstance().apply {
            time = Date()
            set(JCalendar.HOUR_OF_DAY, end.hour)
            set(JCalendar.MINUTE, end.minutes)
            set(JCalendar.SECOND, end.seconds)
        }.time

        val randomTime = (start.time..end.time).random()
        return Date(randomTime).also {
            logger.d("triggerDateForRepeat, start: $start")
            logger.d("triggerDateForRepeat, end: $end")
            logger.d("triggerDateForRepeat, rand: $it")
        }
    }


fun RandomInPeriod.getTriggerTimeOffset(): Long {
    val dateToday = triggerDateToday
    val timeMillisOffset = (dateToday.time - System.currentTimeMillis())
    val nextTriggerDayOfWeekOffset = getNextTriggerDayOfWeekOffset(dateToday)
    return timeMillisOffset + (nextTriggerDayOfWeekOffset * 24 * 60 * 60 * 1000)
}

fun RandomInPeriod.getTriggerTimeOffsetForRepeat(): Long {
    val repeatDate = triggerDateForRepeat
    val timeMillisOffset = (repeatDate.time - System.currentTimeMillis())
    val nextTriggerDayOfWeekOffset = getNextTriggerDayOfWeekOffsetForRepeat()
    return timeMillisOffset + (nextTriggerDayOfWeekOffset * 24 * 60 * 60 * 1000)
}

fun RandomInPeriod.getNextTriggerDayOfWeekOffset(triggerDateToday: Date): Int {
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


fun RandomInPeriod.getNextTriggerDayOfWeekOffsetForRepeat(): Int {
    val todayWeekToday =
        Calendar.getInstance().apply { time = Date() }.get(Calendar.DAY_OF_WEEK)

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