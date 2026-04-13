package tornaco.apps.shortx.services.calendar.workday

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class WorkdayJsonParserTest {
    @Test
    fun `parse keeps holiday and transfer workday entries`() {
        val json = """
            {
              "year": 2026,
              "region": "CN",
              "dates": [
                {"date": "2026-01-01", "type": "public_holiday"},
                {"date": "2026-02-15", "type": "transfer_workday"},
                {"date": "2026-03-01", "type": "ignored"}
              ]
            }
        """.trimIndent()

        val data = WorkdayJsonParser().parse(json)

        assertEquals(2026, data.year)
        assertEquals("CN", data.region)
        assertEquals(2, data.entries.size)
        assertEquals(WorkdayEntryType.PublicHoliday, data.entries[LocalDate.parse("2026-01-01")])
        assertEquals(WorkdayEntryType.TransferWorkday, data.entries[LocalDate.parse("2026-02-15")])
    }

    @Test
    fun `parse fails when year missing`() {
        val json = """
            {
              "region": "CN",
              "dates": []
            }
        """.trimIndent()

        val error = runCatching { WorkdayJsonParser().parse(json) }.exceptionOrNull()

        assertTrue(error != null)
    }
}