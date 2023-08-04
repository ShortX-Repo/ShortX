package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.HwImpl


object SetLocationEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetLocationEnabled::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val hw = HwImpl(androidContext, shortXContext)
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SetLocationEnabled::class.java
        if (d.enable) hw.enableLocation() else hw.disableLocation()
    }
}