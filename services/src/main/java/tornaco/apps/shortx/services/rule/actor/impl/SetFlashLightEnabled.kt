package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.SetFlashLightEnabled
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.HwImpl


object SetFlashLightEnabled : Actor {
    @SuppressLint("StaticFieldLeak")
    private var hw: HwImpl? = null

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetFlashLightEnabled::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        synchronized(SetFlashLightEnabled) {
            if (hw == null) {
                hw = HwImpl(context, shortXContext)
            }
        }
        val d = action unpack_ SetFlashLightEnabled::class.java
        if (d.enable) hw?.enabledFlashlight() else hw?.disableFlashlight()
    }
}