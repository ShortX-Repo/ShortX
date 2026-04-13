package tornaco.apps.shortx.services.calendar.workday

import java.time.DayOfWeek
import java.time.LocalDate

class WorkdayResolver(
    private val yearSource: WorkdayYearSource,
) {
    fun resolve(date: LocalDate): ResolvedCalendarDay {
        val entryType = yearSource.getYear(date.year)?.entries?.get(date)
        val naturalWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY

        return when (entryType) {
            WorkdayEntryType.TransferWorkday -> ResolvedCalendarDay(
                date = date,
                isWorkday = true,
                isHoliday = false,
                isWeekend = false,
                isTransferWorkday = true,
            )

            WorkdayEntryType.PublicHoliday -> ResolvedCalendarDay(
                date = date,
                isWorkday = false,
                isHoliday = true,
                isWeekend = naturalWeekend,
                isTransferWorkday = false,
            )

            null -> ResolvedCalendarDay(
                date = date,
                isWorkday = !naturalWeekend,
                isHoliday = false,
                isWeekend = naturalWeekend,
                isTransferWorkday = false,
            )
        }
    }
}