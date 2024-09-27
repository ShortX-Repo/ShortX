package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.connectivityManager
import tornaco.apps.shortx.core.util.vpn
import tornaco.apps.shortx.services.ShortXContext

// API 30.
// Missing call permission for uid 1000
class VPNStateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("VPNStateTrigger")

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            logger.d("onAvailable: $network")
            if (OsUtils.isROrAbove()) {
                val vpnConnected = context.vpn.getVpnConfig(myUserId) != null
                logger.d("vpnConnected: $vpnConnected")
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            logger.d("onLost: $network")
        }
    }

    override fun register() {
        if (OsUtils.isROrAbove()) {
            val request = NetworkRequest.Builder().clearCapabilities().build()
            context.connectivityManager.registerNetworkCallback(request, callback)
        }
    }

    override fun unRegister() {
        context.connectivityManager.unregisterNetworkCallback(callback)
    }
}