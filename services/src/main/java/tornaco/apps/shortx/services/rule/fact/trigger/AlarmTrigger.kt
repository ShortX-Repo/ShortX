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
        AlarmScheduler(context, shortXContext.rule.handler) { ruleId, tag ->
            logger.d("onAlarm: $ruleId $tag")

            val ruleOrNull = shortXContext.rule.getRuleById(ruleId)
            if (ruleOrNull == null) {
                logger.i("Rule with id: $ruleId not exists, will not publish fact.")
                return@AlarmScheduler
            }
            if (!ruleOrNull.isEnabled) {
                logger.i("Rule ${ruleOrNull.title} it not enabled now, will not publish fact.")
                return@AlarmScheduler
            }
            if (!(ruleOrNull.fact.isAlarm())) {
                logger.i("Rule ${ruleOrNull.title} fact it not Alarm anymore, will not publish fact.")
                return@AlarmScheduler
            }

            val alarm = ruleOrNull.fact unpack_ Alarm::class.java
            if (alarm.tag != tag) {
                logger.i("Rule ${ruleOrNull.title} tag changed to: ${alarm.tag}, will not publish fact.")
                return@AlarmScheduler
            }

            logger.i("Good, will publish fact.")
            shortXContext.rule.publishFact("onAlarm", Alarm.newBuilder().setTag(tag).build())

            // Repeat
            if (!alarm.repeat.isNo) {
                logger.i("Repeat alarm")
                schedule(ruleOrNull, alarm, "AlarmTrigger.repeat")
            } else {
                logger.i("Disable rule since the alarm is not repeat.")
                shortXContext.rule.setRuleEnabled(ruleId, false)
            }
        }
    }

    override fun register() {
        alarmScheduler.systemReady()
        shortXContext.rule.getAllRules().filter {
            it.isEnabled && it.fact.isAlarm()
        }.forEach {
            val alarm = it.fact unpack_ Alarm::class.java
            alarmScheduler.schedule(it, alarm, "AlarmTrigger.register")
        }
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        if (rule.fact.isAlarm()) {
            val alarm = rule.fact unpack_ Alarm::class.java
            alarmScheduler.cancel(alarm, "onAddRule")
            if (rule.isEnabled) alarmScheduler.schedule(rule, alarm, "onAddRule")
        }
    }

    override fun onUpdateRule(oldRule: Rule, newRule: Rule) {
        super.onUpdateRule(oldRule, newRule)

        // FAST: If both old or new is not alarm, skip.
        if (!oldRule.fact.isAlarm() && !newRule.fact.isAlarm()) {
            logger.d("Both old or new rule is not alarm.")
            return
        }

        // FAST: If alarm not changed, skip.
        if (oldRule.fact.isAlarm() && newRule.fact.isAlarm()) {
            val oldAlarm = oldRule.fact unpack_ Alarm::class.java
            val newAlarm = newRule.fact unpack_ Alarm::class.java
            if (oldAlarm == newAlarm) {
                logger.d("Alarm not change for this rule(new label): ${newRule.title}")
                return
            }
        }

        if (oldRule.fact.isAlarm()) {
            val alarm = oldRule.fact unpack_ Alarm::class.java
            alarmScheduler.cancel(alarm, "onUpdateRule, cancel old")
        }

        if (newRule.fact.isAlarm()) {
            val alarm = newRule.fact unpack_ Alarm::class.java
            alarmScheduler.cancel(alarm, "onUpdateRule")
            if (newRule.isEnabled) alarmScheduler.schedule(
                rule = newRule,
                alarm = alarm,
                reason = "onUpdateRule, schedule new"
            )
        }
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            if (it.isEnabled && it.fact.isAlarm()) {
                val alarm = it.fact unpack_ Alarm::class.java
                alarmScheduler.cancel(alarm, "onRuleEnabled")
                alarmScheduler.schedule(it, alarm, "onRuleEnabled")
            }
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            if (!it.isEnabled && it.fact.isAlarm()) {
                val alarm = it.fact unpack_ Alarm::class.java
                alarmScheduler.cancel(alarm, "onRuleDisabled")
            }
        }
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        logger.d("onDeleteRule: ${rule.title}")
        if (rule.fact.isAlarm()) {
            val alarm = rule.fact unpack_ Alarm::class.java
            alarmScheduler.cancel(alarm, "onDeleteRule")
        }
    }

    override fun unRegister() {
        alarmScheduler.shutdown()
    }

    private fun ProtoAny.isAlarm() = this is_ Alarm::class.java
}