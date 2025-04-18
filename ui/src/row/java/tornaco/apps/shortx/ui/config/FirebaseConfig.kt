package tornaco.apps.shortx.ui.config

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug

class FirebaseConfig : Config {
    private val logger = Logger("FirebaseConfig")

    private var remoteConfig: FirebaseRemoteConfig? = null

    init {
        kotlin.runCatching {
            setup()
        }.onFailure {
            logger.e(it, "Init.")
        }
    }

    private fun setup() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60 * 10)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.fetchAndActivate()
                logger.i("onUpdate")
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                logger.i("onError: $error")
            }
        })
        this.remoteConfig = remoteConfig
    }

    override val isFeatureCVFindImageEnabled: Boolean
        get() = true
    override val forceUpgradeVersion: Long
        get() = remoteConfig?.getLong("force_upgrade_version_code") ?: 0
    override val globalNoticeMessage: String
        get() = remoteConfig?.getString("global_notice_message").orEmpty()
    override val variant: String
        get() = "google.com"
    override val globalNoticeId: String
        get() = remoteConfig?.getString("global_notice_id").orEmpty()
}