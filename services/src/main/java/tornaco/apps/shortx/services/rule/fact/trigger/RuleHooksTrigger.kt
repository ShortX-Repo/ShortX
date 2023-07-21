package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
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
                            id = it.id,
                            reason = "Hook: actionsOnEnabled",
                            actions = this,
                            initialContextData = mapOf(
                                ContextDataMapping.Context.contextSrcTitle.name to "On rule enabled: ${it.title}",
                                ContextDataMapping.Context.contextSrcEvent.name to ContextSrcEvent.ContextSrc_RuleHook,
                            ),
                            ConflictStrategy.ConflictStrategy_ExecuteBoth
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
                            id = it.id,
                            reason = "Hook: actionsOnDisabled",
                            actions = this,
                            initialContextData = mapOf(
                                ContextDataMapping.Context.contextSrcTitle.name to "On rule disabled: ${it.title}",
                                ContextDataMapping.Context.contextSrcEvent.name to ContextSrcEvent.ContextSrc_RuleHook,
                            ),
                            ConflictStrategy.ConflictStrategy_ExecuteBoth
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
                        id = rule.id,
                        reason = "Hook: actionsOnDeleted",
                        actions = this,
                        initialContextData = mapOf(
                            ContextDataMapping.Context.contextSrcTitle.name to "On rule deleted: ${rule.title}",
                            ContextDataMapping.Context.contextSrcEvent.name to ContextSrcEvent.ContextSrc_RuleHook,
                        ),
                        ConflictStrategy.ConflictStrategy_ExecuteBoth
                    )
                }
            }
        }
    }
}