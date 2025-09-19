package tornaco.apps.shortx.services.phone

import android.annotation.SuppressLint
import android.telephony.CellInfo
import android.telephony.CellLocation
import android.telephony.TelephonyCallback
import com.google.protobuf.ByteString
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.INFCTagEndpointListener
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.phone.ISMSProvider
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.fact.NFCTagDiscover
import tornaco.apps.shortx.core.rule.action.emptyByteArrayWrapper
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.RemoteCallbackListExt
import tornaco.apps.shortx.core.util.telephonyManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.runClearCallingIdentity
import java.time.Duration

private const val MISSING_SMS_PROVIDER_ERROR_MSG =
    "No registered SMS providers. Please ensure 'PhoneProvider' scope is selected in Xposed."

class PhoneService(shortXContext: ShortXContext) : SystemService(shortXContext, "PhoneService") {
    private val nfcListeners = RemoteCallbackListExt<INFCTagEndpointListener>()
    private val smsProviders = RemoteCallbackListExt<ISMSProvider>()

    override fun systemReady() {
        super.systemReady()
    }

    @SuppressLint("MissingPermission")
    fun getCellInfos() {
        logger.d("getCellInfos")
        runClearCallingIdentity {
            context.telephonyManager.allCellInfo?.forEach {
                logger.logOnDebug("cell: ${it.cellIdToString()}")
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

    fun attachSMSProvider(provider: ISMSProvider) {
        logger.w("attachSMSProvider: $provider")
        smsProviders.register(provider)
    }

    fun markSMSAsReadOrThrow(smsId: Long) {
        val first = acquireSMSProvider()
        val receiver = SynchronousResultReceiver.get()
        first.markAsRead(smsId, receiver)
        val result = runBlocking { receiver.awaitResultNoInterrupt(Duration.ofSeconds(5)) }
        val pair = StringPair.parseFrom(result.getValue(emptyByteArrayWrapper)?.byteData)
        val success = pair.second.toBoolean()
        require(success) { "Unable to mark msg as read: ${pair.second}" }
    }

    fun deleteSMSOrThrow(smsId: Long) {
        val first = acquireSMSProvider()
        val receiver = SynchronousResultReceiver.get()
        first.delete(smsId, receiver)
        val result = runBlocking { receiver.awaitResultNoInterrupt(Duration.ofSeconds(5)) }
        val pair = StringPair.parseFrom(result.getValue(emptyByteArrayWrapper)?.byteData)
        val success = pair.second.toBoolean()
        require(success) { "Unable to delete: ${pair.second}" }
    }

    private fun acquireSMSProvider(): ISMSProvider {
        val itemCount = smsProviders.registeredCallbackCount
        if (itemCount == 0) {
            error(MISSING_SMS_PROVIDER_ERROR_MSG)
        }
        val first = smsProviders.getRegisteredCallbackItem(0)
        return first
    }

    @SuppressLint("NewApi")
    private class TelephonyCallbackImpl : TelephonyCallback(),
        TelephonyCallback.CellLocationListener,
        TelephonyCallback.CellInfoListener {
        private val logger = Logger("TelephonyCallbackImpl")

        override fun onCellLocationChanged(location: CellLocation?) {
            location?.let {
                logger.logOnDebug("onCellLocationChanged: $location")
            }
        }

        override fun onCellInfoChanged(cellInfo: List<CellInfo>?) {
            cellInfo?.let {
                logger.logOnDebug("onCellInfoChanged: ${cellInfo.map { it }}")
            }
        }

    }
}


