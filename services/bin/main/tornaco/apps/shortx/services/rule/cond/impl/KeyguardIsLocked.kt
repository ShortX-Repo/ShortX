package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.keyguardManager
import tornaco.apps.shortx.services.rule.cond.Cond

object KeyguardIsLocked : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.KeyguardIsLocked::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.KeyguardIsLocked::class.java
            val res = context.keyguardManager.isKeyguardLocked
            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}