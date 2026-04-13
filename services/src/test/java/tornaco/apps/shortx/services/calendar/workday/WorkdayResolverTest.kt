package tornaco.apps.shortx.services.calendar.workday

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import tornaco.apps.shortx.core.proto.condition.CalendarDayType
import java.time.LocalDate

class WorkdayResolverTest {
    private val source = object : WorkdayYearSource {
        override fun getYear(year: Int): WorkdayYearData? {
            return WorkdayYearData(
                year = year,
                region = "CN",
                entries = mapOf(
                    LocalDate.parse("2026-01-01") to WorkdayEntryType.PublicHoliday,
                    LocalDate.parse("2026-02-15") to WorkdayEntryType.TransferWorkday,
                )
            )
        }
    }

    private val resolver = WorkdayResolver(source)

    @Test
    fun `weekday holiday is holiday not workday`() {
        val resolved = resolver.resolve(LocalDate.parse("2026-01-01"))

        assertTrue(resolved.isHoliday)
        assertFalse(resolved.isWorkday)
        assertTrue(resolved.matches(CalendarDayType.HolidayOnly))
        assertFalse(resolved.matches(CalendarDayType.WorkdayOnly))
    }

    @Test
    fun `transfer workday on weekend matches workday and transfer workday`() {
        val resolved = resolver.resolve(LocalDate.parse("2026-02-15"))

        assertTrue(resolved.isWorkday)
        assertTrue(resolved.isTransferWorkday)
        assertFalse(resolved.isWeekend)
        assertTrue(resolved.matches(CalendarDayType.WorkdayOnly))
        assertTrue(resolved.matches(CalendarDayType.TransferWorkdayOnly))
        assertFalse(resolved.matches(CalendarDayType.WeekendOnly))
    }

    @Test
    fun `plain weekend stays weekend`() {
        val resolved = resolver.resolve(LocalDate.parse("2026-02-14"))

        assertTrue(resolved.isWeekend)
        assertFalse(resolved.isWorkday)
        assertTrue(resolved.matches(CalendarDayType.WeekendOnly))
    }
}