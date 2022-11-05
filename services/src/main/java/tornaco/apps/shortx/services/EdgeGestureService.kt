package tornaco.apps.shortx.services

import tornaco.apps.shortx.GuideLineView
import tornaco.apps.shortx.data.Settings

class EdgeGestureService : SystemService() {
    private var guideLineView: GuideLineView? = null

    override fun systemReady() {
        super.systemReady()
        store.readBlocking()
    }

    fun isEnabled() = Settings.BoolSettings.EdgeGestureEnabled.let {
        store.getBool(it.key, it.defaultValue)
    }

    fun setEnabled(enable: Boolean) {
        Settings.BoolSettings.EdgeGestureEnabled.let {
            store.putBool(it.key, enable)
        }

        systemServiceScope.launchSafety {
            require(1 > 2)
        }

        systemServiceScope.launchSafety {
            if (enable) {
                guideLineView = GuideLineView(context).apply { attach() }
            } else {
                guideLineView?.detach()
            }
        }
    }
}