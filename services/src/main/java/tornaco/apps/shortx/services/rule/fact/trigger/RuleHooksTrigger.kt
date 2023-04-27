package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

class RuleHooksTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("RuleHooksTrigger")

    override fun register() {

    }

    override fun unRegister() {
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        logger.d("onRuleEnabled: $ruleId")
        shortXContext.rule.getRuleById(ruleId)?.let {
            it.hook?.let { hook ->
                hook.actionsOnEnabledList.apply {
                    if (isNotEmpty()) {
                        shortXContext.rule.executeActions(
                            it.id,
                            "Hook: actionsOnEnabled",
                            this,
                            emptyMap()
                        )
                    }
                }
            }
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        logger.d("onRuleDisabled: $ruleId")
        shortXContext.rule.getRuleById(ruleId)?.let {
            it.hook?.let { hook ->
                hook.actionsOnDisabledList.apply {
                    if (isNotEmpty()) {
                        shortXContext.rule.executeActions(
                            it.id,
                            "Hook: actionsOnDisabled",
                            this,
                            emptyMap()
                        )
                    }
                }
            }
        }
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        logger.d("onDeleteRule: ${rule.id}")

        rule.hook?.let { hook ->
            hook.actionsOnDeletedList.apply {
                if (isNotEmpty()) {
                    shortXContext.rule.executeActions(
                        rule.id,
                        "Hook: actionsOnDeleted",
                        this,
                        emptyMap()
                    )
                }
            }
        }
    }
}