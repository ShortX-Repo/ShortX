package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
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
            val inflatedTitle =
                d.title.inflateContextMap(contextData)
                    .replaceLocalVarValues(localVars)
                    .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
            val inflatedMessage =
                d.message.inflateContextMap(contextData)
                    .replaceLocalVarValues(localVars)
                    .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
            val inflatedN =
                PostNotification.newBuilder(d).setTitle(inflatedTitle).setMessage(inflatedMessage)
                    .build()
            shortXContext.notification.post(inflatedN)
        }
    }
}