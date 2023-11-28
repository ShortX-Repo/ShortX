package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged
import tornaco.apps.shortx.core.util.connectivityManager
import tornaco.apps.shortx.services.ShortXContext

class ConnectivityStateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            shortXContext.rule.publishFact(
                "ConnectivityState onAvailable",
                MobileDataStatusChanged.newBuilder()
                    .setOoa(OnOffAny.On)
                    .build()
            ) { emptyMap() }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            shortXContext.rule.publishFact(
                "ConnectivityState onLost",
                MobileDataStatusChanged.newBuilder()
                    .setOoa(OnOffAny.Off)
                    .build()
            ) { emptyMap() }
        }
    }

    override fun register() {
        val builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        context.connectivityManager.registerNetworkCallback(builder.build(), callback)
    }

    override fun unRegister() {
        context.connectivityManager.unregisterNetworkCallback(callback)
    }
}