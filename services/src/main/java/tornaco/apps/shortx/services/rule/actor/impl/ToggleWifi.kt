package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.HwImpl

object ToggleWifi : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ToggleWifi::class.java
    }

    override suspend fun handle(params: HandleParams) = with(params) {
        val hw = HwImpl(androidContext, shortXContext)
        if (hw.isWifiEnabled()) hw.disableWifi() else hw.enableWifi()
        Actor.Res.EMPTY
    }
}
