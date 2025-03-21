package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object RequireIMEVisibility : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireIMEVisibility::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireIMEVisibility::class.java
            val res = shortXContext.input.isIMEShown() == data.isShown
            Cond.Result(
                if (data.isInvert) {
                    !res
                } else {
                    res
                }
            )
        }
    }
}