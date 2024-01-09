package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object PostNotification : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.PostNotification::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d = action unpack_ PostNotification::class.java
            val cv = RuleContextAndVars(params)
            val inflatedTitle = d.title.compileContextAndVars(cv)
            val inflatedMessage = d.message.compileContextAndVars(cv)
            val inflatedN =
                PostNotification.newBuilder(d).setTitle(inflatedTitle).setMessage(inflatedMessage)
                    .build()
            shortXContext.notification.post(inflatedN)
        }
    }
}