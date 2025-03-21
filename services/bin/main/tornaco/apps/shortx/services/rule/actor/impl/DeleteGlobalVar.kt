package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.DeleteGlobalVar
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object DeleteGlobalVar : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.DeleteGlobalVar::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d = action unpack_ DeleteGlobalVar::class.java
        logger.d("DeleteGlobalVar: ${d.varName}")
        d.varName?.let {
            shortXContext.rule.deleteGlobalVar(it)
        }
        Actor.Res.EMPTY
    }
}