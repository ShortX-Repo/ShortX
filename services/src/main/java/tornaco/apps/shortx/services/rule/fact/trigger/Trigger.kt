package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety

interface Trigger {
    val context: Context
    val shortXContext: ShortXContext

    fun register()
    fun unRegister()

    fun onAddRule(rule: Rule) {}
    fun onUpdateRule(oldRule: Rule, newRule: Rule) {}
    fun onDeleteRule(rule: Rule) {}
    fun onRuleEnabled(ruleId: String) {}
    fun onRuleDisabled(ruleId: String) {}

    fun onAddOrUpdateDA(daId: String) {}
    fun onDeleteDA(daId: String) {}
}

fun Trigger.withRuleServiceScope(block: suspend CoroutineScope.() -> Unit) {
    shortXContext.rule.systemServiceScope.launchSafety {
        block()
    }
}