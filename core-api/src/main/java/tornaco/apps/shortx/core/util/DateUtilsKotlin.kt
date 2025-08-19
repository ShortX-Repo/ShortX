package tornaco.apps.shortx.core.util

import java.util.Locale

object DateUtilsKotlin {
    fun formatDuration(durationInMillis: Long): String {
        val totalSeconds = durationInMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            // 显示为 "hh:mm:ss"
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            // 显示为 "mm:ss"
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}