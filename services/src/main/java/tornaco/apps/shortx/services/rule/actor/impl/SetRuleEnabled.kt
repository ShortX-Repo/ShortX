package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object SetRuleEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetRuleEnabled::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SetRuleEnabled::class.java
        logger.i("isEnable: ${d.isEnable}, ruleId: ${d.ruleId}")
        requireNotNull(shortXContext.rule.getRuleById(d.ruleId)) {
            "Rule not exists: ${d.ruleId}"
        }
        shortXContext.rule.setRuleEnabled(d.ruleId, d.isEnable)
    }
}