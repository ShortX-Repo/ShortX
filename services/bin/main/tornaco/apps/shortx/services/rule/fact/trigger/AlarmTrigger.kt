package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.alarm.isNo
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.factsAndQuitFacts
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.alarm.AlarmScheduler
import tornaco.apps.shortx.services.rule.fact.alarm.acquireWakeLockForPublishFactAndNextAlarmSchedule

// [ 2024-05-27T21:39:47.706    10284:  4122:  4122 I/LSPosed-Bridge  ]   Loading class one.yufz.hmspush.hook.XposedMod
//[ 2024-05-27T21:39:47.817    10284:  4122:  4122 I/LSPosed-Bridge  ] [HMSPush]  XposedMod  Loaded app: com.huawei.hwid process:com.huawei.hwid.container2
//[ 2024-05-27T21:39:47.884     1000:  1762:  1762 I/LSPosed-Bridge  ] Crash unexpectedly: java.lang.RuntimeException: Error receiving broadcast Intent { act=android.app.action.NEXT_ALARM_CLOCK_CHANGED flg=0x21000010 } in com.android.server.power.PowerOffAlarmService$1@9a8888c
//	at android.app.LoadedApk$ReceiverDispatcher$Args.lambda$getRunnable$0(LoadedApk.java:1818)
//	at android.app.LoadedApk$ReceiverDispatcher$Args.$r8$lambda$mcNAAl1SQ4MyJPyDg8TJ2x2h0Rk(Unknown Source:0)
//	at android.app.LoadedApk$ReceiverDispatcher$Args$$ExternalSyntheticLambda0.run(D8$$SyntheticClass:0)
//	at android.os.Handler.handleCallback(Handler.java:959)
//	at android.os.Handler.dispatchMessage(Handler.java:100)
//	at android.os.Looper.loopOnce(Looper.java:232)
//	at android.os.Looper.loop(Looper.java:317)
//	at com.android.server.SystemServer.run(SystemServer.java:1009)
//	at com.android.server.SystemServer.main(SystemServer.java:693)
//	at java.lang.reflect.Method.invoke(Native Method)
//	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:554)
//	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:856)
//Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'void android.app.NotificationManager.cancel(int)' on a null object reference
//	at com.android.server.power.PowerOffAlarmService.updateNotification(PowerOffAlarmService.java:164)
//	at com.android.server.power.PowerOffAlarmService.updateAlarms(PowerOffAlarmService.java:136)
//	at com.android.server.power.PowerOffAlarmService.-$$Nest$mupdateAlarms(PowerOffAlarmService.java:0)
//	at com.android.server.power.PowerOffAlarmService$1.onReceive(PowerOffAlarmService.java:112)
//	at android.app.LoadedApk$ReceiverDispatcher$Args.lambda$getRunnable$0(LoadedApk.java:1810)
//	... 11 more
//[ 2024-05-27T21:39:47.887     1000:  1762:  1762 E/AndroidRuntime  ] *** FATAL EXCEPTION IN SYSTEM PROCESS: main
//java.lang.RuntimeException: Error receiving broadcast Intent { act=android.app.action.NEXT_ALARM_CLOCK_CHANGED flg=0x21000010 } in com.android.server.power.PowerOffAlarmService$1@9a8888c
//	at android.app.LoadedApk$ReceiverDispatcher$Args.lambda$getRunnable$0(LoadedApk.java:1818)
//	at android.app.LoadedApk$ReceiverDispatcher$Args.$r8$lambda$mcNAAl1SQ4MyJPyDg8TJ2x2h0Rk(Unknown Source:0)
//	at android.app.LoadedApk$ReceiverDispatcher$Args$$ExternalSyntheticLambda0.run(D8$$SyntheticClass:0)
//	at android.os.Handler.handleCallback(Handler.java:959)
//	at android.os.Handler.dispatchMessage(Handler.java:100)
//	at android.os.Looper.loopOnce(Looper.java:232)
//	at android.os.Looper.loop(Looper.java:317)
//	at com.android.server.SystemServer.run(SystemServer.java:1009)
//	at com.android.server.SystemServer.main(SystemServer.java:693)
//	at java.lang.reflect.Method.invoke(Native Method)
//	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:554)
//	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:856)
//Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'void android.app.NotificationManager.cancel(int)' on a null object reference
//	at com.android.server.power.PowerOffAlarmService.updateNotification(PowerOffAlarmService.java:164)
//	at com.android.server.power.PowerOffAlarmService.updateAlarms(PowerOffAlarmService.java:136)
//	at com.android.server.power.PowerOffAlarmService.-$$Nest$mupdateAlarms(PowerOffAlarmService.java:0)
//	at com.android.server.power.PowerOffAlarmService$1.onReceive(PowerOffAlarmService.java:112)
//	at android.app.LoadedApk$ReceiverDispatcher$Args.lambda$getRunnable$0(LoadedApk.java:1810)
const val ALARM_SCHEDULE_DELAY = 10 * 1000L

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
            if (ruleOrNull.factsAndQuitFacts.all { !it.isAlarm() }) {
                logger.i("Rule ${ruleOrNull.title} fact and it's quit does not contains Alarm anymore, will not publish fact.")
                return@AlarmScheduler
            }

            val alarmList =
                ruleOrNull.factsAndQuitFacts.filter { it.isAlarm() }
                    .map { it unpack_ Alarm::class.java }
            val targetFact = alarmList.firstOrNull { it.id == factId }
            if (targetFact == null) {
                logger.i("Rule ${ruleOrNull.title} does not have this fact with id: $factId, will not publish fact.")
                return@AlarmScheduler
            }

            val wakeLock = context.acquireWakeLockForPublishFactAndNextAlarmSchedule("Alarm")
            logger.i("Good, will publish fact: $wakeLock")
            shortXContext.rule.publishFact(
                reason = "onAlarm",
                fact = Alarm.newBuilder().setTriggerAt(targetFact.triggerAt)
                    .setRepeat(targetFact.repeat)
                    .setId(factId).build(),
                initialContextDataLazy = { emptyMap() }
            )

            // Repeat
            if (!targetFact.repeat.isNo) {
                logger.i("Repeat alarm")
                schedule(ruleOrNull, targetFact, "AlarmTrigger.repeat")
                wakeLock?.release()
            }
        }
    }

    private val scope = CoroutineScope(SupervisorJob())

    override fun register() {
        scope.launch {
            delay(ALARM_SCHEDULE_DELAY)
            shortXContext.rule.getAllEnabledRules().forEach { rule ->
                checkAndScheduleAlarmForRule(rule) { "AlarmTrigger.register for rule: ${rule.title}" }
            }
        }
    }

    private fun checkAndScheduleAlarmForRule(rule: Rule, reason: () -> String) {
        if (rule.isEnabled) {
            val alarms = (rule.factsAndQuitFacts).filter { it.isAlarm() }
                .map { it unpack_ Alarm::class.java }
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
        val alarms =
            (rule.factsAndQuitFacts).filter { it.isAlarm() }.map { it unpack_ Alarm::class.java }
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

        if (oldRule.factsList == newRule.factsList && oldRule.quit.factsList == newRule.quit.factsList) {
            logger.d("Facts and quit not change for this rule(new label): ${newRule.title}")
            return
        }

        if (oldRule.factsList.filter { it.isAlarm() } == newRule.factsList.filter { it.isAlarm() }
            && oldRule.quit.factsList.filter { it.isAlarm() } == newRule.quit.factsList.filter { it.isAlarm() }) {
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
    }

    private fun ProtoAny.isAlarm() = this is_ Alarm::class.java
}