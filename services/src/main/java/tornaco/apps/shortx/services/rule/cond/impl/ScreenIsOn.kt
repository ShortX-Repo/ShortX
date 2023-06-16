package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.cond.Cond

object ScreenIsOn : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.ScreenIsOn::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.ScreenIsOn::class.java
            val res = context.powerManager.isInteractive

            if (data.isInvert) {
                !res
            } else {
                res
            }
        }
    }
}