package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import com.google.protobuf.Message
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.HwImpl

object ToggleFlashLight : Actor {
    @SuppressLint("StaticFieldLeak")
    var hw: HwImpl? = null

    fun initHW(androidContext: Context, shortXContext: ShortXContext) {
        synchronized(ToggleFlashLight) {
            if (hw == null) {
                hw = HwImpl(androidContext, shortXContext)
            }
        }
    }

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ToggleFlashLight::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        initHW(androidContext, shortXContext)
        val enabled = hw?.isFlashlightEnabled() == true
        if (enabled) {
            hw?.disableFlashlight()
        } else {
            hw?.enabledFlashlight()
        }
        Actor.Res.EMPTY
    }
}
