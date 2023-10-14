package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.alarm.isNo
import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.period.RandomInPeriodScheduler

class RandomInPeriodTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("ShortXRandomInPeriodTrigger")
    private val scheduler by lazy {
        RandomInPeriodScheduler(context, shortXContext.rule.handler) { ruleTitle, ruleId, factId ->
            logger.i("onAlarm: $ruleTitle $ruleId $factId")

            val ruleOrNull = shortXContext.rule.getRuleById(ruleId)
            if (ruleOrNull == null) {
                logger.i("Rule with id: $ruleId not exists, will not publish fact.")
                return@RandomInPeriodScheduler
            }
            if (!ruleOrNull.isEnabled) {
                logger.i("Rule ${ruleOrNull.title} it not enabled now, will not publish fact.")
                return@RandomInPeriodScheduler
            }
            if (ruleOrNull.factsAndQuitFacts.all { !it.isPeriod() }) {
                logger.i("Rule ${ruleOrNull.title} fact and quit does not contains Period anymore, will not publish fact.")
                return@RandomInPeriodScheduler
            }

            val randomInPeriods =
                ruleOrNull.factsAndQuitFacts.filter { it.isPeriod() }
                    .map { it unpack_ RandomInPeriod::class.java }
            val targetFact = randomInPeriods.firstOrNull { it.id == factId }
            if (targetFact == null) {
                logger.i("Rule ${ruleOrNull.title} does not have this fact with id: $factId, will not publish fact.")
                return@RandomInPeriodScheduler
            }

            logger.i("Good, will publish fact.")
            shortXContext.rule.publishFact(
                reason = "Period#onAlarm",
                fact = RandomInPeriod.newBuilder()
                    .setId(factId)
                    .setStart(targetFact.start)
                    .setEnd(targetFact.end)
                    .setRepeat(targetFact.repeat).build(),
                initialContextDataLazy = { emptyMap() }
            )

            // Repeat
            if (!targetFact.repeat.isNo) {
                logger.i("Repeat period")
                schedule(ruleOrNull, targetFact, true, "PeriodTrigger.repeat")
            }
        }
    }

    override fun register() {
        scheduler.systemReady()
        shortXContext.rule.getAllEnabledRules().forEach { rule ->
            checkAndSchedulePeriodForRule(rule) { "PeriodTrigger.register for rule: ${rule.title}" }
        }
    }

    private fun checkAndSchedulePeriodForRule(rule: Rule, reason: () -> String) {
        if (rule.isEnabled) {
            val periods = rule.factsAndQuitFacts.filter { it.isPeriod() }
                .map { it unpack_ RandomInPeriod::class.java }
                .filter { it.hasStart() && it.hasEnd() }
            if (periods.isNotEmpty()) {
                periods.forEach { period ->
                    scheduler.schedule(
                        rule = rule,
                        period = period,
                        isForRepeat = false,
                        reason = reason()
                    )
                }
            }
        }
    }

    private fun cancelPeriodForRule(rule: Rule, reason: () -> String) {
        val periods =
            rule.factsAndQuitFacts.filter { it.isPeriod() }.map { it unpack_ RandomInPeriod::class.java }
        if (periods.isNotEmpty()) {
            periods.forEach { period ->
                scheduler.cancel(rule, period, reason())
            }
        }
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        checkAndSchedulePeriodForRule(rule) { "onAddRule: ${rule.title}" }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)

        if (oldRule.factsList == newRule.factsList && oldRule.quit.factsList == newRule.quit.factsList) {
            logger.d("Facts and quit not change for this rule(new label): ${newRule.title}")
            return
        }

        if (oldRule.factsList.filter { it.isPeriod() } == newRule.factsList.filter { it.isPeriod() }
            && oldRule.quit.factsList.filter { it.isPeriod() } == newRule.quit.factsList.filter { it.isPeriod() }) {
            logger.d("Period not change for this rule(new label): ${newRule.title}")
            return
        }

        // Cancel old, check new.
        cancelPeriodForRule(oldRule) { "onUpdateRule, cancel for old: ${oldRule.title}" }
        checkAndSchedulePeriodForRule(newRule) { "onUpdateRule, check for new: ${newRule.title}" }
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            checkAndSchedulePeriodForRule(it) { "onRuleEnabled: ${it.title}" }
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            cancelPeriodForRule(it) { "onRuleDisabled: ${it.title}" }
        }
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        cancelPeriodForRule(rule) { "onDeleteRule: ${rule.title}" }
    }

    override fun unRegister() {
        scheduler.shutdown()
    }

    private fun ProtoAny.isPeriod() = this is_ RandomInPeriod::class.java
}