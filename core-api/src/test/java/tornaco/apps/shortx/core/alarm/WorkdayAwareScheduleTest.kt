package tornaco.apps.shortx.core.alarm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import java.time.LocalDate
import java.time.LocalDateTime

class WorkdayAwareScheduleTest {
    @Test
    fun repeatedAlarmSkipsNonMatchingCalendarDays() {
        val alarm = Alarm.newBuilder()
            .setTriggerAt(TimeOfADay.newBuilder().setHour(9).setMinutes(0).setSeconds(0).build())
            .setRepeat(
                RepeatDays.newBuilder()
                    .addAllDays(
                        listOf(
                            WeekDay.MONDAY,
                            WeekDay.TUESDAY,
                            WeekDay.WEDNESDAY,
                            WeekDay.THURSDAY,
                            WeekDay.FRIDAY,
                        )
                    )
                    .build()
            )
            .build()

        val now = LocalDateTime.of(2026, 4, 10, 8, 0, 0)
        val offset = alarm.getTriggerTimeOffset(
            dateFilter = { it == LocalDate.of(2026, 4, 13) },
            now = now,
        )

        assertEquals(73L * 60L * 60L * 1000L, offset)
    }

    @Test
    fun randomPeriodRepeatStartsSearchingFromTomorrow() {
        val period = RandomInPeriod.newBuilder()
            .setStart(TimeOfADay.newBuilder().setHour(9).setMinutes(0).setSeconds(0).build())
            .setEnd(TimeOfADay.newBuilder().setHour(10).setMinutes(0).setSeconds(0).build())
            .setRepeat(
                RepeatDays.newBuilder()
                    .addAllDays(
                        listOf(
                            WeekDay.MONDAY,
                            WeekDay.TUESDAY,
                            WeekDay.WEDNESDAY,
                            WeekDay.THURSDAY,
                            WeekDay.FRIDAY,
                        )
                    )
                    .build()
            )
            .build()

        val now = LocalDateTime.of(2026, 4, 10, 9, 30, 0)
        val offset = period.getTriggerTimeOffsetForRepeat(
            dateFilter = { it == LocalDate.of(2026, 4, 13) },
            now = now,
        )

        val minOffset = 71L * 60L * 60L * 1000L + 30L * 60L * 1000L
        val maxOffset = 72L * 60L * 60L * 1000L + 30L * 60L * 1000L
        assertTrue(offset in minOffset..maxOffset)
    }
}
