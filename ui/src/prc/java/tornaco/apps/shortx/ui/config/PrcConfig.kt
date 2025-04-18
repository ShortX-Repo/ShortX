package tornaco.apps.shortx.ui.config

class PrcConfig : Config {
    override val isFeatureCVFindImageEnabled: Boolean
        get() = true
    override val forceUpgradeVersion: Long
        get() = 0
    override val globalNoticeMessage: String
        get() = ""
    // Null means PRC build.
    override val variant: String?
        get() = null
    override val globalNoticeId: String
        get() = ""
}