package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.services.rule.cond.Cond

object True : Cond {
    override fun condClass(): Class<out Message> = True::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return true
    }
}