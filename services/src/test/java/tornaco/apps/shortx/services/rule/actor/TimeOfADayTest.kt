package tornaco.apps.shortx.services.rule.actor

import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.common.timeOfADayToTodayDate
import tornaco.apps.shortx.core.common.toTimeOfADay
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.util.logAdapter
import java.time.LocalTime

class TimeOfADayTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testTimeOfADay() {
        TimeOfADay.newBuilder().setHour(12).setMinutes(34).setSeconds(11).build()
            .timeOfADayToTodayDate().apply {
                println(toString())
            }
    }

    @Test
    fun testTimeOfADayToTodayDate() {
        println(LocalTime.of(12, 22, 33).toTimeOfADay().timeOfADayToTodayDate())
    }
}
