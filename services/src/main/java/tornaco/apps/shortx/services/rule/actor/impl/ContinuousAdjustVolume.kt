package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ContinuousAdjustVolume : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ContinuousAdjustVolume::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        logger.i("ContinuousAdjustVolume is executed by EdgeGestureEngine during gesture handling")
        Actor.Res.EMPTY
    }
}