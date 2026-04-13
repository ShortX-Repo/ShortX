package tornaco.apps.shortx.services.calendar.workday

import tornaco.apps.shortx.core.proto.condition.CalendarDayType
import java.time.LocalDate

enum class WorkdayEntryType {
    PublicHoliday,
    TransferWorkday,
}

data class WorkdayYearData(
    val year: Int,
    val region: String,
    val entries: Map<LocalDate, WorkdayEntryType>,
)

interface WorkdayYearSource {
    fun getYear(year: Int): WorkdayYearData?
}

data class ResolvedCalendarDay(
    val date: LocalDate,
    val isWorkday: Boolean,
    val isHoliday: Boolean,
    val isWeekend: Boolean,
    val isTransferWorkday: Boolean,
) {
    fun matches(dayType: CalendarDayType): Boolean {
        return when (dayType) {
            CalendarDayType.AnyDay -> true
            CalendarDayType.WorkdayOnly -> isWorkday
            CalendarDayType.HolidayOnly -> isHoliday
            CalendarDayType.WeekendOnly -> isWeekend
            CalendarDayType.TransferWorkdayOnly -> isTransferWorkday
            CalendarDayType.UNRECOGNIZED -> false
        }
    }
}