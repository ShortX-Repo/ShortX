package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ContinuousGesturePointer : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ContinuousGesturePointer::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        logger.i("ContinuousGesturePointer is executed by EdgeGestureEngine during gesture handling")
        Actor.Res.EMPTY
    }
}