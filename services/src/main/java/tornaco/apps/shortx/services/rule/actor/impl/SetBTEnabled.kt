package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.SetBTEnabled
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.HwImpl


object SetBTEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetBTEnabled::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val hw = HwImpl(androidContext, shortXContext)
        val d = action unpack_ SetBTEnabled::class.java
        if (d.enable) hw.enableBT() else hw.disableBT()
    }
}