package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.BreakActionException
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object BreakActionExecute : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.BreakActionExecute::class.java
    }

    override suspend fun handle(params: HandleParams) {
        val d = params.action.unpack(tornaco.apps.shortx.core.proto.action.BreakActionExecute::class.java)
        d.scope
        throw BreakActionException("BreakActionExecute action")
    }
}