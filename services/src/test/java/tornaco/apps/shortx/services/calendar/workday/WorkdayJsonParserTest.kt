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
    fun `parse expands holiday api ranges and comp days for requested year`() {
        val json = """
            {
              "Name": "ShuYZ中国节假日",
              "Years": {
                "2023": [
                  {
                    "Name": "元旦",
                    "StartDate": "2022-12-31",
                    "EndDate": "2023-01-02",
                    "Duration": 3,
                    "CompDays": []
                  },
                  {
                    "Name": "春节",
                    "StartDate": "2023-01-21",
                    "EndDate": "2023-01-27",
                    "Duration": 7,
                    "CompDays": ["2023-01-28", "2023-01-29"]
                  }
                ]
              }
            }
        """.trimIndent()

        val data = WorkdayJsonParser().parse(json, requestedYear = 2023)

        assertEquals(2023, data.year)
        assertEquals("CN", data.region)
        assertEquals(11, data.entries.size)
        assertEquals(WorkdayEntryType.PublicHoliday, data.entries[LocalDate.parse("2023-01-01")])
        assertEquals(WorkdayEntryType.PublicHoliday, data.entries[LocalDate.parse("2023-01-02")])
        assertEquals(null, data.entries[LocalDate.parse("2022-12-31")])
        assertEquals(WorkdayEntryType.PublicHoliday, data.entries[LocalDate.parse("2023-01-27")])
        assertEquals(WorkdayEntryType.TransferWorkday, data.entries[LocalDate.parse("2023-01-28")])
        assertEquals(WorkdayEntryType.TransferWorkday, data.entries[LocalDate.parse("2023-01-29")])
    }

    @Test
    fun `parse holiday api requires requested year when payload has multiple years`() {
        val json = """
            {
              "Name": "ShuYZ中国节假日",
              "Years": {
                "2025": [],
                "2026": []
              }
            }
        """.trimIndent()

        val error = runCatching { WorkdayJsonParser().parse(json) }.exceptionOrNull()

        assertTrue(error != null)
    }

  @Test
  fun `parse single-year holiday api payload without requested year`() {
    val json = """
      {
        "Name": "ShuYZ中国节假日",
        "Version": "1.0",
        "Years": {
        "2027": []
        }
      }
    """.trimIndent()

    val data = WorkdayJsonParser().parse(json)

    assertEquals(2027, data.year)
    assertEquals("CN", data.region)
    assertTrue(data.entries.isEmpty())
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