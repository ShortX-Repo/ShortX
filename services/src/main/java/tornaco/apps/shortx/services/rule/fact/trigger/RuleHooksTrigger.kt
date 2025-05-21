package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.rule.RuleEditOp
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
                        val evaluateContext = EvaluateContext.newBuilder()
                            .setSrcId(it.id)
                            .setSrcEvent(ContextSrcEvent.ContextSrc_RuleHook)
                            .setSrcEventPayload1(RuleEditOp.RuleEditOp_Enable.name)
                            .setSrcTitle(it.title)
                            .build()
                        shortXContext.rule.executeActions(
                            context = evaluateContext,
                            actions = this,
                            conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                            actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
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
                        val evaluateContext = EvaluateContext.newBuilder()
                            .setSrcId(it.id)
                            .setSrcEvent(ContextSrcEvent.ContextSrc_RuleHook)
                            .setSrcEventPayload1(RuleEditOp.RuleEditOp_Disable.name)
                            .setSrcTitle(it.title)
                            .build()
                        shortXContext.rule.executeActions(
                            context = evaluateContext,
                            actions = this,
                            conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                            actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
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
                    val evaluateContext = EvaluateContext.newBuilder()
                        .setSrcId(rule.id)
                        .setSrcEvent(ContextSrcEvent.ContextSrc_RuleHook)
                        .setSrcEventPayload1(RuleEditOp.RuleEditOp_Remove.name)
                        .setSrcTitle(rule.title)
                        .build()
                    shortXContext.rule.executeActions(
                        context = evaluateContext,
                        actions = this,
                        conflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
                        actionAsyncMode = ActionAsyncMode.ActionAsyncMode_Sync
                    )
                }
            }
        }
    }
}