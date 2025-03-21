package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireRingerMode : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireRingerMode::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireRingerMode::class.java
            val res = context.audioManager.ringerMode == data.mode.number
            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}