package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object EnableUniversalCopy : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.EnableUniversalCopy::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ):  Actor.Res = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.EnableUniversalCopy::class.java
        shortXContext.wm.withUiAutomation("enableUniversalCopy") {
            it.enableUniversalCopy(data.themeMode)
        }
        Actor.Res.EMPTY
    }
}
