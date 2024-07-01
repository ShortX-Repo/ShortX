package tornaco.apps.shortx.services.rule.fact.trigger.ws

import android.content.Context
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.Trigger

class WebSocketTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    override fun register() {
    }

    override fun unRegister() {
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
    }

}