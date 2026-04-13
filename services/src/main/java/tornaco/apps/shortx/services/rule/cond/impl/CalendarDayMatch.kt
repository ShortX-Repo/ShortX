package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.calendar.workday.WorkdayRepository
import tornaco.apps.shortx.services.calendar.workday.WorkdayResolver
import tornaco.apps.shortx.services.rule.cond.Cond
import java.time.LocalDate

object CalendarDayMatch : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.CalendarDayMatch::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.CalendarDayMatch::class.java
            val resolver = WorkdayResolver(WorkdayRepository(context))
            val resolved = resolver.resolve(LocalDate.now())
            val res = resolved.matches(data.dayType)
            if (data.isInvert) !res else res
        }.toResult()
    }
}