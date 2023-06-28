package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.common.timeOfADayRangeToString
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond
import java.util.Calendar
import java.util.Date

object TimeInRange : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.TimeInRange::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.TimeInRange::class.java

            val currentDate = Date()

            logger.d("check range: ${data.range.timeOfADayRangeToString()}, current: $currentDate")

            val rangeStartTime = timeOfADayToTodayDate(data.range.start)
            val rangeEndTime = timeOfADayToTodayDate(data.range.end)
            logger.d("rangeStartTime: $rangeStartTime, rangeEndTime: $rangeEndTime")

            val res =
                currentDate.time >= rangeStartTime.time && currentDate.time <= rangeEndTime.time

            if (data.isInvert) {
                !res
            } else {
                res
            }
        }
    }

    fun timeOfADayToTodayDate(timeOfADay: TimeOfADay): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar[Calendar.HOUR_OF_DAY] = timeOfADay.hour
        calendar[Calendar.MINUTE] = timeOfADay.minutes
        calendar[Calendar.SECOND] = timeOfADay.seconds
        return calendar.time
    }
}