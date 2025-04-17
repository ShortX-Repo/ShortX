package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.ShowPPN
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object RemovePPN : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.RemovePPN::class.java
    }

    override suspend fun handle(params: HandleParams): Actor.Res {
        return Actor.Res.EMPTY
    }
}

object ShowPPN : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowPPN::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d = action unpack_ ShowPPN::class.java
            val cv = RuleContextAndVars(params)
            val inflatedTitle = d.title.compileContextAndVars(cv)
            val inflatedMessage = d.message.compileContextAndVars(cv)

            val inflatedN =
                ShowPPN.newBuilder(d)
                    .setTitle(inflatedTitle)
                    .setMessage(inflatedMessage)
                    .build()
            shortXContext.notification.showPPN(inflatedN)
            Actor.Res.EMPTY
        }
    }
}