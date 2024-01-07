package tornaco.apps.shortx.services.rule.fact.trigger.wifi

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.wifiManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.Trigger
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

class WifiScanResultTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("WifiScanResultTrigger")

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            val wifiManager = context.wifiManager
            val scanResults: List<ScanResult> = wifiManager.scanResults
            for (scanResult in scanResults) {
                val ssid: String = scanResult.SSID
                val bssid: String = scanResult.BSSID
                val signalStrength: Int = scanResult.level
                logger.d("SSID: $ssid, BSSID: $bssid, Signal Strength: $signalStrength")
            }
        }
    }

    override fun register() {
        val wifiIntentFilter = IntentFilter().apply {
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        }
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