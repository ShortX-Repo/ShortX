package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.alarm.getNextTriggerTimeFromSegments
import tornaco.apps.shortx.core.alarm.getTriggerTimeSegmentsByInterval
import tornaco.apps.shortx.core.alarm.getTriggerTimeSegmentsByTimes
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.logAdapter
import java.util.Calendar as JCalendar
import java.util.Date
import kotlin.time.Duration.Companion.minutes

class FixedInPeriodTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun getTriggerTimeSegmentsByIntervalTest() {
        // 6min/c in 30min
        val start = JCalendar.getInstance().apply {
            time = Date()
            set(JCalendar.HOUR_OF_DAY, 12)
            set(JCalendar.MINUTE, 0)
            set(JCalendar.SECOND, 0)
        }.time.time

        val segments = getTriggerTimeSegmentsByInterval(
            interval = 6.minutes.inWholeMilliseconds,
            start = Date(start),
            end = Date(start + 30.minutes.inWholeMilliseconds),
            inclusive = false
        )

        println(segments.joinToString(System.lineSeparator()))
        Assert.assertEquals(5, segments.size)
        Assert.assertEquals(listOf(
            "12:06:00",
            "12:12:00",
            "12:18:00",
            "12:24:00",
            "12:30:00",
        ), segments.map { DateUtils.formatHHMMSS(it.time) })


        val segmentsInclusive = getTriggerTimeSegmentsByInterval(
            interval = 6.minutes.inWholeMilliseconds,
            start = Date(start),
            end = Date(start + 30.minutes.inWholeMilliseconds),
            inclusive = true
        )

        println(segmentsInclusive.joinToString(System.lineSeparator()))
        Assert.assertEquals(6, segmentsInclusive.size)
        Assert.assertEquals(listOf(
            "12:00:00",
            "12:06:00",
            "12:12:00",
            "12:18:00",
            "12:24:00",
            "12:30:00",
        ), segmentsInclusive.map { DateUtils.formatHHMMSS(it.time) })


        val segmentsOutOfBound = getTriggerTimeSegmentsByInterval(
            interval = 8.minutes.inWholeMilliseconds,
            start = Date(start),
            end = Date(start + 30.minutes.inWholeMilliseconds),
            inclusive = false
        )

        println(segmentsOutOfBound.joinToString(System.lineSeparator()))
        Assert.assertEquals(3, segmentsOutOfBound.size)
        Assert.assertEquals(listOf(
            "12:08:00",
            "12:16:00",
            "12:24:00",
        ), segmentsOutOfBound.map { DateUtils.formatHHMMSS(it.time) })

    }


    @Test
    fun getTriggerTimeSegmentsByTimesTest() {
        // 6times in 30min
        val start = JCalendar.getInstance().apply {
            time = Date()
            set(JCalendar.HOUR_OF_DAY, 12)
            set(JCalendar.MINUTE, 0)
            set(JCalendar.SECOND, 0)
        }.time.time

        val segmentsZero = getTriggerTimeSegmentsByTimes(
            totalTimes = 0,
            start = Date(start),
            end = Date(start + 30.minutes.inWholeMilliseconds),
            startInclusive = false
        )
        println(segmentsZero.joinToString(System.lineSeparator()))
        Assert.assertEquals(0, segmentsZero.size)

        val segments = getTriggerTimeSegmentsByTimes(
            totalTimes = 6,
            start = Date(start),
            end = Date(start + 30.minutes.inWholeMilliseconds),
            startInclusive = false
        )

        println(segments.joinToString(System.lineSeparator()))
        Assert.assertEquals(6, segments.size)
        Assert.assertEquals(listOf(
            "12:05:00",
            "12:10:00",
            "12:15:00",
            "12:20:00",
            "12:25:00",
            "12:30:00",
        ), segments.map { DateUtils.formatHHMMSS(it.time) })


        val segmentsStartInclusive = getTriggerTimeSegmentsByTimes(
            totalTimes = 6,
            start = Date(start),
            end = Date(start + 30.minutes.inWholeMilliseconds),
            startInclusive = true
        )

        println(segmentsStartInclusive.joinToString(System.lineSeparator()))
        Assert.assertEquals(6, segmentsStartInclusive.size)
        Assert.assertEquals(listOf(
            "12:00:00",
            "12:06:00",
            "12:12:00",
            "12:18:00",
            "12:24:00",
            "12:30:00",
        ), segmentsStartInclusive.map { DateUtils.formatHHMMSS(it.time) })


        val segmentsStartInclusive7 = getTriggerTimeSegmentsByTimes(
            totalTimes = 7,
            start = Date(start),
            end = Date(start + 30.minutes.inWholeMilliseconds),
            startInclusive = true
        )

        println(segmentsStartInclusive7.joinToString(System.lineSeparator()))
        Assert.assertEquals(7, segmentsStartInclusive7.size)
        Assert.assertEquals(listOf(
            "12:00:00",
            "12:05:00",
            "12:10:00",
            "12:15:00",
            "12:20:00",
            "12:25:00",
            "12:30:00",
        ), segmentsStartInclusive7.map { DateUtils.formatHHMMSS(it.time) })


        val currentDate = JCalendar.getInstance().apply {
            time = Date()
            set(JCalendar.HOUR_OF_DAY, 12)
            set(JCalendar.MINUTE, 11)
            set(JCalendar.SECOND, 32)
        }.time
        val triggerTime = getNextTriggerTimeFromSegments(currentDate, segmentsStartInclusive7)
        // should be 12:15:00
        println("triggerTime: $triggerTime")
        Assert.assertEquals("12:15:00", DateUtils.formatHHMMSS(triggerTime.time))

        val currentDateMissed = JCalendar.getInstance().apply {
            time = Date()
            set(JCalendar.HOUR_OF_DAY, 12)
            set(JCalendar.MINUTE, 40)
            set(JCalendar.SECOND, 32)
        }.time
        val triggerTimeMissed =
            getNextTriggerTimeFromSegments(currentDateMissed, segmentsStartInclusive7)
        // should be 12:00:00
        println("triggerTimeMissed: $triggerTimeMissed")
        Assert.assertEquals("12:00:00", DateUtils.formatHHMMSS(triggerTimeMissed.time))

        val currentDateEarly = JCalendar.getInstance().apply {
            time = Date()
            set(JCalendar.HOUR_OF_DAY, 11)
            set(JCalendar.MINUTE, 56)
            set(JCalendar.SECOND, 33)
        }.time
        val triggerTimeEarly = getNextTriggerTimeFromSegments(currentDateEarly, segmentsStartInclusive7)
        // should be 12:00:00
        println("triggerTimeEarly: $triggerTimeEarly")
        Assert.assertEquals("12:00:00", DateUtils.formatHHMMSS(triggerTimeEarly.time))
    }
}