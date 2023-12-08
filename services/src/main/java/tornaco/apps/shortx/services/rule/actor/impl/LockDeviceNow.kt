package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.wm.WMSFunc


object LockDeviceNow : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.LockDeviceNow::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        WMSFunc.lockDeviceNow()
    }
}