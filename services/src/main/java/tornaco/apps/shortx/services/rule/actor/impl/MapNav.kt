package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.proto.action.MapNav
import tornaco.apps.shortx.core.proto.action.NavType
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
        val type = d.navType
        logger.d("Nav: $app $type")

        if (app == MapApp.Baidu) {
            if (type == NavType.NavType_Car || type == NavType.UNRECOGNIZED) {
                MapUtils.openBaiduNaviMap(
                    context = androidContext,
                    longitude = d.loc.longitude.toString(),
                    latitude = d.loc.latitude.toString()
                )
            } else {
                TODO("Baidu $type is not supported for now.")
            }

        } else if (app == MapApp.Gaode) {
            if (type == NavType.NavType_Car || type == NavType.UNRECOGNIZED) {
                MapUtils.openGaodeNaviMap(
                    context = androidContext,
                    appName = "ShortX",
                    poiName = d.poi.fallbackOnEmpty("Destination"),
                    latitude = d.loc.latitude.toString(),
                    longitude = d.loc.longitude.toString()
                )
            } else if (type == NavType.NavType_Ride) {
                MapUtils.openGaodeRideNaviMap(
                    context = androidContext,
                    appName = "ShortX",
                    latitude = d.loc.latitude.toString(),
                    longitude = d.loc.longitude.toString()
                )
            } else {
                TODO("Gaode $type is not supported for now.")
            }

        }
    }
}