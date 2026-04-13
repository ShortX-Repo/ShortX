package tornaco.apps.shortx.core.alarm

import org.junit.Assert.assertEquals
import org.junit.Test
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import java.time.LocalDateTime

class AlarmScheduleTest {
    @Test
    fun oneShotAlarmTriggersLaterToday() {
        // 单次 Alarm：今天 10:00 触发，当前时间是今天 09:30。
        // 预期还有 30 分钟触发，不会跨天。
        val alarm = alarmAt(hour = 10)
        val now = LocalDateTime.of(2026, 4, 13, 9, 30, 0)

        val offset = alarm.getTriggerTimeOffset(now = now)

        assertEquals(30L * 60L * 1000L, offset)
    }

    @Test
    fun oneShotAlarmDoesNotRescheduleAfterTodayIsMissed() {
        // 单次 Alarm：今天 10:00 触发，但当前已经是 10:30。
        // 单次任务不应该自动顺延到明天，所以返回 -1。
        val alarm = alarmAt(hour = 10)
        val now = LocalDateTime.of(2026, 4, 13, 10, 30, 0)

        val offset = alarm.getTriggerTimeOffset(now = now)

        assertEquals(-1L, offset)
    }

    @Test
    fun repeatedAlarmTriggersTodayWhenWeekdayMatchesAndTimeNotPassed() {
        // 重复 Alarm：配置为每周一 10:00，当前正好是周一 09:30。
        // 因为今天命中重复日且还没过点，所以应该今天 10:00 触发。
        val alarm = alarmAt(
            hour = 10,
            repeatDays = listOf(WeekDay.MONDAY)
        )
        val now = LocalDateTime.of(2026, 4, 13, 9, 30, 0)

        val offset = alarm.getTriggerTimeOffset(now = now)

        assertEquals(30L * 60L * 1000L, offset)
    }

    @Test
    fun repeatedAlarmMovesToNextConfiguredWeekdayWhenTodayIsMissed() {
        // 重复 Alarm：配置周一、周三 10:00，当前是周一 10:30。
        // 今天虽然是命中的重复日，但已经过点，所以应该跳到周三 10:00。
        val alarm = alarmAt(
            hour = 10,
            repeatDays = listOf(WeekDay.MONDAY, WeekDay.WEDNESDAY)
        )
        val now = LocalDateTime.of(2026, 4, 13, 10, 30, 0)

        val offset = alarm.getTriggerTimeOffset(now = now)

        assertEquals(47L * 60L * 60L * 1000L + 30L * 60L * 1000L, offset)
    }

    @Test
    fun repeatedAlarmSelectsNearestConfiguredWeekday() {
        // 重复 Alarm：配置周二、周四 10:00，当前是周一 09:30。
        // 应该优先选择最近的周二 10:00，而不是更远的周四。
        val alarm = alarmAt(
            hour = 10,
            repeatDays = listOf(WeekDay.TUESDAY, WeekDay.THURSDAY)
        )
        val now = LocalDateTime.of(2026, 4, 13, 9, 30, 0)

        val offset = alarm.getTriggerTimeOffset(now = now)

        assertEquals(24L * 60L * 60L * 1000L + 30L * 60L * 1000L, offset)
    }

    @Test
    fun repeatedAlarmWrapsToNextWeekWhenOnlyEarlierWeekdayIsConfigured() {
        // 重复 Alarm：只配置每周一 10:00，当前时间是周二 10:30。
        // 本周的周一已经过去，只能滚到下周一 10:00。
        val alarm = alarmAt(
            hour = 10,
            repeatDays = listOf(WeekDay.MONDAY)
        )
        val now = LocalDateTime.of(2026, 4, 14, 10, 30, 0)

        val offset = alarm.getTriggerTimeOffset(now = now)

        assertEquals(143L * 60L * 60L * 1000L + 30L * 60L * 1000L, offset)
    }

    @Test
    fun repeatedAlarmWithEverydayScheduleMovesToTomorrowAfterTodayIsMissed() {
        // 重复 Alarm：配置为每天 10:00，当前是今天 10:30。
        // 因为今天已经过点，所以应该滚到明天 10:00。
        val alarm = alarmAt(
            hour = 10,
            repeatDays = listOf(
                WeekDay.SUNDAY,
                WeekDay.MONDAY,
                WeekDay.TUESDAY,
                WeekDay.WEDNESDAY,
                WeekDay.THURSDAY,
                WeekDay.FRIDAY,
                WeekDay.SATURDAY,
            )
        )
        val now = LocalDateTime.of(2026, 4, 13, 10, 30, 0)

        val offset = alarm.getTriggerTimeOffset(now = now)

        assertEquals(23L * 60L * 60L * 1000L + 30L * 60L * 1000L, offset)
    }

    private fun alarmAt(
        hour: Int,
        minute: Int = 0,
        second: Int = 0,
        repeatDays: List<WeekDay> = emptyList(),
    ): Alarm {
        return Alarm.newBuilder()
            .setTriggerAt(
                TimeOfADay.newBuilder()
                    .setHour(hour)
                    .setMinutes(minute)
                    .setSeconds(second)
                    .build()
            )
            .setRepeat(
                RepeatDays.newBuilder()
                    .addAllDays(repeatDays)
                    .build()
            )
            .build()
    }
}