package tornaco.apps.shortx.services.calendar.workday

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate

class WorkdayJsonParser {
    fun parse(json: String): WorkdayYearData {
        val root = Json.parseToJsonElement(json).jsonObject
        val year = root["year"]?.jsonPrimitive?.content?.toIntOrNull() ?: error("Missing year")
        val region = root["region"]?.jsonPrimitive?.content ?: "CN"
        val entries = buildMap {
            root["dates"]?.jsonArray?.forEach { element ->
                val item = element.jsonObject
                val dateString = item["date"]?.jsonPrimitive?.content ?: return@forEach
                val typeString = item["type"]?.jsonPrimitive?.content ?: return@forEach
                val entryType = when (typeString) {
                    "public_holiday" -> WorkdayEntryType.PublicHoliday
                    "transfer_workday" -> WorkdayEntryType.TransferWorkday
                    else -> null
                } ?: return@forEach
                put(LocalDate.parse(dateString), entryType)
            }
        }
        return WorkdayYearData(year = year, region = region, entries = entries)
    }
}