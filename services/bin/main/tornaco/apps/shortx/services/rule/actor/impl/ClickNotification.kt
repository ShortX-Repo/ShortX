package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ClickNotification : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ClickNotification::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        withContext(Dispatchers.Main) {
            val d =
                action unpack_ tornaco.apps.shortx.core.proto.action.ClickNotification::class.java
            val cv = RuleContextAndVars(params)
            val n = Notification.newBuilder(d.notification)
                .setTitle(d.notification.title.compileContextAndVars(cv))
                .setContentText(d.notification.contentText.compileContextAndVars(cv))
                .setTag(d.notification.tag.compileContextAndVars(cv))
                .build()
            shortXContext.notification.clickNotification(n)

            Actor.Res.EMPTY
        }
    }
}