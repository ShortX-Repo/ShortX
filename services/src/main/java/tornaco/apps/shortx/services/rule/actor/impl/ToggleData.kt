package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.ToggleData
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.DataImpl

object ToggleData : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ToggleData::class.java
    }

    override suspend fun handle(params: HandleParams) = with(params) {
        val dataImpl = DataImpl(androidContext, shortXContext)
        val d = action unpack_ ToggleData::class.java
        if (d.hasSpecificSlotId) {
            val enabled = dataImpl.isDataEnabledForSlot(d.slotId)
            dataImpl.setDataEnabledForSlot(d.slotId, !enabled)
        } else {
            dataImpl.setDataEnabled(!dataImpl.isDataEnabled)
        }
        Actor.Res.EMPTY
    }
}
