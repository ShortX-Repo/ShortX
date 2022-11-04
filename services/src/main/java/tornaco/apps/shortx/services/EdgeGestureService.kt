package tornaco.apps.shortx.services

import tornaco.apps.shortx.data.Settings

class EdgeGestureService : SystemService() {

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
    }
}