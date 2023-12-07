package tornaco.apps.shortx.services.rule.actor.impl

import android.net.wifi.WifiManager.ActionListener
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.maskAll
import tornaco.apps.shortx.core.util.wifiManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ConnectWifi : Actor {
    /** Passed with {@link #onFailure}.
     * Indicates that the operation failed due to an internal error.
     */
    const val FAILURE_INTERNAL_ERROR = 0;

    /**
     * Passed with {@link #onFailure}.
     * Indicates that the operation is already in progress.
     */
    const val FAILURE_IN_PROGRESS = 1;

    /**
     * Passed with {@link #onFailure}.
     * Indicates that the operation failed because the framework is busy and is unable to
     * service the request.
     */
    const val FAILURE_BUSY = 2;

    /**
     * Passed with {@link #onFailure}.
     * Indicates that the operation failed due to invalid inputs.
     */
    const val FAILURE_INVALID_ARGS = 3;

    /**
     * Passed with {@link #onFailure}.
     * Indicates that the operation failed due to insufficient user permissions.
     */
    const val FAILURE_NOT_AUTHORIZED = 4;


    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ConnectWifi::class.java
    }

    @ExperimentalCoroutinesApi
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ConnectWifi::class.java
        logger.d("connect wifi: ${d.ssid.maskAll()}")
        val config = shortXContext.network.getPrivilegedConfiguredNetworks().firstOrNull {
            it.ssid == d.ssid
        }
        logger.p("config: $config")
        requireNotNull(config) {
            "Wifi config not found: ${d.ssid}"
        }

        val isSuccessToReason: Pair<Boolean, Int> =
            suspendCancellableCoroutine { continuation ->
                androidContext.wifiManager.connect(config.networkId,
                    @DoNotStrip
                    object : ActionListener {
                        override fun onSuccess() {
                            logger.d("onSuccess")
                            continuation.resume(true to -1) {}

                        }

                        override fun onFailure(reason: Int) {
                            logger.d("onFailure: $reason")
                            continuation.resume(false to reason) {}

                        }
                    })
            }
        logger.d("isSuccessToReason: $isSuccessToReason")

        require(isSuccessToReason.first) {
            "Fail to connect wifi, reason: ${isSuccessToReason.second.toFailReason()}"
        }
    }

    private fun Int.toFailReason(): String {
        return when (this) {
            FAILURE_BUSY -> "FAILURE_BUSY"
            FAILURE_NOT_AUTHORIZED -> "FAILURE_NOT_AUTHORIZED"
            FAILURE_INTERNAL_ERROR -> "FAILURE_INTERNAL_ERROR"
            FAILURE_IN_PROGRESS -> "FAILURE_IN_PROGRESS"
            FAILURE_INVALID_ARGS -> "FAILURE_INVALID_ARGS"
            else -> {
                "UNKNOWN: $this"
            }
        }
    }
}