package tornaco.apps.shortx.services.rule.fact.trigger.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.WifiStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.core.util.wifiManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.Trigger

class WifiStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("WifiStateTrigger")

    private var lastWifiState: Boolean = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            kotlin.runCatching {
                intent?.action?.let {
                    val newWifiState =
                        context.wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED

                    logger.d("newWifiState: $newWifiState, last: $lastWifiState")

                    if (lastWifiState != newWifiState) {
                        lastWifiState = newWifiState

                        shortXContext.rule.publishFact(
                            reason = "Wifi state changed",
                            fact = WifiStatusChanged.newBuilder()
                                .setOoa(lastWifiState.select(OnOffAny.On, OnOffAny.Off)).build(),
                            contextMap = emptyMap()
                        )
                    }
                }
            }.onFailure {
                logger.e(it, "onReceive error")
            }
        }
    }

    override fun register() {
        lastWifiState = context.wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED
        logger.d("register, lastWifiState: $lastWifiState")

        val wifiIntentFilter = IntentFilter()
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        context.registerReceiver(receiver, wifiIntentFilter)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}