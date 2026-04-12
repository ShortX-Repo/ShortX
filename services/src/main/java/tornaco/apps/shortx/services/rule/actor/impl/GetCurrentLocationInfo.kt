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

object GetCurrentLocationInfo : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo::class.java
    }

    override suspend fun handle(params: HandleParams): Actor.Res = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo::class.java
        logger.d(
            "GetCurrentLocationInfo start: providerPreference=${data.providerPreference}, timeoutMillis=${data.timeoutMillis}, hasCustomContextDataKey=${data.hasCustomContextDataKey()}"
        )
        val location = withContext(Dispatchers.IO) {
            AppLocationServiceProxy.getCurrentLocationInfo(
                context = androidContext,
                scope = this,
                requestData = data.toByteArray().wrap(),
                timeoutMillis = data.timeoutMillis,
            )
        } ?: error("Can not get current location info from App")

        logger.d(
            "GetCurrentLocationInfo success: provider=${location.provider}, lat=${location.latitude}, lng=${location.longitude}, accuracy=${location.accuracy}"
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
            location.provider
        )
        contextData.add(
            data.customContextDataKey.forKey(ContextDataMapping.GetCurrentLocationInfo.accuracy.name)
                .fallbackOnEmpty { ContextDataMapping.GetCurrentLocationInfo.accuracy.name },
            location.accuracy.toString()
        )

        Actor.Res(
            "lat=${location.latitude}, lng=${location.longitude}, provider=${location.provider}, accuracy=${location.accuracy}"
        )
    }
}