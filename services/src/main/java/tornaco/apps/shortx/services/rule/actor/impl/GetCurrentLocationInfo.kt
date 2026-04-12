package tornaco.apps.shortx.services.rule.actor.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Process
import com.google.protobuf.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import kotlin.coroutines.resume

object GetCurrentLocationInfo : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo::class.java
    }

    override suspend fun handle(params: HandleParams): Actor.Res = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo::class.java
        if (!hasLocationPermission(androidContext)) {
            error("Location permission is not granted")
        }

        val location = currentLocation(androidContext) ?: error("Current location unavailable")

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

    private fun hasLocationPermission(context: Context): Boolean {
        return context.checkPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED ||
            context.checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Process.myPid(),
                Process.myUid()
            ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(context: Context): Location? {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

        bestLastKnownLocation(locationManager)?.let { return it }

        val providers = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
            .filter { provider ->
                runCatching { locationManager.isProviderEnabled(provider) }.getOrDefault(false)
            }

        for (provider in providers) {
            val location = suspendCancellableCoroutine<Location?> { continuation ->
                runCatching {
                    locationManager.getCurrentLocation(provider, null, context.mainExecutor) {
                        if (continuation.isActive) {
                            continuation.resume(it)
                        }
                    }
                }.onFailure {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
            if (location != null) {
                return location
            }
        }

        return bestLastKnownLocation(locationManager)
    }

    @SuppressLint("MissingPermission")
    private fun bestLastKnownLocation(locationManager: LocationManager): Location? {
        val providers = runCatching { locationManager.getProviders(true) }.getOrDefault(emptyList())
        return providers.mapNotNull { provider ->
            runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
        }.maxByOrNull { it.time }
            ?: runCatching { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) }.getOrNull()
            ?: runCatching { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) }.getOrNull()
    }
}