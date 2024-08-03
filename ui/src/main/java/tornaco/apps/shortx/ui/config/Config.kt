package tornaco.apps.shortx.ui.config

interface Config {
    val isFeatureCVFindImageEnabled: Boolean
    val forceUpgradeVersion: Long

    val globalNoticeId: String
    val globalNoticeMessage: String
}

val config by lazy { Provider().provideConfig() }