package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.False
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object True : Cond {
    override fun condClass(): Class<out Message> = True::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        val f = param.condition unpack_ False::class.java
        return !f.isInvert
    }
}