package tornaco.apps.shortx.ui.config

class PrcConfig : Config {
    override val isFeatureCVFindImageEnabled: Boolean
        get() = false
    override val forceUpgradeVersion: Long
        get() = 0
    override val globalNotice: String
        get() = ""
}