package tornaco.apps.shortx.services.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo.DetailedState
import android.net.wifi.WifiManager
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.WifiConfig
import tornaco.apps.shortx.core.proto.fact.VPNConnected
import tornaco.apps.shortx.core.proto.fact.VPNDisconnected
import tornaco.apps.shortx.core.proto.fact.WifiConnectedTo
import tornaco.apps.shortx.core.proto.fact.WifiDisconnectedFrom
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.wifiManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.rule.fact.trigger.wifi.WifiSignalState
import tornaco.apps.shortx.services.rule.fact.trigger.wifi.WifiState
import tornaco.apps.shortx.services.rule.fact.trigger.wifi.WifiStatusTracker
import tornaco.apps.shortx.services.runClearCallingIdentity
import tornaco.apps.shortx.services.v.v
import java.util.concurrent.atomic.AtomicReference

class NetworkService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "NetworkService") {
    private val _currentVpnState: MutableStateFlow<DetailedState?> = MutableStateFlow(null)

    private lateinit var wifiStatusTracker: WifiStatusTracker
    private val _connectedWifiSSID: AtomicReference<String?> = AtomicReference(null)
    val connectedWifiSSID get() = _connectedWifiSSID.get()

    private val _wifiSignalState: AtomicReference<WifiSignalState> = AtomicReference(
        WifiSignalState(0, 0)
    )
    val wifiSignalState get() = _wifiSignalState.get()

    private val wifiStatusReceiver = object : BroadcastReceiver() {
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

    override fun systemReady() {
        super.systemReady()
        registerWifiReceiver()
    }

    fun isVPNConnected() = _currentVpnState.value == DetailedState.CONNECTED

    fun onVpnStateChanged(detailedState: DetailedState) {
        logger.d("onVpnStateChanged: $detailedState")
        val currentState = _currentVpnState.value
        if (currentState != detailedState) {
            if (detailedState == DetailedState.CONNECTED) {
                logger.i("VPN connected.")
                shortXContext.rule.publishFact(
                    reason = "onVpnStateChanged",
                    fact = VPNConnected.newBuilder().build()
                )

            } else if (detailedState == DetailedState.DISCONNECTED) {
                logger.i("VPN disconnected.")
                shortXContext.rule.publishFact(
                    reason = "onVpnStateChanged",
                    fact = VPNDisconnected.newBuilder().build()
                )
            }
        }
        _currentVpnState.value = detailedState
    }

    private fun registerWifiReceiver() = kotlin.runCatching {
        wifiStatusTracker = WifiStatusTracker(context) {
            checkWifiStateChange()
        }

        wifiStatusTracker.setListening(true)

        val wifiIntentFilter = IntentFilter()
        wifiIntentFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        ContextCompat.registerReceiver(
            context,
            wifiStatusReceiver,
            wifiIntentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }.onFailure {
        logger.e(it, "registerWifiReceiver")
    }

    private fun checkWifiSignalChange() {
        val signalState =
            WifiSignalState(level = wifiStatusTracker.level, rssi = wifiStatusTracker.rssi)
        logger.d("WifiStatusTracker callback.run checkWifiSignalChange, ss: $signalState")
        val lastSignalState = wifiSignalState
        if (lastSignalState != signalState) {
            logger.d("Wifi signal changed.")
            _wifiSignalState.set(signalState)
        }
    }

    private fun checkWifiStateChange() {
        val ws = WifiState(
            enabled = wifiStatusTracker.enabled,
            statusLabel = wifiStatusTracker.statusLabel,
            // Workaround to remove ssid extra ""HELLO-KITTY""
            ssid = wifiStatusTracker.ssid?.replace("\"", "")
        )
        logger.d("WifiStatusTracker callback.run checkWifiStateChange, ws: $ws")

        val lastConnectedSSID = _connectedWifiSSID.get()

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

        _connectedWifiSSID.set(ws.ssid)
    }

    override fun shutdown() {
        super.shutdown()
        context.unregisterReceiver(wifiStatusReceiver)
    }

    fun getPrivilegedConfiguredNetworks(): List<WifiConfig> {
        return v(context) {
            runClearCallingIdentity {
                context.wifiManager.privilegedConfiguredNetworks.mapNotNull {
                    WifiConfig.newBuilder().setNetworkId(it.networkId).setSsid(it.SSID).build()
                }.distinct()
            }
        } ?: emptyList()
    }
}