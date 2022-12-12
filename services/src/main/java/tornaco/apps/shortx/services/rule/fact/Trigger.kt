package tornaco.apps.shortx.services.rule.fact

import android.content.Context
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.services.ShortXContext

interface Trigger {
    val context: Context
    val shortXContext: ShortXContext

    fun register()
    fun unRegister()

    fun onAddRule(rule: Rule) {}
    fun onDeleteRule() {}
    fun onRuleEnabled(ruleId: String) {}
    fun onRuleDisabled(ruleId: String) {}
}