package tornaco.apps.shortx.ui.changelog

import android.content.Context
import com.google.gson.Gson
import tornaco.apps.shortx.ui.util.AssetUtils

data class ChangeLog(
    val items: List<ChangeLogItem> = emptyList(),
)

data class ChangeLogItem(
    val versionName: String = "",
    val versionCode: Int = 0,
    val date: String = "",
    val sections: List<ChangeLogSection> = emptyList(),
)

data class ChangeLogSection(
    val title: String = "",
    val body: String = "",
    val bullets: List<String> = emptyList(),
)

object ChangeLogRepository {
    private const val CHANGE_LOG_ASSET = "changelog/changelog.json"
    private val gson by lazy { Gson() }

    fun load(context: Context): ChangeLog {
        return runCatching {
            gson.fromJson(
                AssetUtils.readFileToString(context, CHANGE_LOG_ASSET),
                ChangeLog::class.java
            )
        }.getOrNull() ?: ChangeLog()
    }
}
