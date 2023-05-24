package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.FindAndClickViewByText
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object FindAndClickViewByText : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindAndClickViewByText::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ FindAndClickViewByText::class.java
        shortXContext.wm.findAndClickViewByText(
            text = d.text,
            isRegex = d.isRegex,
            timeout = maxOf(d.timeout, 1000L)
        )
    }
}