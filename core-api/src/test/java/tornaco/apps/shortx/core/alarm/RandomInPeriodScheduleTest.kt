package tornaco.apps.shortx.core.alarm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import java.time.LocalDate
import java.time.LocalDateTime

class RandomInPeriodScheduleTest {
    @Test
    fun oneShotPeriodTriggersWithinRemainingWindowToday() {
        // 单次随机时间段：今天 09:00 到 10:00，当前是 09:30。
        // 触发时间必须落在今天剩余窗口内，所以 offset 应在 0 到 30 分钟之间。
        val period = period(
            startHour = 9,
            endHour = 10,
        )

        val now = LocalDateTime.of(2026, 4, 13, 9, 30, 0)
        val offset = period.getTriggerTimeOffset(now = now)

        assertTrue(offset in 1L..(30L * 60L * 1000L))
    }

    @Test
    fun oneShotPeriodReturnsNegativeWhenWindowAlreadyMissed() {
        // 单次随机时间段：今天 09:00 到 10:00，当前已是 10:30。
        // 今天窗口已完全错过，单次任务不应顺延到明天，所以返回 -1。
        val period = period(
            startHour = 9,
            endHour = 10,
        )

        val now = LocalDateTime.of(2026, 4, 13, 10, 30, 0)
        val offset = period.getTriggerTimeOffset(now = now)

        assertEquals(-1L, offset)
    }

    @Test
    fun repeatedPeriodUsesTodayWhenTodayMatchesAndWindowStillOpen() {
        // 重复随机时间段：配置每周一，窗口 09:00 到 10:00，当前是周一 09:30。
        // 应该仍在今天窗口内随机，offset 必须在 0 到 30 分钟之间。
        val period = period(
            startHour = 9,
            endHour = 10,
            repeatDays = listOf(WeekDay.MONDAY),
        )

        val now = LocalDateTime.of(2026, 4, 13, 9, 30, 0)
        val offset = period.getTriggerTimeOffset(now = now)

        assertTrue(offset in 1L..(30L * 60L * 1000L))
    }

    @Test
    fun repeatedPeriodMovesToNextConfiguredWeekdayAfterTodayWindowIsMissed() {
        // 重复随机时间段：配置周一、周三，窗口 09:00 到 10:00，当前是周一 10:30。
        // 今天窗口已错过，所以应跳到周三窗口内随机。
        val period = period(
            startHour = 9,
            endHour = 10,
            repeatDays = listOf(WeekDay.MONDAY, WeekDay.WEDNESDAY),
        )

        val now = LocalDateTime.of(2026, 4, 13, 10, 30, 0)
        val offset = period.getTriggerTimeOffset(now = now)

        val minOffset = 46L * 60L * 60L * 1000L + 30L * 60L * 1000L
        val maxOffset = 47L * 60L * 60L * 1000L + 30L * 60L * 1000L
        assertTrue(offset in minOffset..maxOffset)
    }

    @Test
    fun repeatedPeriodWrapsToNextWeekWhenOnlyPastWeekdayIsConfigured() {
        // 重复随机时间段：只配置每周一，窗口 09:00 到 10:00，当前是周二 10:30。
        // 只能滚到下周一，并在下周一窗口内随机。
        val period = period(
            startHour = 9,
            endHour = 10,
            repeatDays = listOf(WeekDay.MONDAY),
        )

        val now = LocalDateTime.of(2026, 4, 14, 10, 30, 0)
        val offset = period.getTriggerTimeOffset(now = now)

        val minOffset = 142L * 60L * 60L * 1000L + 30L * 60L * 1000L
        val maxOffset = 143L * 60L * 60L * 1000L + 30L * 60L * 1000L
        assertTrue(offset in minOffset..maxOffset)
    }

    @Test
    fun repeatedPeriodForRepeatAlwaysStartsSearchingFromTomorrow() {
        // repeat 调度路径：当前仍在今天窗口中，但下一次重复不应继续落在今天，
        // 而是从明天开始找第一个命中的重复日。
        val period = period(
            startHour = 9,
            endHour = 10,
            repeatDays = listOf(WeekDay.MONDAY, WeekDay.TUESDAY),
        )

        val now = LocalDateTime.of(2026, 4, 13, 9, 30, 0)
        val offset = period.getTriggerTimeOffsetForRepeat(now = now)

        val minOffset = 23L * 60L * 60L * 1000L + 30L * 60L * 1000L
        val maxOffset = 24L * 60L * 60L * 1000L + 30L * 60L * 1000L
        assertTrue(offset in minOffset..maxOffset)
    }

    @Test
    fun repeatedPeriodSkipsNonMatchingCalendarDaysWhenDateFilterIsPresent() {
        // 叠加日期过滤：工作日重复窗口从周五开始，但只有下周一允许命中。
        // 应直接跳过中间所有不满足 dateFilter 的日期。
        val period = period(
            startHour = 9,
            endHour = 10,
            repeatDays = listOf(
                WeekDay.MONDAY,
                WeekDay.TUESDAY,
                WeekDay.WEDNESDAY,
                WeekDay.THURSDAY,
                WeekDay.FRIDAY,
            ),
        )

        val now = LocalDateTime.of(2026, 4, 10, 9, 30, 0)
        val offset = period.getTriggerTimeOffsetForRepeat(
            dateFilter = { it == LocalDate.of(2026, 4, 13) },
            now = now,
        )

        val minOffset = 71L * 60L * 60L * 1000L + 30L * 60L * 1000L
        val maxOffset = 72L * 60L * 60L * 1000L + 30L * 60L * 1000L
        assertTrue(offset in minOffset..maxOffset)
    }

    private fun period(
        startHour: Int,
        endHour: Int,
        repeatDays: List<WeekDay> = emptyList(),
    ): RandomInPeriod {
        return RandomInPeriod.newBuilder()
            .setStart(TimeOfADay.newBuilder().setHour(startHour).setMinutes(0).setSeconds(0).build())
            .setEnd(TimeOfADay.newBuilder().setHour(endHour).setMinutes(0).setSeconds(0).build())
            .setRepeat(RepeatDays.newBuilder().addAllDays(repeatDays).build())
            .build()
    }
}