package tornaco.apps.shortx.ui.settings.log

data class LogState(
    val isLogEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val horizontalScrollEnabled: Boolean = false,
    val logs: List<String> = emptyList()
)
