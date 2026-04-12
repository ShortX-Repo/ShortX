package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.location.ILocationProvider
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.CurrentLocationAddressData
import tornaco.apps.shortx.core.proto.common.CurrentLocationInfoData
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.services.util.AppServiceProxy
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max

internal class AppLocationServiceProxy(context: Context, intent: Intent, scope: CoroutineScope) :
    AppServiceProxy(context, intent, scope) {
    private val logger = tornaco.apps.shortx.core.util.Logger("AppLocationServiceProxy")
    private var impl: ILocationProvider? = null

    override fun onConnected(binder: IBinder) {
        impl = ILocationProvider.Stub.asInterface(binder)
    }

    suspend fun getCurrentLocationInfo(
        requestData: ByteArrayWrapper,
        timeoutMillis: Long,
    ): CurrentLocationInfoData? {
        val res = AtomicReference<CurrentLocationInfoData>()
        val requestId = SerialNum.nextId().toString()
        val waitTimeout = proxyTimeout(timeoutMillis)

        setTask(object : ProxyTask {
            override suspend fun run() {
                runCatching {
                    val receiver = SynchronousResultReceiver.get()
                    requireNotNull(impl) { "Impl is null." }.getCurrentLocationInfo(
                        requestId,
                        receiver,
                        requestData,
                    )
                    val receiverRes = receiver.awaitResultNoInterrupt(waitTimeout)
                    val byteRes: ByteArrayWrapper? = receiverRes.getValue(null)
                    res.set(byteRes?.let { CurrentLocationInfoData.parseFrom(it.byteData) })
                }.onFailure {
                    logger.e(it, "getCurrentLocationInfo error")
                    res.set(null)
                }
            }
        }, "GetCurrentLocationInfo")

        waitForCompletion(waitTimeout)
        return res.get()
    }

    suspend fun getCurrentLocationAddress(
        requestData: ByteArrayWrapper,
        timeoutMillis: Long,
    ): CurrentLocationAddressData? {
        val res = AtomicReference<CurrentLocationAddressData>()
        val requestId = SerialNum.nextId().toString()
        val waitTimeout = proxyTimeout(timeoutMillis)

        setTask(object : ProxyTask {
            override suspend fun run() {
                runCatching {
                    val receiver = SynchronousResultReceiver.get()
                    requireNotNull(impl) { "Impl is null." }.getCurrentLocationAddress(
                        requestId,
                        receiver,
                        requestData,
                    )
                    val receiverRes = receiver.awaitResultNoInterrupt(waitTimeout)
                    val byteRes: ByteArrayWrapper? = receiverRes.getValue(null)
                    res.set(byteRes?.let { CurrentLocationAddressData.parseFrom(it.byteData) })
                }.onFailure {
                    logger.e(it, "getCurrentLocationAddress error")
                    res.set(null)
                }
            }
        }, "GetCurrentLocationAddress")

        waitForCompletion(waitTimeout)
        return res.get()
    }

    companion object {
        suspend fun getCurrentLocationInfo(
            context: Context,
            scope: CoroutineScope,
            requestData: ByteArrayWrapper,
            timeoutMillis: Long,
        ): CurrentLocationInfoData? {
            val intent = Intent().apply {
                setPackage(BuildPropInternal.APP_PACKAGE_NAME)
                component = ComponentName.unflattenFromString(
                    "${BuildPropInternal.APP_PACKAGE_NAME}/${BuildProp.APP_LOCATION_SERVICE_NAME}"
                )
            }
            return AppLocationServiceProxy(context, intent, scope).getCurrentLocationInfo(
                requestData = requestData,
                timeoutMillis = timeoutMillis,
            )
        }

        suspend fun getCurrentLocationAddress(
            context: Context,
            scope: CoroutineScope,
            requestData: ByteArrayWrapper,
            timeoutMillis: Long,
        ): CurrentLocationAddressData? {
            val intent = Intent().apply {
                setPackage(BuildPropInternal.APP_PACKAGE_NAME)
                component = ComponentName.unflattenFromString(
                    "${BuildPropInternal.APP_PACKAGE_NAME}/${BuildProp.APP_LOCATION_SERVICE_NAME}"
                )
            }
            return AppLocationServiceProxy(context, intent, scope).getCurrentLocationAddress(
                requestData = requestData,
                timeoutMillis = timeoutMillis,
            )
        }

        private fun proxyTimeout(timeoutMillis: Long): Duration {
            val effectiveTimeout = timeoutMillis.takeIf { it > 0 } ?: 3000L
            return Duration.ofMillis(max(effectiveTimeout + 15000L, 30000L))
        }
    }

    private object SerialNum {
        private val id = AtomicInteger(0)

        fun nextId() = id.getAndIncrement()
    }
}