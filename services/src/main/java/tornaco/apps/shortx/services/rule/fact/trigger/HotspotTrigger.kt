package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.SoftApCallback
import android.os.HandlerExecutor
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.wifiManager
import tornaco.apps.shortx.services.ShortXContext


class HotspotTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("HotspotTrigger")

    override fun register() {
        context.wifiManager.registerSoftApCallback(
            HandlerExecutor(shortXContext.phone.handler),
            object : SoftApCallback {
                override fun onStateChanged(state: Int, failureReason: Int) {
                    super.onStateChanged(state, failureReason)
                    logger.d("onStateChanged: $state $failureReason ${stateToString(state)}")
                }
            })
    }

    override fun unRegister() {
    }

    private fun stateToString(hotspotState: Int): String? {
        when (hotspotState) {
            WifiManager.WIFI_AP_STATE_DISABLED -> return "DISABLED"
            WifiManager.WIFI_AP_STATE_DISABLING -> return "DISABLING"
            WifiManager.WIFI_AP_STATE_ENABLED -> return "ENABLED"
            WifiManager.WIFI_AP_STATE_ENABLING -> return "ENABLING"
            WifiManager.WIFI_AP_STATE_FAILED -> return "FAILED"
        }
        return null
    }
}