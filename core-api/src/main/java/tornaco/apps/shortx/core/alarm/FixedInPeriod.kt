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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

private val logger = Logger("FixedInPeriodExt")
private const val MAX_SCHEDULE_LOOKAHEAD_DAYS = 370

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
    return getTriggerTimeOffset(now = LocalDateTime.now())
}

fun FixedInPeriod.getTriggerTimeOffset(
    dateFilter: ((LocalDate) -> Boolean)? = null,
    now: LocalDateTime = LocalDateTime.now(),
): Long {
    val nextTrigger = nextTriggerDateTime(now, dateFilter) ?: return -1L
    return java.time.Duration.between(now, nextTrigger).toMillis()
}

internal fun FixedInPeriod.nextTriggerDateTime(now: LocalDateTime): LocalDateTime? {
    return nextTriggerDateTime(now, null)
}

internal fun FixedInPeriod.nextTriggerDateTime(
    now: LocalDateTime,
    dateFilter: ((LocalDate) -> Boolean)? = null,
): LocalDateTime? {
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

        val segments = triggerDateTimesOn(candidateDate)
        val nextTrigger = if (dayOffset == 0) {
            segments.firstOrNull { it.isAfter(now) }
        } else {
            segments.firstOrNull()
        }

        if (nextTrigger != null) {
            return nextTrigger
        }
    }

    return null
}

internal fun FixedInPeriod.triggerDateTimesOn(date: LocalDate): List<LocalDateTime> {
    val zoneId = ZoneId.systemDefault()
    val startDate = Date.from(
        date.atTime(start.hour, start.minutes, start.seconds)
            .atZone(zoneId)
            .toInstant()
    )
    val endDate = Date.from(
        date.atTime(end.hour, end.minutes, end.seconds)
            .atZone(zoneId)
            .toInstant()
    )

    return getTriggerTimeSegments(fixedBy, startDate, endDate)
        .map { LocalDateTime.ofInstant(it.toInstant(), zoneId) }
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