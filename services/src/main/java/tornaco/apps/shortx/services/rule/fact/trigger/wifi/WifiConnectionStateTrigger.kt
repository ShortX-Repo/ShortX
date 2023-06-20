package tornaco.apps.shortx.services.rule.fact.trigger.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.WifiConnectedTo
import tornaco.apps.shortx.core.proto.fact.WifiDisconnectedFrom
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.Trigger
import java.util.concurrent.atomic.AtomicReference

class WifiConnectionStateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("WifiConnectionStateTrigger")
    private lateinit var wifiStatusTracker: WifiStatusTracker

    private val connectedSSID: AtomicReference<String?> = AtomicReference(null)

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

            val lastConnectedSSID = connectedSSID.get()

            if (ws.ssid != null && lastConnectedSSID != ws.ssid) {
                logger.i("Connect changed to: ${ws.ssid}")
                shortXContext.rule.publishFact(
                    reason = "Wifi SSID changed.",
                    fact = WifiConnectedTo.newBuilder()
                        .addAllSsidList(listOf(ws.ssid)).build(),
                    initialContextData = mapOf(
                        ContextDataMapping.WifiConnectedTo.ssid.name to ws.ssid.fallbackOnEmpty(""),
                        ContextDataMapping.WifiConnectedTo.isWifiEnabled.name to ws.enabled.toString(),
                        ContextDataMapping.WifiConnectedTo.wifiStatusLabel.name to ws.statusLabel.fallbackOnEmpty(
                            ""
                        )
                    )
                )
            }

            if (ws.ssid == null && lastConnectedSSID != null) {
                logger.i("Disconnected from: $lastConnectedSSID")
                shortXContext.rule.publishFact(
                    reason = "Wifi disconnected.",
                    fact = WifiDisconnectedFrom.newBuilder()
                        .addAllSsidList(listOf(lastConnectedSSID)).build(),
                    initialContextData = mapOf(
                        ContextDataMapping.WifiDisconnectedFrom.ssid.name to lastConnectedSSID.fallbackOnEmpty(
                            ""
                        ),
                        ContextDataMapping.WifiDisconnectedFrom.isWifiEnabled.name to ws.enabled.toString(),
                        ContextDataMapping.WifiDisconnectedFrom.wifiStatusLabel.name to ws.statusLabel.fallbackOnEmpty(
                            ""
                        )
                    )
                )
            }

            connectedSSID.set(ws.ssid)
        }

        wifiStatusTracker.setListening(true)

        val wifiIntentFilter = IntentFilter()
        wifiIntentFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        ContextCompat.registerReceiver(
            context,
            receiver,
            wifiIntentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}