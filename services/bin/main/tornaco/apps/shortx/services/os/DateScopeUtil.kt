package tornaco.apps.shortx.services.os

import tornaco.apps.shortx.core.proto.condition.TheXXTimeTodayScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


fun formatTheXXTimeTodayScope(timeRange: TheXXTimeTodayScope): String {
    val calendar = Calendar.getInstance()
    // 将第一天设置为周一
    calendar.firstDayOfWeek = Calendar.MONDAY

    return when (timeRange) {
        TheXXTimeTodayScope.Today -> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStart = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0); set(
                Calendar.MINUTE,
                0
            ); set(Calendar.SECOND, 0)
            }.time
            val todayEnd = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(
                Calendar.SECOND,
                59
            )
            }.time
            "${dateFormat.format(todayStart)} - ${dateFormat.format(todayEnd)}"
        }

        TheXXTimeTodayScope.TodayAM -> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
            val todayAMStart = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0); set(
                Calendar.MINUTE,
                0
            ); set(Calendar.SECOND, 0)
            }.time
            val todayAMEnd = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 11); set(Calendar.MINUTE, 59); set(
                Calendar.SECOND,
                59
            )
            }.time
            "${dateFormat.format(todayAMStart)} - ${dateFormat.format(todayAMEnd)}"
        }

        TheXXTimeTodayScope.TodayPM -> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
            val todayPMStart = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0); set(
                Calendar.SECOND,
                0
            )
            }.time
            val todayPMEnd = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(
                Calendar.SECOND,
                59
            )
            }.time
            "${dateFormat.format(todayPMStart)} - ${dateFormat.format(todayPMEnd)}"
        }

        TheXXTimeTodayScope.ThisWeek -> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startOfWeek = calendar.apply {
                set(
                    Calendar.DAY_OF_WEEK,
                    calendar.firstDayOfWeek
                ); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
            }.time
            val endOfWeek = calendar.apply {
                set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6); set(
                Calendar.HOUR_OF_DAY,
                23
            ); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59)
            }.time
            "${dateFormat.format(startOfWeek)} - ${dateFormat.format(endOfWeek)}"
        }

        TheXXTimeTodayScope.ThisMonth -> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startOfMonth = calendar.apply {
                set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(
                Calendar.MINUTE,
                0
            ); set(Calendar.SECOND, 0)
            }.time
            val endOfMonth = calendar.apply {
                set(
                    Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                ); set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59)
            }.time
            "${dateFormat.format(startOfMonth)} - ${dateFormat.format(endOfMonth)}"
        }

        else -> {
            "UNKNOWN"
        }
    }
}