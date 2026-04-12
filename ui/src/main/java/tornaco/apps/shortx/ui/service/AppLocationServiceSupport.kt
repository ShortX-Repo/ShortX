package tornaco.apps.shortx.ui.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.apis.utils.core.api.AMapUtilCoreApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.action.CurrentLocationProviderPreference
import tornaco.apps.shortx.core.proto.common.CurrentLocationAddressData
import tornaco.apps.shortx.core.proto.common.CurrentLocationInfoData
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.wrap
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal object AppLocationServiceSupport {
    private const val DEFAULT_TIMEOUT_MILLIS = 10000L

    private val logger = Logger("AppLocationService")
    private val executors = Executors.newCachedThreadPool()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun getCurrentLocationInfo(
        service: AppLocationService,
        requestId: String,
        receiver: SynchronousResultReceiver,
        requestData: ByteArrayWrapper,
    ) {
        executors.execute {
            runCatching {
                val request =
                    tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo.parseFrom(
                        requestData.byteData
                    )
                logger.d("getCurrentLocationInfo requestId=$requestId, providerPreference=${request.providerPreference}, timeoutMillis=${request.timeoutMillis}")
                val location = runBlocking {
                    currentAMapLocation(
                        context = service.applicationContext,
                        needAddress = false,
                        providerPreference = request.providerPreference,
                        timeoutMillis = request.timeoutMillis,
                        logPrefix = "AppLocationService#getCurrentLocationInfo",
                    )
                }
                val result = CurrentLocationInfoData.newBuilder()
                    .setLatitude(location.latitude)
                    .setLongitude(location.longitude)
                    .setProvider(locationProvider(location))
                    .setAccuracy(location.accuracy)
                    .build()
                receiver.send(result.toByteArray().wrap())
            }.onFailure {
                logger.e(it, "getCurrentLocationInfo failed")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    fun getCurrentLocationAddress(
        service: AppLocationService,
        requestId: String,
        receiver: SynchronousResultReceiver,
        requestData: ByteArrayWrapper,
    ) {
        executors.execute {
            runCatching {
                val request =
                    tornaco.apps.shortx.core.proto.action.GetCurrentLocationAddress.parseFrom(
                        requestData.byteData
                    )
                logger.d("getCurrentLocationAddress requestId=$requestId, providerPreference=${request.providerPreference}, timeoutMillis=${request.timeoutMillis}")
                val location = runBlocking {
                    currentAMapLocation(
                        context = service.applicationContext,
                        needAddress = true,
                        providerPreference = request.providerPreference,
                        timeoutMillis = request.timeoutMillis,
                        logPrefix = "AppLocationService#getCurrentLocationAddress",
                    )
                }
                val result = CurrentLocationAddressData.newBuilder()
                    .setAddressLine(location.address.orEmpty())
                    .setCountryName(location.country.orEmpty())
                    .setAdminArea(location.province.orEmpty())
                    .setSubAdminArea(location.city.orEmpty())
                    .setLocality(location.city.orEmpty())
                    .setSubLocality(location.district.orEmpty())
                    .setThoroughfare(location.street.orEmpty())
                    .setSubThoroughfare(location.streetNum.orEmpty())
                    .setPostalCode("")
                    .setFeatureName(location.aoiName.orEmpty())
                    .build()
                receiver.send(result.toByteArray().wrap())
            }.onFailure {
                logger.e(it, "getCurrentLocationAddress failed")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    private suspend fun currentAMapLocation(
        context: Context,
        needAddress: Boolean,
        providerPreference: CurrentLocationProviderPreference,
        timeoutMillis: Long,
        logPrefix: String,
    ): AMapLocation {
        ensureLocationPermissions(context, logPrefix)
        val effectiveTimeout = timeoutMillis.takeIf { it > 0 } ?: DEFAULT_TIMEOUT_MILLIS
        initAMapPrivacy(context)
        ensureAMapApiKeyConfigured(context, logPrefix)

        logger.d("$logPrefix using AMap SDK, providerPreference=$providerPreference, effectiveTimeout=$effectiveTimeout, needAddress=$needAddress")

        return suspendCancellableCoroutine { continuation ->
            var locationClient: AMapLocationClient? = null

            fun cleanup() {
                mainHandler.post {
                    val client = locationClient ?: return@post
                    runCatching { client.stopLocation() }
                    runCatching { client.onDestroy() }
                    locationClient = null
                }
            }

            fun resumeFailure(message: String, cause: Throwable? = null) {
                cleanup()
                val error = if (cause != null) {
                    logger.e(cause, message)
                    IllegalStateException(message, cause)
                } else {
                    logger.e(message)
                    IllegalStateException(message)
                }
                if (continuation.isActive) {
                    continuation.resumeWithException(error)
                }
            }

            continuation.invokeOnCancellation {
                cleanup()
            }

            mainHandler.post {
                if (!continuation.isActive) {
                    return@post
                }

                val client = try {
                    AMapLocationClient(context)
                } catch (e: Exception) {
                    resumeFailure("$logPrefix: failed to create AMapLocationClient", e)
                    return@post
                }
                locationClient = client

                val locationOption = AMapLocationClientOption().apply {
                    locationMode = when (providerPreference) {
                        CurrentLocationProviderPreference.CurrentLocationProviderPreference_GpsFirst -> {
                            AMapLocationMode.Device_Sensors
                        }

                        CurrentLocationProviderPreference.CurrentLocationProviderPreference_NetworkFirst -> {
                            AMapLocationMode.Battery_Saving
                        }

                        CurrentLocationProviderPreference.CurrentLocationProviderPreference_Auto,
                        CurrentLocationProviderPreference.UNRECOGNIZED -> {
                            AMapLocationMode.Hight_Accuracy
                        }
                    }
                    isOnceLocation = true
                    isOnceLocationLatest = true
                    isNeedAddress = needAddress
                    httpTimeOut = effectiveTimeout
                    interval = effectiveTimeout
                    isMockEnable = false
                    setLocationCacheEnable(false)
                }

                client.setLocationOption(locationOption)
                client.setLocationListener { aMapLocation ->
                    if (!continuation.isActive) {
                        cleanup()
                        return@setLocationListener
                    }
                    if (aMapLocation == null) {
                        resumeFailure("$logPrefix: AMap returned null location result")
                        return@setLocationListener
                    }
                    if (aMapLocation.errorCode == 0) {
                        logger.d(
                            "$logPrefix AMap success: lat=${aMapLocation.latitude}, lng=${aMapLocation.longitude}, accuracy=${aMapLocation.accuracy}, locationType=${aMapLocation.locationType}, provider=${locationProvider(aMapLocation)}"
                        )
                        cleanup()
                        continuation.resume(aMapLocation)
                    } else {
                        resumeFailure(
                            message = "$logPrefix: AMap location failed, errorCode=${aMapLocation.errorCode}, errorInfo=${aMapLocation.errorInfo}, locationDetail=${aMapLocation.locationDetail.orEmpty()}"
                        )
                    }
                }
                logger.d("$logPrefix AMap startLocation")
                client.startLocation()
            }
        }
    }

    private fun locationProvider(location: AMapLocation): String {
        return when (location.locationType) {
            AMapLocation.LOCATION_TYPE_GPS -> "amap:gps"
            AMapLocation.LOCATION_TYPE_SAME_REQ -> "amap:cache"
            AMapLocation.LOCATION_TYPE_FIX_CACHE -> "amap:fused-cache"
            AMapLocation.LOCATION_TYPE_WIFI -> "amap:wifi"
            AMapLocation.LOCATION_TYPE_CELL -> "amap:cell"
            AMapLocation.LOCATION_TYPE_OFFLINE -> "amap:offline"
            else -> "amap:${location.locationType}"
        }
    }

    private fun ensureLocationPermissions(context: Context, logPrefix: String) {
        val fine =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (fine != PackageManager.PERMISSION_GRANTED) {
            fail("$logPrefix: No ACCESS_FINE_LOCATION permission")
        }
        // 不再强制要求后台定位权限
    }

    private fun initAMapPrivacy(context: Context) {
        runCatching {
            AMapLocationClient.updatePrivacyShow(context, true, true)
            AMapLocationClient.updatePrivacyAgree(context, true)
            AMapUtilCoreApi.setCollectInfoEnable(true)
        }.onFailure {
            fail("AMap privacy initialization failed", it)
        }
    }

    private fun ensureAMapApiKeyConfigured(context: Context, logPrefix: String) {
        val appInfo = runCatching {
            context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        }.getOrElse {
            fail("$logPrefix: unable to read application meta-data", it)
        }
        val apiKey = appInfo.metaData?.getString("com.amap.api.v2.apikey").orEmpty()
        if (apiKey.isBlank()) {
            fail("$logPrefix: AMap API key is missing, please configure amap.api.key.row / amap.api.key.prc in local.properties or gradle.properties, or provide amap.api.key as a shared fallback")
        }
    }

    private fun fail(message: String, cause: Throwable? = null): Nothing {
        if (cause != null) {
            logger.e(cause, message)
            throw IllegalStateException(message, cause)
        }
        logger.e(message)
        throw IllegalStateException(message)
    }
}