package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.False
import tornaco.apps.shortx.services.rule.cond.Cond

object False : Cond {
    override fun condClass(): Class<out Message> = False::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return false
    }
}