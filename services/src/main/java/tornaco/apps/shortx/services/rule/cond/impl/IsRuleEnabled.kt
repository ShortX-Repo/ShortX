package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object IsRuleEnabled : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.IsRuleEnabled::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.IsRuleEnabled::class.java)
        val isEnabled = requireNotNull(param.shortXContext.rule.getRuleById(data.ruleId)).isEnabled
        return if (data.isInvert) {
            !isEnabled
        } else {
            isEnabled
        }.toResult()
    }
}