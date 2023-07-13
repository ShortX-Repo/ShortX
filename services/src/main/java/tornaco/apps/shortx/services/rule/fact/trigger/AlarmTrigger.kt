package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.alarm.isNo
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.alarm.AlarmScheduler

class AlarmTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("ShortXAlarmTrigger")
    private val alarmScheduler by lazy {
        AlarmScheduler(context, shortXContext.rule.handler) { ruleTitle, ruleId, factId ->
            logger.i("onAlarm: $ruleTitle $ruleId $factId")

            val ruleOrNull = shortXContext.rule.getRuleById(ruleId)
            if (ruleOrNull == null) {
                logger.i("Rule with id: $ruleId not exists, will not publish fact.")
                return@AlarmScheduler
            }
            if (!ruleOrNull.isEnabled) {
                logger.i("Rule ${ruleOrNull.title} it not enabled now, will not publish fact.")
                return@AlarmScheduler
            }
            if (ruleOrNull.factsList.all { !it.isAlarm() }) {
                logger.i("Rule ${ruleOrNull.title} fact it not contains Alarm anymore, will not publish fact.")
                return@AlarmScheduler
            }

            val alarmList =
                ruleOrNull.factsList.filter { it.isAlarm() }.map { it unpack_ Alarm::class.java }
            val targetFact = alarmList.firstOrNull { it.id == factId }
            if (targetFact == null) {
                logger.i("Rule ${ruleOrNull.title} does not have this fact with id: $factId, will not publish fact.")
                return@AlarmScheduler
            }

            logger.i("Good, will publish fact.")
            shortXContext.rule.publishFact("onAlarm",
                Alarm.newBuilder().setTriggerAt(targetFact.triggerAt).setRepeat(targetFact.repeat)
                    .setId(factId).build()
            )

            // Repeat
            if (!targetFact.repeat.isNo) {
                logger.i("Repeat alarm")
                schedule(ruleOrNull, targetFact, "AlarmTrigger.repeat")
            }
        }
    }

    override fun register() {
        alarmScheduler.systemReady()
        shortXContext.rule.getAllEnabledRules().forEach { rule ->
            checkAndScheduleAlarmForRule(rule) { "AlarmTrigger.register for rule: ${rule.title}" }
        }
    }

    private fun checkAndScheduleAlarmForRule(rule: Rule, reason: () -> String) {
        if (rule.isEnabled) {
            val alarms = rule.factsList.filter { it.isAlarm() }.map { it unpack_ Alarm::class.java }
            if (alarms.isNotEmpty()) {
                alarms.forEach { alarm ->
                    alarmScheduler.schedule(
                        rule,
                        alarm,
                        reason()
                    )
                }
            }
        }
    }

    private fun cancelAlarmForRule(rule: Rule, reason: () -> String) {
        val alarms = rule.factsList.filter { it.isAlarm() }.map { it unpack_ Alarm::class.java }
        if (alarms.isNotEmpty()) {
            alarms.forEach { alarm ->
                alarmScheduler.cancel(rule, alarm, reason())
            }
        }
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        checkAndScheduleAlarmForRule(rule) { "onAddRule: ${rule.title}" }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)

        if (oldRule.factsList == newRule.factsList) {
            logger.d("Facts not change for this rule(new label): ${newRule.title}")
            return
        }

        if (oldRule.factsList.filter { it.isAlarm() } == newRule.factsList.filter { it.isAlarm() }) {
            logger.d("Alarm not change for this rule(new label): ${newRule.title}")
            return
        }

        // Cancel old, check new.
        cancelAlarmForRule(oldRule) { "onUpdateRule, cancel for old: ${oldRule.title}" }
        checkAndScheduleAlarmForRule(newRule) { "onUpdateRule, check for new: ${newRule.title}" }
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            checkAndScheduleAlarmForRule(it) { "onRuleEnabled: ${it.title}" }
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            cancelAlarmForRule(it) { "onRuleDisabled: ${it.title}" }
        }
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        cancelAlarmForRule(rule) { "onDeleteRule: ${rule.title}" }
    }

    override fun unRegister() {
        alarmScheduler.shutdown()
    }

    private fun ProtoAny.isAlarm() = this is_ Alarm::class.java
}