package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.quitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.services.ShortXContext

abstract class DAAndRuleAwareTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    abstract val factClass: Class<out com.google.protobuf.Message>

    abstract fun updateTriggerState()

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        if (rule != null && rule.factsAndQuitFacts.any { it is_ factClass }) {
            updateTriggerState()
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        if (rule != null && rule.factsAndQuitFacts.any { it is_ factClass }) {
            updateTriggerState()
        }
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        if (rule.factsAndQuitFacts.any { it is_ factClass }) {
            updateTriggerState()
        }
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        if (rule.factsAndQuitFacts.any { it is_ factClass }) {
            updateTriggerState()
        }
    }

    override fun onAddDA(da: DirectAction) {
        super.onAddDA(da)
        if (da.quitFacts.any { it is_ factClass }) {
            updateTriggerState()
        }
    }

    override fun onUpdateDA(oldDA: DirectAction, newDA: DirectAction) {
        super.onUpdateDA(oldDA, newDA)
        if (oldDA.quitFacts.any { it is_ factClass } || newDA.quitFacts.any { it is_ factClass }) {
            updateTriggerState()
        }
    }

    override fun onDeleteDA(da: DirectAction) {
        super.onDeleteDA(da)
        if (da.quitFacts.any { it is_ factClass }) {
            updateTriggerState()
        }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)
        if (oldRule.factsAndQuitFacts.any { it is_ factClass }
            || newRule.factsAndQuitFacts.any { it is_ factClass }) {
            updateTriggerState()
        }
    }
}