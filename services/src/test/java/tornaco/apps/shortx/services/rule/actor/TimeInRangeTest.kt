package tornaco.apps.shortx.services.rule.actor

import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.rule.cond.impl.TimeInRange

class TimeInRangeTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testTimeOfADay() {
        TimeInRange.timeOfADayToTodayDate(
            TimeOfADay.newBuilder().setHour(12).setMinutes(34).setSeconds(11).build()
        ).apply {
            println(toString())
        }
    }
}
