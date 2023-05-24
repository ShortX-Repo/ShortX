package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.SetDataEnabled
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.DataImpl


object SetDataEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetDataEnabled::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val data = DataImpl(context, shortXContext)
        val d = action unpack_ SetDataEnabled::class.java
        if (d.hasSpecificSlotId) {
            data.setDataEnabledForSlot(d.slotId, d.enable)
        } else {
            data.isDataEnabled = d.enable
        }
    }
}