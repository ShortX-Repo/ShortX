package tornaco.apps.shortx.core.alarm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.FixedByInterval
import tornaco.apps.shortx.core.proto.fact.FixedByTimes
import tornaco.apps.shortx.core.proto.fact.FixedInPeriod
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.logAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale

class FixedInPeriodScheduleTest {
    @Before
    fun installLogger() {
        logAdapter = { _, _, _ -> }
    }

    @Test
    fun fixedByTimesSplitsPeriodIntoExpectedSegments() {
        // 固定次数切段：15:00 到 20:00，总共触发 4 次。
        // 起点和终点都算在内，所以应该被均分为 4 个触发点。
        val period = fixedPeriod(
            startHour = 15,
            endHour = 20,
            fixedBy = FixedByTimes.newBuilder().setTimes(4).build().pack_(),
        )

        val segments = period.triggerDateTimesOn(LocalDate.of(2026, 4, 13))

        assertEquals(
            listOf("15:00:00", "16:40:00", "18:20:00", "20:00:00"),
            segments.map { it.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString().normalizeTime() }
        )
    }

    @Test
    fun fixedByIntervalSplitsPeriodIntoExpectedSegments() {
        // 固定间隔切段：15:00 到 18:00，间隔 1 小时。
        // 预期触发点就是 15:00、16:00、17:00、18:00。
        val period = fixedPeriod(
            startHour = 15,
            endHour = 18,
            fixedBy = FixedByInterval.newBuilder().setInterval(60L * 60L * 1000L).build().pack_(),
        )

        val segments = period.triggerDateTimesOn(LocalDate.of(2026, 4, 13))

        assertEquals(
            listOf("15:00:00", "16:00:00", "17:00:00", "18:00:00"),
            segments.map { it.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString().normalizeTime() }
        )
    }

    @Test
    fun fixedByTimesOneKeepsOnlyStartBoundary() {
        // 固定次数为 1：表示整个时间段内只触发一次。
        // 当前实现下只保留起始边界，也就是 15:00。
        val period = fixedPeriod(
            startHour = 15,
            endHour = 20,
            fixedBy = FixedByTimes.newBuilder().setTimes(1).build().pack_(),
        )

        val segments = period.triggerDateTimesOn(LocalDate.of(2026, 4, 13))

        assertEquals(
            listOf("15:00:00"),
            segments.map { it.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString().normalizeTime() }
        )
    }

    @Test
    fun fixedByIntervalLongerThanPeriodKeepsOnlyStartBoundary() {
        // 固定间隔大于整个时间段：15:00 到 16:00，但间隔是 2 小时。
        // 无法再切出第二个点，所以只保留起点 15:00。
        val period = fixedPeriod(
            startHour = 15,
            endHour = 16,
            fixedBy = FixedByInterval.newBuilder().setInterval(2L * 60L * 60L * 1000L).build().pack_(),
        )

        val segments = period.triggerDateTimesOn(LocalDate.of(2026, 4, 13))

        assertEquals(
            listOf("15:00:00"),
            segments.map { it.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString().normalizeTime() }
        )
    }

    @Test
    fun fixedByTimesSupportsNonWholeHourBoundaries() {
        // 非整点切段：15:15 到 16:45，按 3 次切分。
        // 预期仍然按整段均分，得到 15:15、16:00、16:45。
        val period = fixedPeriod(
            startHour = 15,
            startMinute = 15,
            endHour = 16,
            endMinute = 45,
            fixedBy = FixedByTimes.newBuilder().setTimes(3).build().pack_(),
        )

        val segments = period.triggerDateTimesOn(LocalDate.of(2026, 4, 13))

        assertEquals(
            listOf("15:15:00", "16:00:00", "16:45:00"),
            segments.map { it.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString().normalizeTime() }
        )
    }

    @Test
    fun fixedPeriodChoosesNextSegmentWithinToday() {
        // 今天还在时间段内：15:00 到 20:00，4 个触发点。
        // 当前时间是 16:00，那么下一个触发点应该是 16:40，而不是回到 15:00。
        val period = fixedPeriod(
            startHour = 15,
            endHour = 20,
            fixedBy = FixedByTimes.newBuilder().setTimes(4).build().pack_(),
            repeatDays = listOf(WeekDay.MONDAY),
        )

        val nextTrigger = period.nextTriggerDateTime(
            now = LocalDateTime.of(2026, 4, 13, 16, 0, 0)
        )

        assertNotNull(nextTrigger)
        assertEquals(LocalDateTime.of(2026, 4, 13, 16, 40, 0), nextTrigger)
    }

