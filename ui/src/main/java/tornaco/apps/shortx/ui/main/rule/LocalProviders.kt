package tornaco.apps.shortx.ui.main.rule

import androidx.compose.runtime.staticCompositionLocalOf
import tornaco.apps.shortx.ui.main.model.RuleUM

interface RuleItemHandler {
    fun run(rule: RuleUM)
    fun dup(rule: RuleUM)
    fun delete(rule: RuleUM)
    fun edit(rule: RuleUM)
    fun click(rule: RuleUM)
    fun share(rule: RuleUM)
    fun moveToSet(rule: RuleUM, setId: String)
    fun moveOutSet(rule: RuleUM)
    fun stopAllJobs(rule: RuleUM)
    fun setRuleEnabled(rule: RuleUM, enable: Boolean)
    fun extractAsDA(title: String, rule: RuleUM, token: String?)
}

interface RuleSetItemHandler {
    fun dismiss(rule: RuleUM) {}
    fun delete(rule: RuleUM, alsoDeleteItems: Boolean) {}
    fun rename(rule: RuleUM, newName: String) {}
    fun click(rule: RuleUM) {}
    fun share(rule: RuleUM) {}

    // Batch op
    fun setRuleEnabled(rule: RuleUM, enable: Boolean) {}
}

val LocalRuleItemHandler = staticCompositionLocalOf<RuleItemHandler> {
    error("CompositionLocal LocalRuleItemHandler not present")
}
val LocalRuleSetItemHandler = staticCompositionLocalOf<RuleSetItemHandler> {
    error("CompositionLocal RuleSetItemHandler not present")
}