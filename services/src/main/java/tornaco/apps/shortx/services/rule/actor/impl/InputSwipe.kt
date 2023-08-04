package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object InputSwipe : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.InputSwipe::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.InputSwipe::class.java

        require(androidContext.powerManager.isInteractive) {
            "Can not swipe when device is not interactive"
        }

        shortXContext.wm.withUiAutomation("InputSwipe") {
            it.swipe(data.startX, data.startY, data.endX, data.endY, data.swipeTime)
        }
    }
}