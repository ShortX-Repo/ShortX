package tornaco.apps.shortx.ui.main.model

data class MainState(
    val isHideSubscribeInfo: Boolean = true,
    val isActivated: Boolean = false,
    val isFrameworkUpdated: Boolean = false,
    val isIgnoreFrameworkUpdate: Boolean = false,
    val isPrivacyStatementAccepted: Boolean = true,
    val isAudioRecording: Boolean = false,
    val hasSensorUsage: Boolean = false,
    val globalNotice: Pair<String, String> = "" to "",
)
