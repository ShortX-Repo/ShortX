package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.SoftApCallback
import android.os.HandlerExecutor
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.wifiManager
import tornaco.apps.shortx.services.ShortXContext


class HotspotTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("HotspotTrigger")

    override fun register() {
        // Android 10 http://aospxref.com/android-10.0.0_r47/xref/frameworks/base/wifi/java/android/net/wifi/WifiManager.java#3080
        // Android 11 http://aospxref.com/android-11.0.0_r21/xref/frameworks/base/wifi/java/android/net/wifi/WifiManager.java#3612

        if (OsUtils.isROrAbove()) {
            context.wifiManager.registerSoftApCallback(
                HandlerExecutor(shortXContext.phone.handler),
                object : SoftApCallback {
                    override fun onStateChanged(state: Int, failureReason: Int) {
                        super.onStateChanged(state, failureReason)
                        logger.d("onStateChanged: $state $failureReason ${stateToString(state)}")

                        if (state == WifiManager.WIFI_AP_STATE_ENABLED) {
                            shortXContext.rule.publishFact(
                                "SoftApCallback.onStateChanged",
                                HotSpotStatusChanged.newBuilder().setOoa(OnOffAny.On).build()
                            ) { emptyMap() }
                        } else if (state == WifiManager.WIFI_AP_STATE_DISABLED) {
                            shortXContext.rule.publishFact(
                                "SoftApCallback.onStateChanged",
                                HotSpotStatusChanged.newBuilder().setOoa(OnOffAny.Off).build()
                            ) { emptyMap() }
                        }
                    }
                })
        }
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