package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.FindPointsByColor.handleAction

object FindPointsByColorAndClick : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindPointsByColorAndClick::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindPointsByColorAndClick::class.java
        val points = handleAction(
            "FindPointsByColorAndClick",
            d.config,
            androidContext,
            evaluateContext,
            shortXContext
        )

        val firstPoint = points.firstOrNull()
        requireNotNull(firstPoint) {
            "No point found"
        }

        shortXContext.wm.withUiAutomation("FindPointsByColorAndClick") {
            it.click(firstPoint.x, firstPoint.y)
        }

        Actor.Res.EMPTY
    }
}