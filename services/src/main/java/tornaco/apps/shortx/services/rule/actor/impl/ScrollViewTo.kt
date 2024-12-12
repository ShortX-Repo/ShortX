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

        when (location) {
            ScrollViewToLocation.Top -> {
                shortXContext.wm.withUiAutomation("ScrollViewTo") {
                    it.viewScroller.scrollToTop()
                }
            }

            ScrollViewToLocation.Bottom -> {
                shortXContext.wm.withUiAutomation("ScrollViewTo") {
                    it.viewScroller.scrollToBottom()
                }
            }

            ScrollViewToLocation.Forward -> {
                shortXContext.wm.withUiAutomation("ScrollViewTo") {
                    it.viewScroller.scrollForward()
                }
            }

            ScrollViewToLocation.Backward -> {
                shortXContext.wm.withUiAutomation("ScrollViewTo") {
                    it.viewScroller.scrollBackward()
                }
            }

            ScrollViewToLocation.UNRECOGNIZED -> {
                throw Error("UNRECOGNIZED")
            }

            ScrollViewToLocation.TopForce -> {
                shortXContext.wm.viewScroller.scrollToForce(vS = 100000f)
            }

            ScrollViewToLocation.BottomForce -> {
                shortXContext.wm.viewScroller.scrollToForce(vS = -100000f)
            }
        }
        Actor.Res.EMPTY
    }
}