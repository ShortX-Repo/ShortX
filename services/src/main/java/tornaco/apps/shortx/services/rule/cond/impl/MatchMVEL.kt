package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.MatchMVEL
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.impl.MVELExecutor
import tornaco.apps.shortx.services.rule.cond.Cond

object MatchMVEL : Cond {
    override fun condClass(): Class<out Message> = MatchMVEL::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val mvel = condition unpack_ MatchMVEL::class.java
            val res = MVELExecutor(context, shortXContext, mvel.expression).invoke()
            val retValue = kotlin.runCatching { res as Boolean }.getOrElse {
                logger.e(it, "MatchMVEL check error")
                false
            }
            if (mvel.isInvert) {
                !retValue
            } else {
                retValue
            }
        }
    }
}