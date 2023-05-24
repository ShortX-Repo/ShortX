package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.proto.action.Delay
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object Delay : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.Delay::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        val d = params.action unpack_ Delay::class.java
        delay(maxOf(0, d.time))
    }
}