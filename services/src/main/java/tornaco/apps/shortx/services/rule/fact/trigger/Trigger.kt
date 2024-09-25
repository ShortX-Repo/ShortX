package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.services.ShortXContext

interface Trigger {
    val context: Context
    val shortXContext: ShortXContext

    fun register()
    fun unRegister()

    fun onAddRule(rule: Rule) {
        onAnyChanged()
    }

    fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        onAnyChanged()
    }

    fun onDeleteRule(rule: Rule) {
        onAnyChanged()
    }

    fun onRuleEnabled(ruleId: String) {
        onAnyChanged()
    }

    fun onRuleDisabled(ruleId: String) {
        onAnyChanged()
    }

    fun onAddDA(da: DirectAction) {
        onAnyChanged()
    }

    fun onUpdateDA(oldDA: DirectAction, newDA: DirectAction) {
        onAnyChanged()
    }

    fun onDeleteDA(da: DirectAction) {
        onAnyChanged()
    }

    fun onAnyChanged() {}

    fun onScreenOff() {}
    fun onScreenOn() {}
}