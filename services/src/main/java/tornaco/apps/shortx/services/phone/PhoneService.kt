package tornaco.apps.shortx.services.phone

import android.annotation.SuppressLint
import android.os.RemoteCallbackList
import android.telephony.CellInfo
import android.telephony.CellLocation
import android.telephony.TelephonyCallback
import com.google.protobuf.ByteString
import tornaco.apps.shortx.core.INFCTagEndpointListener
import tornaco.apps.shortx.core.proto.fact.NFCTagDiscover
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.telephonyManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.runClearCallingIdentity


class PhoneService(shortXContext: ShortXContext) : SystemService(shortXContext, "PhoneService") {
    private val nfcListeners = RemoteCallbackList<INFCTagEndpointListener>()

    override fun systemReady() {
        super.systemReady()
    }

    @SuppressLint("MissingPermission")
    fun getCellInfos() {
        logger.d("getCellInfos")
        runClearCallingIdentity {
            context.telephonyManager.allCellInfo?.forEach {
                logger.p("cell: ${it.cellIdToString()}")
            }
        }
    }

    fun dispatchTagEndpoint(uid: ByteArray) {
        logger.d("dispatchTagEndpoint: ${uid.joinToString()}")
        synchronized(nfcListeners) {
            nfcListeners.broadcast {
                runCatching { it.onDispatchTagEndpoint(uid) }.onFailure {
                    logger.e(
                        it,
                        "Call dispatchTagEndpoint"
                    )
                }
            }
        }

        shortXContext.rule.publishFact(
            "dispatchTagEndpoint",
            NFCTagDiscover.newBuilder().setUid(ByteString.copyFrom(uid)).build()
        ) { emptyMap() }
    }

    fun registerNFCTagEndpointListener(listener: INFCTagEndpointListener) {
        nfcListeners.register(listener)
    }

    fun unregisterNFCTagEndpointListener(listener: INFCTagEndpointListener) {
        nfcListeners.unregister(listener)
    }

    @SuppressLint("NewApi")
    private class TelephonyCallbackImpl : TelephonyCallback(),
        TelephonyCallback.CellLocationListener,
        TelephonyCallback.CellInfoListener {
        private val logger = Logger("TelephonyCallbackImpl")

        override fun onCellLocationChanged(location: CellLocation?) {
            location?.let {
                logger.p("onCellLocationChanged: $location")
            }
        }

        override fun onCellInfoChanged(cellInfo: List<CellInfo>?) {
            cellInfo?.let {
                logger.p("onCellInfoChanged: ${cellInfo.map { it }}")
            }
        }

    }
}


