package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.SetHotSpotEnabled
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.HwImpl


object SetHotSpotEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetHotSpotEnabled::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val hw = HwImpl(context, shortXContext)
        val d = action unpack_ SetHotSpotEnabled::class.java
        if (d.enable) hw.enableTethering() else hw.disableTethering()
    }
}