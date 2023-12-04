package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.ScrollViewToLocation
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ScrollViewTo : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ScrollViewTo::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ScrollViewTo::class.java
        val location: ScrollViewToLocation = d.location
        logger.d("location = $location")
        shortXContext.wm.withUiAutomation("ScrollViewTo") {
            when (location) {
                ScrollViewToLocation.Top -> {
                    it.scrollToTop()
                }

                ScrollViewToLocation.Bottom -> {
                    it.scrollToBottom()
                }

                ScrollViewToLocation.Forward -> {
                    it.scrollForward()
                }

                ScrollViewToLocation.Backward -> {
                    it.scrollBackward()
                }

                ScrollViewToLocation.UNRECOGNIZED -> {
                    throw Error("UNRECOGNIZED")
                }

            }
        }
    }
}