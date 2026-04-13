package tornaco.apps.shortx.services.calendar.workday

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate

class WorkdayJsonParser {
    fun parse(json: String, requestedYear: Int? = null): WorkdayYearData {
        val root = Json.parseToJsonElement(json).jsonObject
        return when {
            root.containsKey("dates") -> parseLegacyYearPayload(root)
            root.containsKey("Years") -> parseHolidayApiPayload(root, requestedYear)
            else -> error("Unsupported workday payload")
        }
    }

    private fun parseLegacyYearPayload(root: kotlinx.serialization.json.JsonObject): WorkdayYearData {
        val year = root["year"]?.jsonPrimitive?.content?.toIntOrNull() ?: error("Missing year")
        val region = root["region"]?.jsonPrimitive?.content ?: "CN"
        val entries = buildMap {
            root["dates"]?.jsonArray?.forEach { element ->
                val item = element.jsonObject
                val dateString = item["date"]?.jsonPrimitive?.content ?: return@forEach
                val typeString = item["type"]?.jsonPrimitive?.content ?: return@forEach
                val entryType = entryTypeOf(typeString) ?: return@forEach
                put(LocalDate.parse(dateString), entryType)
            }
        }
        return WorkdayYearData(year = year, region = region, entries = entries)
    }

    private fun parseHolidayApiPayload(
        root: kotlinx.serialization.json.JsonObject,
        requestedYear: Int?,
    ): WorkdayYearData {
        val years = root["Years"]?.jsonObject ?: error("Missing Years")
        val year = requestedYear ?: years.keys.singleOrNull()?.toIntOrNull()
            ?: error("Missing requested year for multi-year workday payload")
        val holidays = years[year.toString()]?.jsonArray ?: error("Missing year $year")
        val entries = buildMap {
            holidays.forEach { element ->
                val item = element.jsonObject
                addHolidayRange(year = year, startDate = item["StartDate"]?.jsonPrimitive?.content, endDate = item["EndDate"]?.jsonPrimitive?.content)
                item["CompDays"]?.jsonArray?.forEach { compDay ->
                    val date = LocalDate.parse(compDay.jsonPrimitive.content)
                    if (date.year == year) {
                        put(date, WorkdayEntryType.TransferWorkday)
                    }
                }
            }
        }
        return WorkdayYearData(year = year, region = "CN", entries = entries)
    }

    private fun MutableMap<LocalDate, WorkdayEntryType>.addHolidayRange(
        year: Int,
        startDate: String?,
        endDate: String?,
    ) {
        val start = startDate?.let(LocalDate::parse) ?: return
        val end = endDate?.let(LocalDate::parse) ?: return
        var cursor = start
        while (!cursor.isAfter(end)) {
            if (cursor.year == year) {
                put(cursor, WorkdayEntryType.PublicHoliday)
            }
            cursor = cursor.plusDays(1)
        }
    }

    private fun entryTypeOf(typeString: String): WorkdayEntryType? {
        return when (typeString) {
            "public_holiday" -> WorkdayEntryType.PublicHoliday
            "transfer_workday" -> WorkdayEntryType.TransferWorkday
            else -> null
        }
    }
}