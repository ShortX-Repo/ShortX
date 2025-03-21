package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver


class HotspotTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("HotspotTrigger")

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            logger.d("handleReceive: $intent")

            val action = intent.action

            if (WifiManager.WIFI_AP_STATE_CHANGED_ACTION == action) {
                val wifiApState = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE, -1)
                logger.d("wifiApState: ${stateToString(wifiApState)}")
                when (wifiApState) {
                    WifiManager.WIFI_AP_STATE_DISABLED -> {
                        shortXContext.rule.publishFact(
                            "SoftApCallback.onStateChanged",
                            HotSpotStatusChanged.newBuilder().setOoa(OnOffAny.Off).build()
                        ) { emptyMap() }
                    }

                    WifiManager.WIFI_AP_STATE_ENABLED -> {
                        shortXContext.rule.publishFact(
                            "SoftApCallback.onStateChanged",
                            HotSpotStatusChanged.newBuilder().setOoa(OnOffAny.On).build()
                        ) { emptyMap() }
                    }

                    WifiManager.WIFI_AP_STATE_FAILED -> {}
                }
            }
        }
    }

    override fun register() {
        val filter = IntentFilter(WifiManager.WIFI_AP_STATE_CHANGED_ACTION)
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
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