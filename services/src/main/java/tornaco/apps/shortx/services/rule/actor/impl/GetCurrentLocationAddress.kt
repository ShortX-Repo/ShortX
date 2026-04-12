package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.action.wrap
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object GetCurrentLocationAddress : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.GetCurrentLocationAddress::class.java
    }

    override suspend fun handle(params: HandleParams): Actor.Res = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.GetCurrentLocationAddress::class.java
        logger.d(
            "GetCurrentLocationAddress start: providerPreference=${data.providerPreference}, timeoutMillis=${data.timeoutMillis}, hasCustomContextDataKey=${data.hasCustomContextDataKey()}"
        )

        val address = withContext(Dispatchers.IO) {
            AppLocationServiceProxy.getCurrentLocationAddress(
                context = androidContext,
                scope = this,
                requestData = data.toByteArray().wrap(),
                timeoutMillis = data.timeoutMillis,
            )
        } ?: error("Can not get current location address from App")
        val addressLine = address.addressLine

        logger.d(
            "GetCurrentLocationAddress success: addressLine=$addressLine, locality=${address.locality}, adminArea=${address.adminArea}, country=${address.countryName}"
        )

        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.addressLine.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.addressLine.name },
            addressLine
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.countryName.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.countryName.name },
            address.countryName
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.adminArea.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.adminArea.name },
            address.adminArea
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.subAdminArea.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.subAdminArea.name },
            address.subAdminArea
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.locality.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.locality.name },
            address.locality
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.subLocality.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.subLocality.name },
            address.subLocality
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.thoroughfare.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.thoroughfare.name },
            address.thoroughfare
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.subThoroughfare.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.subThoroughfare.name },
            address.subThoroughfare
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.postalCode.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.postalCode.name },
            address.postalCode
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationAddress.featureName.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationAddress.featureName.name },
            address.featureName
        )

        Actor.Res(addressLine)
    }
}