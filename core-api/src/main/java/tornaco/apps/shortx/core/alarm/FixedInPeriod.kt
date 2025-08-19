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

import android.os.Parcel
import android.os.Parcelable
import tornaco.apps.shortx.core.common.sortedDays
import tornaco.apps.shortx.core.proto.fact.FixedByInterval
import tornaco.apps.shortx.core.proto.fact.FixedByTimes
import tornaco.apps.shortx.core.proto.fact.FixedInPeriod
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

private val logger = Logger("FixedInPeriodExt")

val FixedInPeriod.triggerDateToday: Date
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

        val timeSegments = getTriggerTimeSegments(fixedBy, start, end)
        logger.i("timeSegments: $timeSegments")

        val triggerTimeFromSegments = getNextTriggerTimeFromSegments(Date(), timeSegments)
        logger.i("getTriggerTimeFromSegments: $triggerTimeFromSegments")

        return triggerTimeFromSegments.also {
            logger.d("triggerDateToday, current: $current")
            logger.d("triggerDateToday, start: $start")
            logger.d("triggerDateToday, end: $end")
            logger.d("triggerDateToday, trigger: $it")
        }
    }

fun getTriggerTimeSegments(
    fixedBy: ProtoAny,
    start: Date,
    end: Date
) = if (fixedBy is_ FixedByInterval::class.java) {
    val interval = (fixedBy unpack_ FixedByInterval::class.java).interval
    getTriggerTimeSegmentsByInterval(interval, start, end, true)
} else if (fixedBy is_ FixedByTimes::class.java) {
    val times = (fixedBy unpack_ FixedByTimes::class.java).times
    getTriggerTimeSegmentsByTimes(times, start, end, true)
} else {
    throw IllegalArgumentException("Bad fixedBy: ${fixedBy.typeUrl}")
}.apply {
    logger.d("getTriggerTimeSegments, res: ${this.joinToString(System.lineSeparator())}")
}

fun getNextTriggerTimeFromSegments(currentDate: Date, segments: List<Date>): Date {
    return segments.firstOrNull { it.time > currentDate.time }
        ?: segments.first()
}

fun getTriggerTimeSegmentsByInterval(
    interval: Long,
    start: Date,
    end: Date,
    inclusive: Boolean
): List<Date> {
    if (interval <= 0) return emptyList()
    val totalPeriodMillis = end.time - start.time
    val totalTimes: Int = (totalPeriodMillis / interval).toInt()
    logger.d("getTriggerTimeSegmentsByInterval, start: $start, end: $end, interval: $interval, totalPeriodMillis: $totalPeriodMillis, totalTimes: $totalTimes")
    val segments = mutableListOf<Date>()
    for (i in (if (inclusive) 0 else 1)..totalTimes) {
        segments.add(Date(start.time + interval * i))
    }
    return segments
}

fun getTriggerTimeSegmentsByTimes(
    totalTimes: Int,
    start: Date,
    end: Date,
    startInclusive: Boolean
): List<Date> {
    if (totalTimes <= 0) return emptyList()
    val totalPeriodMillis = end.time - start.time
    val interval: Long =
        totalPeriodMillis / maxOf(1, if (startInclusive) totalTimes - 1 else totalTimes)
    logger.d("getTriggerTimeSegmentsByTimes, start: $start, end: $end, interval: $interval, totalPeriodMillis: $totalPeriodMillis, totalTimes: $totalTimes")
    val segments = mutableListOf<Date>()
    for (i in (if (startInclusive) 0 else 1)..if (startInclusive) totalTimes - 1 else totalTimes) {
        segments.add(Date(start.time + interval * i))
    }
    return segments
}


fun FixedInPeriod.getTriggerTimeOffset(): Long {
    val triggerDateToday = triggerDateToday
    val timeMillisOffset = (triggerDateToday.time - System.currentTimeMillis())
    val nextTriggerDayOfWeekOffset = getNextTriggerDayOfWeekOffset(triggerDateToday)
    return timeMillisOffset + (nextTriggerDayOfWeekOffset * 24 * 60 * 60 * 1000)
}

fun FixedInPeriod.getNextTriggerDayOfWeekOffset(triggerDateToday: Date): Int {
    // No repeat
    if (repeat.isNo) {
        return 0
    }

    val todayWeekToday =
        Calendar.getInstance().apply { time = Date() }.get(Calendar.DAY_OF_WEEK)
    logger.d("todayWeekToday: $todayWeekToday")
    logger.d("repeat.sortedDays: ${repeat.sortedDays}")

    // Today will trigger
    val hasRepeatToday =
        repeat.sortedDays.firstOrNull { it.toCalendarInt() == todayWeekToday } != null
    val isCurrentBeforeTodayTriggerTime = System.currentTimeMillis() < triggerDateToday.time
    if (hasRepeatToday && isCurrentBeforeTodayTriggerTime) {
        return 0
    }

    // Today will not, find next day
    val nextRepeatDay =
        repeat.sortedDays.firstOrNull { it.toCalendarInt() > todayWeekToday }
    logger.d("nextRepeatDay: $nextRepeatDay")
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

sealed interface FixedSetting : Parcelable {
    data class Interval(val interval: Long) : FixedSetting {
        val displayDuration: String
            get() = DateUtils.formatDuration(
                this.interval.milliseconds.toJavaDuration()
            )

        constructor(parcel: Parcel) : this(parcel.readLong()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeLong(interval)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Interval> {
            override fun createFromParcel(parcel: Parcel): Interval {
                return Interval(parcel)
            }

            override fun newArray(size: Int): Array<Interval?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class Times(val times: Int) : FixedSetting {
        constructor(parcel: Parcel) : this(parcel.readInt()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(times)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Times> {
            override fun createFromParcel(parcel: Parcel): Times {
                return Times(parcel)
            }

            override fun newArray(size: Int): Array<Times?> {
                return arrayOfNulls(size)
            }
        }
    }
}

fun ProtoAny.toFixedBy(): FixedSetting? {
    return if (this is_ FixedByTimes::class.java) {
        FixedSetting.Times((this unpack_ FixedByTimes::class.java).times)
    } else if (this is_ FixedByInterval::class.java) {
        FixedSetting.Interval((this unpack_ FixedByInterval::class.java).interval)
    } else null
}

fun FixedSetting.toProto(): ProtoAny {
    return when (this) {
        is FixedSetting.Interval -> FixedByInterval.newBuilder().setInterval(interval).build()
        is FixedSetting.Times -> FixedByTimes.newBuilder().setTimes(times).build()
    }.pack_()
}