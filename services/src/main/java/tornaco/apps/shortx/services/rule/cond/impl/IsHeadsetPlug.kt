package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.services.rule.cond.Cond

object IsHeadsetPlug : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.IsHeadsetPlug::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.IsHeadsetPlug::class.java)

        val res = data.isPlug == param.context.audioManager.isWiredHeadsetOn

        return if (data.isInvert) {
            !res
        } else {
            res
        }.toResult()
    }
}