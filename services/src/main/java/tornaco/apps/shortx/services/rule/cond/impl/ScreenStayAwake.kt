package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.impl.StayAwake
import tornaco.apps.shortx.services.rule.cond.Cond

object ScreenStayAwake : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.ScreenStayAwake::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.ScreenStayAwake::class.java
            val res = StayAwake.isStayAwake()

            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}
