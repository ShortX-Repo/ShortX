package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.MapApp
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.MapUtils
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object MapQueryBus : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.MapQueryBus::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.MapQueryBus::class.java
        val app = d.app
        require(app == MapApp.Gaode) {
            "Only support gaode"
        }
        logger.logOnDebug("Query: $app ${d.city} ${d.bus}")
        MapUtils.openGaodeBusMap(
            context = androidContext,
            appName = "ShortX",
            busName = d.bus,
            city = d.city
        )
        Actor.Res.EMPTY
    }
}