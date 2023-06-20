package tornaco.apps.shortx.services.network

import android.net.NetworkInfo.DetailedState
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.proto.fact.VPNConnected
import tornaco.apps.shortx.core.proto.fact.VPNDisconnected
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService

class NetworkService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    override val logTag: String get() = "NetworkService"

    private val _currentVpnState: MutableStateFlow<DetailedState?> = MutableStateFlow(null)

    override fun systemReady() {
        super.systemReady()
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
}