    @Test
    fun fixedPeriodMovesToNextRepeatDayWhenTodayIsMissed() {
        // 配置为周一、周三重复，时间段是 15:00 到 20:00。
        // 当前是周一 21:00，说明今天整段都错过了，所以应该跳到周三的第一个触发点 15:00。
        val period = fixedPeriod(
            startHour = 15,
            endHour = 20,
            fixedBy = FixedByTimes.newBuilder().setTimes(4).build().pack_(),
            repeatDays = listOf(WeekDay.MONDAY, WeekDay.WEDNESDAY),
        )

        val nextTrigger = period.nextTriggerDateTime(
            now = LocalDateTime.of(2026, 4, 13, 21, 0, 0)
        )

        assertNotNull(nextTrigger)
        assertEquals(LocalDateTime.of(2026, 4, 15, 15, 0, 0), nextTrigger)
    }

    @Test
    fun nonRepeatingFixedPeriodReturnsNullAfterWholePeriodIsMissed() {
        // 非重复时间段：今天 15:00 到 20:00。
        // 当前已经 21:00，整段都错过了，因此不应该再补排，返回 null。
        val period = fixedPeriod(
            startHour = 15,
            endHour = 20,
            fixedBy = FixedByInterval.newBuilder().setInterval(60L * 60L * 1000L).build().pack_(),
        )

        val nextTrigger = period.nextTriggerDateTime(
            now = LocalDateTime.of(2026, 4, 13, 21, 0, 0)
        )

        assertEquals(null, nextTrigger)
    }

    @Test
    fun fixedPeriodWrapsToNextWeekForSingleRepeatDay() {
        // 只配置每周一重复，当前时间是周二 21:00。
        // 本周的周一已经过去，所以应该滚到下周一，并取该天的第一个触发点 15:00。
        val period = fixedPeriod(
            startHour = 15,
            endHour = 20,
            fixedBy = FixedByInterval.newBuilder().setInterval(60L * 60L * 1000L).build().pack_(),
            repeatDays = listOf(WeekDay.MONDAY),
        )

        val nextTrigger = period.nextTriggerDateTime(
            now = LocalDateTime.of(2026, 4, 14, 21, 0, 0)
        )

        assertNotNull(nextTrigger)
        assertEquals(LocalDateTime.of(2026, 4, 20, 15, 0, 0), nextTrigger)
    }

    @Test
    fun fixedPeriodSkipsNonMatchingCalendarDaysWhenDateFilterIsPresent() {
        // 叠加日期过滤：工作日重复时间段从周五开始，但只有下周一允许命中。
        // 应该直接跳过中间所有不满足 dateFilter 的日期。
        val period = fixedPeriod(
            startHour = 15,
            endHour = 20,
            fixedBy = FixedByInterval.newBuilder().setInterval(60L * 60L * 1000L).build().pack_(),
            repeatDays = listOf(
                WeekDay.MONDAY,
                WeekDay.TUESDAY,
                WeekDay.WEDNESDAY,
                WeekDay.THURSDAY,
                WeekDay.FRIDAY,
            ),
        )

        val now = LocalDateTime.of(2026, 4, 10, 16, 0, 0)
        val offset = period.getTriggerTimeOffset(
            dateFilter = { it == LocalDate.of(2026, 4, 13) },
            now = now,
        )

        val minOffset = 71L * 60L * 60L * 1000L
        val maxOffset = 76L * 60L * 60L * 1000L
        assertTrue(offset in minOffset..maxOffset)
    }

    private fun fixedPeriod(
        startHour: Int,
        startMinute: Int = 0,
        endHour: Int,
        endMinute: Int = 0,
        fixedBy: ProtoAny,
        repeatDays: List<WeekDay> = emptyList(),
    ): FixedInPeriod {
        return FixedInPeriod.newBuilder()
            .setStart(
                TimeOfADay.newBuilder()
                    .setHour(startHour)
                    .setMinutes(startMinute)
                    .setSeconds(0)
                    .build()
            )
            .setEnd(
                TimeOfADay.newBuilder()
                    .setHour(endHour)
                    .setMinutes(endMinute)
                    .setSeconds(0)
                    .build()
            )
            .setRepeat(
                RepeatDays.newBuilder()
                    .addAllDays(repeatDays)
                    .build()
            )
                .setFixedBy(fixedBy)
            .build()
    }

    private fun String.normalizeTime(): String {
        return if (length == 5) "$this:00" else lowercase(Locale.ENGLISH)
    }
}