package tornaco.apps.shortx.services.rule.fact.trigger.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.WifiStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.Trigger
import java.util.concurrent.atomic.AtomicReference

class WifiStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("WifiStateTrigger")
    private lateinit var wifiStatusTracker: WifiStatusTracker
    private val currentWifiState = AtomicReference(WifiState(false, null, null))

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            kotlin.runCatching {
                intent?.action?.let {
                    wifiStatusTracker.handleBroadcast(intent)
                }
            }.onFailure {
                logger.e(it, "onReceive error")
            }
        }
    }

    override fun register() {
        wifiStatusTracker = WifiStatusTracker(context) {
            val ws = WifiState(
                enabled = wifiStatusTracker.enabled,
                statusLabel = wifiStatusTracker.statusLabel,
                // Workaround to remove ssid extra ""HELLO-KITTY""
                ssid = wifiStatusTracker.ssid?.replace("\"", "")
            )
            logger.d("WifiStatusTracker, callback.run, ws: $ws")
            if (ws != currentWifiState.get()) {
                logger.d("WifiStatusTracker Wifi update: $ws")
                currentWifiState.set(ws)

                shortXContext.rule.publishFact(
                    reason = "Wifi update",
                    fact = WifiStatusChanged.newBuilder()
                        .setSsid(ws.ssid ?: "")
                        .setOoa(ws.enabled.select(OnOffAny.On, OnOffAny.Off)).build(),
                    contextMap = mapOf(
                        "ssid" to ws.ssid.fallbackOnEmpty(""),
                        "enabled" to ws.enabled.toString(),
                        "statusLabel" to ws.statusLabel.fallbackOnEmpty("")
                    )
                )
            }

        }
        wifiStatusTracker.setListening(true)

        val wifiIntentFilter = IntentFilter()
        wifiIntentFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        context.registerReceiver(receiver, wifiIntentFilter)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}