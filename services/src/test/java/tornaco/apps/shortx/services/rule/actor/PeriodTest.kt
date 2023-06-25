package tornaco.apps.shortx.services.rule.actor

import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.alarm.triggerDate
import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.util.logAdapter

class PeriodTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testPeriod() {
        val rp = RandomInPeriod.newBuilder()
            .setStart(TimeOfADay.newBuilder().setHour(10).setMinutes(20).setSeconds(30).build())
            .setEnd(TimeOfADay.newBuilder().setHour(12).setMinutes(33).setSeconds(44).build())
            .setRepeat(RepeatDays.newBuilder().addAllDays(listOf(WeekDay.FRIDAY)).build())
            .build()

        rp.triggerDate()
    }
}