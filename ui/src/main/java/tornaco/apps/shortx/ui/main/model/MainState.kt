package tornaco.apps.shortx.ui.main.model

data class MainState(
    val isHideSubscribeInfo: Boolean = false,
    val isActivated: Boolean = false,
    val isFrameworkUpdated: Boolean = false,
    val isIgnoreFrameworkUpdate: Boolean = false,
    val isPrivacyStatementAccepted: Boolean = false,
    val isAudioRecording: Boolean = false
)
