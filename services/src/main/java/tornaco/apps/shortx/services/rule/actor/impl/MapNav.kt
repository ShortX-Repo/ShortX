package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.proto.action.MapNav
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.MapUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object MapNav : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.MapNav::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ MapNav::class.java
        val app = d.app
        if (app == MapApp.Baidu) {
            kotlin.runCatching {
                MapUtils.openBaiduNaviMap(
                    context = androidContext,
                    longitude = d.loc.longitude.toString(),
                    latitude = d.loc.latitude.toString()
                )
            }.onFailure {
                logger.e(it, "Baidu Nav error")
            }
        } else if (app == MapApp.Gaode) {
            kotlin.runCatching {
                MapUtils.openGaodeNaviMap(
                    context = androidContext,
                    appName = "ShortX",
                    poiName = d.poi.fallbackOnEmpty("Destination"),
                    latitude = d.loc.latitude.toString(),
                    longitude = d.loc.longitude.toString()
                )
            }.onFailure {
                logger.e(it, "Gaode Nav error")
            }
        }
    }
}