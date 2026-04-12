package tornaco.apps.shortx.ui.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.action.CurrentLocationProviderPreference
import tornaco.apps.shortx.core.proto.common.CurrentLocationAddressData
import tornaco.apps.shortx.core.proto.common.CurrentLocationInfoData
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.wrap
import tornaco.apps.shortx.core.util.Logger
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.coroutines.resume

internal object AppLocationServiceSupport {
    private const val DEFAULT_TIMEOUT_MILLIS = 3000L

    private val logger = Logger("AppLocationService")
    private val executors = Executors.newCachedThreadPool()

    fun getCurrentLocationInfo(
        context: Context,
        requestId: String,
        receiver: SynchronousResultReceiver,
        requestData: ByteArrayWrapper,
    ) {
        executors.execute {
            runCatching {
                val request = tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo.parseFrom(requestData.byteData)
                logger.d("getCurrentLocationInfo requestId=$requestId, providerPreference=${request.providerPreference}, timeoutMillis=${request.timeoutMillis}")
                val location = runBlocking {
                    currentLocation(
                        context = context,
                        providerPreference = request.providerPreference,
                        timeoutMillis = request.timeoutMillis,
                        logPrefix = "AppLocationService#getCurrentLocationInfo",
                    )
                }
                val result = CurrentLocationInfoData.newBuilder()
                    .setLatitude(location.latitude)
                    .setLongitude(location.longitude)
                    .setProvider(location.provider.orEmpty())
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
        context: Context,
        requestId: String,
        receiver: SynchronousResultReceiver,
        requestData: ByteArrayWrapper,
    ) {
        executors.execute {
            runCatching {
                val request = tornaco.apps.shortx.core.proto.action.GetCurrentLocationAddress.parseFrom(requestData.byteData)
                logger.d("getCurrentLocationAddress requestId=$requestId, providerPreference=${request.providerPreference}, timeoutMillis=${request.timeoutMillis}")
                val location = runBlocking {
                    currentLocation(
                        context = context,
                        providerPreference = request.providerPreference,
                        timeoutMillis = request.timeoutMillis,
                        logPrefix = "AppLocationService#getCurrentLocationAddress",
                    )
                }
                val address = reverseGeocode(context, location)
                val result = CurrentLocationAddressData.newBuilder()
                    .setAddressLine(buildAddressLine(address))
                    .setCountryName(address.countryName.orEmpty())
                    .setAdminArea(address.adminArea.orEmpty())
                    .setSubAdminArea(address.subAdminArea.orEmpty())
                    .setLocality(address.locality.orEmpty())
                    .setSubLocality(address.subLocality.orEmpty())
                    .setThoroughfare(address.thoroughfare.orEmpty())
                    .setSubThoroughfare(address.subThoroughfare.orEmpty())
                    .setPostalCode(address.postalCode.orEmpty())
                    .setFeatureName(address.featureName.orEmpty())
                    .build()
                receiver.send(result.toByteArray().wrap())
            }.onFailure {
                logger.e(it, "getCurrentLocationAddress failed")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(
        context: Context,
        providerPreference: CurrentLocationProviderPreference,
        timeoutMillis: Long,
        logPrefix: String,
    ): Location {
        ensureLocationPermissions(context, logPrefix)
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: fail(
                "$logPrefix: LocationManager unavailable"
            )

        val providers = getEnabledProviders(locationManager, providerPreference, logPrefix)
        val effectiveTimeout = timeoutMillis.takeIf { it > 0 } ?: DEFAULT_TIMEOUT_MILLIS
        logger.d("$logPrefix providerPreference=$providerPreference, effectiveTimeout=$effectiveTimeout, enabledProviders=$providers")

        if (providers.isEmpty()) {
            fail("$logPrefix: no enabled providers for preference=$providerPreference")
        }

        val currentLocation = withTimeoutOrNull(effectiveTimeout) {
            for (provider in providers) {
                logger.d("$logPrefix requesting current location from provider=$provider")
                val location = requestCurrentLocationCompat(
                    context = context,
                    locationManager = locationManager,
                    provider = provider,
                    logPrefix = logPrefix,
                )
                if (location != null) {
                    logger.d("$logPrefix current location resolved by provider=$provider")
                    return@withTimeoutOrNull location
                }
                logger.d("$logPrefix provider=$provider returned null location, continue to next provider")
            }
            null
        }

        if (currentLocation != null) {
            return currentLocation
        }

        logger.e("$logPrefix current location timed out or all providers returned null, fallback to last known location")
        return bestLastKnownLocation(locationManager, providers, logPrefix)
            ?: fail("$logPrefix: current location unavailable, and no last known location available")
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocationCompat(
        context: Context,
        locationManager: LocationManager,
        provider: String,
        logPrefix: String,
    ): Location? {
        return suspendCancellableCoroutine { continuation ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                runCatching {
                    locationManager.getCurrentLocation(provider, null, context.mainExecutor) {
                        if (continuation.isActive) {
                            logger.d("$logPrefix current location callback from provider=$provider, location=${it?.let { loc -> "${loc.latitude},${loc.longitude},acc=${loc.accuracy}" } ?: "null"}")
                            continuation.resume(it)
                        }
                    }
                }.onFailure {
                    fail("$logPrefix: getCurrentLocation failed for provider=$provider", it)
                }
            } else {
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        logger.d("$logPrefix single update callback from provider=$provider, location=${location.latitude},${location.longitude},acc=${location.accuracy}")
                        runCatching { locationManager.removeUpdates(this) }
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                }
                continuation.invokeOnCancellation {
                    runCatching { locationManager.removeUpdates(listener) }
                }
                runCatching {
                    locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                }.onFailure {
                    runCatching { locationManager.removeUpdates(listener) }
                    fail("$logPrefix: requestSingleUpdate failed for provider=$provider", it)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun bestLastKnownLocation(
        locationManager: LocationManager,
        preferredProviders: List<String>,
        logPrefix: String,
    ): Location? {
        logger.d("$logPrefix checking last known location with preferredProviders=$preferredProviders")
        preferredProviders.firstNotNullOfOrNull { provider ->
            runCatching { locationManager.getLastKnownLocation(provider) }
                .onFailure {
                    fail("$logPrefix: getLastKnownLocation failed for preferred provider=$provider", it)
                }
                .getOrNull()
        }?.let {
            logger.d("$logPrefix preferred last known location hit, provider=${it.provider.orEmpty()}, lat=${it.latitude}, lng=${it.longitude}, accuracy=${it.accuracy}")
            return it
        }

        val fallbackProviders = runCatching { locationManager.getProviders(true) }
            .onFailure {
                fail("$logPrefix: getProviders(true) failed while checking fallback providers", it)
            }
            .getOrDefault(emptyList())
            .filterNot { it in preferredProviders }
        logger.d("$logPrefix checking fallback last known providers=$fallbackProviders")
        return fallbackProviders.firstNotNullOfOrNull { provider ->
            runCatching { locationManager.getLastKnownLocation(provider) }
                .onFailure {
                    fail("$logPrefix: getLastKnownLocation failed for fallback provider=$provider", it)
                }
                .getOrNull()
        }?.also {
            logger.d("$logPrefix fallback last known location hit, provider=${it.provider.orEmpty()}, lat=${it.latitude}, lng=${it.longitude}, accuracy=${it.accuracy}")
        }
    }

    private fun getEnabledProviders(
        locationManager: LocationManager,
        providerPreference: CurrentLocationProviderPreference,
        logPrefix: String,
    ): List<String> {
        val providerOrder = when (providerPreference) {
            CurrentLocationProviderPreference.CurrentLocationProviderPreference_GpsFirst -> {
                listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            }

            CurrentLocationProviderPreference.CurrentLocationProviderPreference_Auto,
            CurrentLocationProviderPreference.CurrentLocationProviderPreference_NetworkFirst,
            CurrentLocationProviderPreference.UNRECOGNIZED -> {
                listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
            }
        }
        return providerOrder.filter { provider ->
            runCatching { locationManager.isProviderEnabled(provider) }
                .onFailure {
                    fail("$logPrefix: isProviderEnabled failed for provider=$provider", it)
                }
                .getOrDefault(false)
        }.also {
            logger.d("$logPrefix providerOrder=$providerOrder, enabledProviders=$it")
        }
    }

    private fun reverseGeocode(context: Context, location: Location): Address {
        if (!Geocoder.isPresent()) {
            fail("AppLocationService#getCurrentLocationAddress: Geocoder is not present")
        }

        logger.d("AppLocationService#getCurrentLocationAddress reverseGeocode: lat=${location.latitude}, lng=${location.longitude}, locale=${Locale.getDefault()}")
        val geocoder = Geocoder(context, Locale.getDefault())
        val results = runCatching {
            geocoder.getFromLocation(location.latitude, location.longitude, 1).orEmpty()
        }.getOrElse {
            fail("AppLocationService#getCurrentLocationAddress: reverse geocode failed", it)
        }

        return results.firstOrNull()
            ?: fail("AppLocationService#getCurrentLocationAddress: reverse geocode returned no address result")
    }

    private fun buildAddressLine(address: Address): String {
        if (address.maxAddressLineIndex >= 0) {
            return (0..address.maxAddressLineIndex).joinToString(" ") {
                address.getAddressLine(it).orEmpty()
            }.trim()
        }
        return listOfNotNull(
            address.countryName,
            address.adminArea,
            address.subAdminArea,
            address.locality,
            address.subLocality,
            address.thoroughfare,
            address.subThoroughfare,
            address.featureName,
            address.postalCode,
        ).joinToString(" ").trim()
    }

    private fun ensureLocationPermissions(context: Context, logPrefix: String) {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasFineLocation) {
            fail("$logPrefix: ACCESS_FINE_LOCATION is not granted for ShortX app")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasBackgroundLocation = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasBackgroundLocation) {
                fail("$logPrefix: ACCESS_BACKGROUND_LOCATION is not granted for ShortX app, please allow all the time in app settings")
            }
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