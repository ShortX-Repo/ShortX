package tornaco.apps.shortx.services.rule.actor.mvel.handle.impl

import android.content.Context
import android.content.res.Configuration
import tornaco.apps.shortx.core.util.uiModeManager
import tornaco.apps.shortx.services.rule.actor.mvel.handle.ISettings

class SettingsImpl(private val context: Context) : ISettings {
    private val uiModeManager by lazy {
        context.uiModeManager
    }

    override fun enableDarkMode() {
        uiModeManager.setNightModeActivated(true)
    }

    override fun disableDarkMode() {
        uiModeManager.setNightModeActivated(false)
    }

    override fun isDarkModeEnabled(): Boolean {
        val currentConfig = context.resources.configuration
        val uiMode = currentConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }
}