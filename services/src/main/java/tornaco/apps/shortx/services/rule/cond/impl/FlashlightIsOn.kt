package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.impl.ToggleFlashLight
import tornaco.apps.shortx.services.rule.cond.Cond

object FlashlightIsOn : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.FlashlightIsOn::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.FlashlightIsOn::class.java
            ToggleFlashLight.initHW(context, shortXContext)
            val flashLightEnabled = ToggleFlashLight.hw?.isFlashlightEnabled ?: false
            if (data.isInvert) {
                !flashLightEnabled
            } else {
                flashLightEnabled
            }.toResult()
        }
    }
}
