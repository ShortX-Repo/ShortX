package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.common.timeOfADayRangeToString
import tornaco.apps.shortx.core.common.timeOfADayToTodayDate
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond
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

            val rangeStartTime = data.range.start.timeOfADayToTodayDate()
            val rangeEndTime = data.range.end.timeOfADayToTodayDate()
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
}