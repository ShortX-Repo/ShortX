package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.RuleObserver
import tornaco.apps.shortx.services.ShortXContext

class RulePublishInterceptor(private val shortXContext: ShortXContext) {

    init {
        shortXContext.rule.registerRuleObs(object : RuleObserver() {
            override fun onRuleChanged(id: String) {
                super.onRuleChanged(id)
            }

            override fun onRuleSetChanged(id: String) {
                super.onRuleSetChanged(id)
            }
        })
    }

    fun shouldPublish() {
        val enabledRules = shortXContext.rule.getAllEnabledRules()
        val enabledRuleFacts: List<ProtoAny> =
            enabledRules.map { it.factsList }.flatten().distinct()

        val enabledRuleConditions: List<ProtoAny> =
            enabledRules.map { it.conditionsList }.flatten().distinct()

        val enabledRuleActions: List<ProtoAny> =
            enabledRules.map { it.actionsList }.flatten().distinct()
    }


}