package tornaco.apps.shortx.ui.main.model

data class ToggleUM(
    val id: String,
    val title: String,
    val description: String,
    val updateTime: String,
    val createTime: String,
    val enableState: ToggleEnableState,
    val versionCode: Long,

    // For online.
    val author: String = "ShortX",
    val isOnlineItem: Boolean = false,
    val url: String? = null,
    val isInstalled: Boolean = false,
    val hasUpdate: Boolean = false,
)

sealed interface ToggleEnableState {
    data object Loading : ToggleEnableState
    data class Loaded(val isEnabled: Boolean) : ToggleEnableState
}