package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.fact.FixedInPeriod
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.alarm.acquireWakeLockForPublishFactAndNextAlarmSchedule
import tornaco.apps.shortx.services.rule.fact.fixed.FixedInPeriodScheduler
import tornaco.apps.shortx.services.watchdog.launchSafety

class FixedInPeriodTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("FixedInPeriodTrigger")
    private val scope by lazy { CoroutineScope(SupervisorJob()) }
    private val fixedInPeriodScheduler by lazy {
        FixedInPeriodScheduler(context, shortXContext.rule.handler) { ruleTitle, ruleId, factId ->
            logger.i("FixedInPeriodTrigger onAlarm: $ruleTitle $ruleId $factId")

            val ruleOrNull = shortXContext.rule.getRuleById(ruleId)
            if (ruleOrNull == null) {
                logger.i("Rule with id: $ruleId not exists, will not publish fact.")
                return@FixedInPeriodScheduler
            }
            if (!ruleOrNull.isEnabled) {
                logger.i("Rule ${ruleOrNull.title} it not enabled now, will not publish fact.")
                return@FixedInPeriodScheduler
            }
            if (ruleOrNull.factsAndQuitFacts.all { !it.isFixedInPeriod() }) {
                logger.i("Rule ${ruleOrNull.title} fact and quit does not contains FixedInPeriod anymore, will not publish fact.")
                return@FixedInPeriodScheduler
            }

            val fixedInPeriodList =
                ruleOrNull.factsAndQuitFacts.filter { it.isFixedInPeriod() }
                    .map { it unpack_ FixedInPeriod::class.java }
            val targetFact = fixedInPeriodList.firstOrNull { it.id == factId }
            if (targetFact == null) {
                logger.i("Rule ${ruleOrNull.title} does not have this fact with id: $factId, will not publish fact.")
                return@FixedInPeriodScheduler
            }

            val wakeLock = context.acquireWakeLockForPublishFactAndNextAlarmSchedule("FixedPeriod")
            logger.i("Good, will publish fact: $wakeLock")

            shortXContext.rule.publishFact(
                reason = "onAlarm",
                fact = FixedInPeriod.newBuilder().setStart(targetFact.start)
                    .setEnd(targetFact.end)
                    .setFixedBy(targetFact.fixedBy)
                    .setRepeat(targetFact.repeat)
                    .apply {
                        if (targetFact.hasWorkdayFilter()) {
                            setWorkdayFilter(targetFact.workdayFilter)
                        }
                    }
                    .setId(factId).build(),
                initialContextDataLazy = { emptyMap() }
            )

            scope.launchSafety("FixedInPeriodTrigger.next") {
                // Schedule after 3s
                logger.i("Schedule Next FixedInPeriod")
                delay(3000)
                schedule(ruleOrNull, targetFact, "FixedInPeriodTrigger.Next")
                wakeLock?.release()
            }
        }
    }

    override fun register() {
        scope.launch {
            delay(ALARM_SCHEDULE_DELAY)
            shortXContext.rule.getAllEnabledRules().forEach { rule ->
                checkAndScheduleFixedInPeriodForRule(rule) { "FixedInPeriodTrigger.register for rule: ${rule.title}" }
            }
        }
    }

    private fun checkAndScheduleFixedInPeriodForRule(rule: Rule, reason: () -> String) {
        if (rule.isEnabled) {
            val fixedInPeriods =
                rule.factsAndQuitFacts.filter { it.isFixedInPeriod() }
                    .map { it unpack_ FixedInPeriod::class.java }
                    .filter { it.hasFixedBy() && it.hasStart() && it.hasEnd() }
            if (fixedInPeriods.isNotEmpty()) {
                fixedInPeriods.forEach { period ->
                    fixedInPeriodScheduler.schedule(
                        rule,
                        period,
                        reason()
                    )
                }
            }
        }
    }

    private fun cancelFixedInPeriodForRule(rule: Rule, reason: () -> String) {
        val fixedInPeriods =
            rule.factsAndQuitFacts.filter { it.isFixedInPeriod() }
                .map { it unpack_ FixedInPeriod::class.java }
        if (fixedInPeriods.isNotEmpty()) {
            fixedInPeriods.forEach { period ->
                fixedInPeriodScheduler.cancel(rule, period, reason())
            }
        }
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        checkAndScheduleFixedInPeriodForRule(rule) { "onAddRule: ${rule.title}" }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)

        if (oldRule.factsList == newRule.factsList && oldRule.quit.factsList == newRule.quit.factsList) {
            logger.d("Facts and quit not change for this rule(new label): ${newRule.title}")
            return
        }

        if (oldRule.factsList.filter { it.isFixedInPeriod() } == newRule.factsList.filter { it.isFixedInPeriod() }
            && oldRule.quit.factsList.filter { it.isFixedInPeriod() } == newRule.quit.factsList.filter { it.isFixedInPeriod() }) {
            logger.d("FixedInPeriodTrigger not change for this rule(new label): ${newRule.title}")
            return
        }

        // Cancel old, check new.
        cancelFixedInPeriodForRule(oldRule) { "onUpdateRule, cancel for old: ${oldRule.title}" }
        checkAndScheduleFixedInPeriodForRule(newRule) { "onUpdateRule, check for new: ${newRule.title}" }
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            checkAndScheduleFixedInPeriodForRule(it) { "onRuleEnabled: ${it.title}" }
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            cancelFixedInPeriodForRule(it) { "onRuleDisabled: ${it.title}" }
        }
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        cancelFixedInPeriodForRule(rule) { "onDeleteRule: ${rule.title}" }
    }

    override fun unRegister() {
    }

    private fun ProtoAny.isFixedInPeriod() = this is_ FixedInPeriod::class.java
}