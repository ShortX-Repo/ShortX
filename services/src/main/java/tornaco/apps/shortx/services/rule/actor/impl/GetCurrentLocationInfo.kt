package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.protobuf.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.proto.action.CurrentLocationProviderPreference
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import kotlin.coroutines.resume

object GetCurrentLocationInfo : Actor {
    private const val DEFAULT_TIMEOUT_MILLIS = 3000L

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo::class.java
    }

    override suspend fun handle(params: HandleParams): Actor.Res = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo::class.java
        logger.d(
            "GetCurrentLocationInfo start: providerPreference=${data.providerPreference}, timeoutMillis=${data.timeoutMillis}, hasCustomContextDataKey=${data.hasCustomContextDataKey()}"
        )
        val location = currentLocation(
            context = androidContext,
            providerPreference = data.providerPreference,
            timeoutMillis = data.timeoutMillis
        )

        logger.d(
            "GetCurrentLocationInfo success: provider=${location.provider.orEmpty()}, lat=${location.latitude}, lng=${location.longitude}, accuracy=${location.accuracy}"
        )

        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationInfo.latitude.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationInfo.latitude.name },
            location.latitude.toString()
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationInfo.longitude.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationInfo.longitude.name },
            location.longitude.toString()
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationInfo.provider.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationInfo.provider.name },
            location.provider.orEmpty()
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationInfo.accuracy.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationInfo.accuracy.name },
            location.accuracy.toString()
        )

        Actor.Res(
            "lat=${location.latitude}, lng=${location.longitude}, provider=${location.provider.orEmpty()}, accuracy=${location.accuracy}"
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(
        context: Context,
        providerPreference: CurrentLocationProviderPreference,
        timeoutMillis: Long
    ): Location {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: run {
                fail("GetCurrentLocationInfo: LocationManager unavailable")
            }

        val providers = getEnabledProviders(locationManager, providerPreference)
        val effectiveTimeout = timeoutMillis.takeIf { it > 0 } ?: DEFAULT_TIMEOUT_MILLIS
        logger.d(
            "GetCurrentLocationInfo currentLocation: providerPreference=$providerPreference, effectiveTimeout=$effectiveTimeout, enabledProviders=$providers"
        )

        if (providers.isEmpty()) {
            fail("GetCurrentLocationInfo: no enabled providers for preference=$providerPreference")
        }

        val currentLocation = withTimeoutOrNull(effectiveTimeout) {
            for (provider in providers) {
                logger.d("GetCurrentLocationInfo: requesting current location from provider=$provider")
                val location = suspendCancellableCoroutine<Location?> { continuation ->
                    runCatching {
                        locationManager.getCurrentLocation(provider, null, context.mainExecutor) {
                            if (continuation.isActive) {
                                logger.d(
                                    "GetCurrentLocationInfo: current location callback from provider=$provider, location=${it?.let { loc -> "${loc.latitude},${loc.longitude},acc=${loc.accuracy}" } ?: "null"}"
                                )
                                continuation.resume(it)
                            }
                        }
                    }.onFailure {
                        fail(
                            message = "GetCurrentLocationInfo: getCurrentLocation failed for provider=$provider",
                            cause = it
                        )
                    }
                }
                if (location != null) {
                    logger.d("GetCurrentLocationInfo: current location resolved by provider=$provider")
                    return@withTimeoutOrNull location
                }
                logger.d("GetCurrentLocationInfo: provider=$provider returned null location, continue to next provider")
            }
            null
        }

        if (currentLocation != null) {
            return currentLocation
        }

        logger.e("GetCurrentLocationInfo: current location timed out or all providers returned null, fallback to last known location")

        return bestLastKnownLocation(locationManager, providers)
            ?: fail("GetCurrentLocationInfo: current location unavailable, and no last known location available")
    }

    @SuppressLint("MissingPermission")
    private fun bestLastKnownLocation(
        locationManager: LocationManager,
        preferredProviders: List<String>
    ): Location? {
        logger.d("GetCurrentLocationInfo: checking last known location with preferredProviders=$preferredProviders")
        preferredProviders.firstNotNullOfOrNull { provider ->
            runCatching { locationManager.getLastKnownLocation(provider) }
                .onFailure {
                    fail(
                        message = "GetCurrentLocationInfo: getLastKnownLocation failed for preferred provider=$provider",
                        cause = it
                    )
                }
                .getOrNull()
        }?.let {
            logger.d(
                "GetCurrentLocationInfo: preferred last known location hit, provider=${it.provider.orEmpty()}, lat=${it.latitude}, lng=${it.longitude}, accuracy=${it.accuracy}"
            )
            return it
        }

        val fallbackProviders = runCatching { locationManager.getProviders(true) }
            .onFailure {
                fail(
                    message = "GetCurrentLocationInfo: getProviders(true) failed while checking fallback providers",
                    cause = it
                )
            }
            .getOrDefault(emptyList())
            .filterNot { it in preferredProviders }
        logger.d("GetCurrentLocationInfo: checking fallback last known providers=$fallbackProviders")
        return fallbackProviders.firstNotNullOfOrNull { provider ->
            runCatching { locationManager.getLastKnownLocation(provider) }
                .onFailure {
                    fail(
                        message = "GetCurrentLocationInfo: getLastKnownLocation failed for fallback provider=$provider",
                        cause = it
                    )
                }
                .getOrNull()
        }?.also {
            logger.d(
                "GetCurrentLocationInfo: fallback last known location hit, provider=${it.provider.orEmpty()}, lat=${it.latitude}, lng=${it.longitude}, accuracy=${it.accuracy}"
            )
        }
    }

    private fun getEnabledProviders(
        locationManager: LocationManager,
        providerPreference: CurrentLocationProviderPreference
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
                    fail(
                        message = "GetCurrentLocationInfo: isProviderEnabled failed for provider=$provider",
                        cause = it
                    )
                }
                .getOrDefault(false)
        }.also {
            logger.d("GetCurrentLocationInfo: providerOrder=$providerOrder, enabledProviders=$it")
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