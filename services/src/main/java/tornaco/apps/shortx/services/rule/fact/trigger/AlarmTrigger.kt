package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.rule.Rule
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
    private val logger = Logger("AlarmTrigger")
    private val alarmScheduler by lazy {
        AlarmScheduler(context, shortXContext.rule.handler) {
            logger.d("onAlarm: $it")
            shortXContext.rule.publishFact("onAlarm", Alarm.newBuilder().setTag(it).build())
        }
    }

    override fun register() {
        alarmScheduler.systemReady()
        shortXContext.rule.getAllRules().filter {
            it.isEnabled && it.fact is_ Alarm::class.java
        }.forEach {
            val alarm = it.fact unpack_ Alarm::class.java
            alarmScheduler.schedule(alarm, "AlarmTrigger.register")
        }
    }

    override fun onAddRule(rule: Rule) {
        super.onAddRule(rule)
        if (rule.fact is_ Alarm::class.java) {
            val alarm = rule.fact unpack_ Alarm::class.java
            alarmScheduler.cancel(alarm, "onAddRule")
            if (rule.isEnabled) alarmScheduler.schedule(alarm, "onAddRule")
        }
    }

    override fun onRuleEnabled(ruleId: String) {
        super.onRuleEnabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            if (it.isEnabled && it.fact is_ Alarm::class.java) {
                val alarm = it.fact unpack_ Alarm::class.java
                alarmScheduler.cancel(alarm, "onRuleEnabled")
                alarmScheduler.schedule(alarm, "onRuleEnabled")
            }
        }
    }

    override fun onRuleDisabled(ruleId: String) {
        super.onRuleDisabled(ruleId)
        val rule = shortXContext.rule.getRuleById(ruleId)
        rule?.let {
            if (!it.isEnabled && it.fact is_ Alarm::class.java) {
                val alarm = it.fact unpack_ Alarm::class.java
                alarmScheduler.cancel(alarm, "onRuleDisabled")
            }
        }
    }

    override fun onDeleteRule(rule: Rule) {
        super.onDeleteRule(rule)
        logger.d("onDeleteRule: ${rule.title}")
        if (rule.fact is_ Alarm::class.java) {
            val alarm = rule.fact unpack_ Alarm::class.java
            alarmScheduler.cancel(alarm, "onDeleteRule")
        }
    }

    override fun unRegister() {
        alarmScheduler.shutdown()
    }

}