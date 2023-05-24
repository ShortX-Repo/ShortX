package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.StopExecuteException
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object BreakActionExecute : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.BreakActionExecute::class.java
    }

    override suspend fun handle(params: HandleParams) {
        throw StopExecuteException("BreakActionExecute action")
    }
}