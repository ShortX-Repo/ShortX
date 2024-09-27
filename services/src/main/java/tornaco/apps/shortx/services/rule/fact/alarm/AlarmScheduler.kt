/*
 * (C) Copyright 2022 Thanox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package tornaco.apps.shortx.services.rule.fact.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.alarm.formatDuration
import tornaco.apps.shortx.core.alarm.getTriggerTimeOffset
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.alarmManager
import tornaco.apps.shortx.services.util.AlarmManagerCompat
import tornaco.apps.shortx.services.watchdog.postSafety
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

private const val BROADCAST_ACTION = "tornaco.apps.shortx.services.rule.fact.alarm.action"
private const val BROADCAST_EXTRA_RULE_ID =
    "tornaco.apps.shortx.services.rule.fact.alarm.extra.rule.id"
private const val BROADCAST_EXTRA_RULE_TITLE =
    "tornaco.apps.shortx.services.rule.fact.alarm.extra.rule.title"
private const val BROADCAST_EXTRA_FACT_ID =
    "tornaco.apps.shortx.services.rule.fact.alarm.extra.fact.id"

class AlarmScheduler(
    private val context: Context,
    private val handler: Handler,
    private val onAlarm: AlarmScheduler.(ruleTitle: String, ruleId: String, factId: String) -> Unit,
) {
    private val logger = Logger("ShortXAlarm")
    private val alarmManager get() = context.alarmManager

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action ?: return
            if (action != BROADCAST_ACTION) return
            val ruleId = intent.getStringExtra(BROADCAST_EXTRA_RULE_ID)
            if (ruleId == null) {
                logger.w("onReceive, ruleId extra is null.")
                return
            }
            val ruleTitle = intent.getStringExtra(BROADCAST_EXTRA_RULE_TITLE)
            if (ruleTitle == null) {
                logger.w("onReceive, ruleTitle extra is null.")
                return
            }
            val factId = intent.getStringExtra(BROADCAST_EXTRA_FACT_ID)
            if (factId == null) {
                logger.w("onReceive, factId extra is null.")
                return
            }
            handler.postSafety {
                logger.i("receiver, call on alarm, ruleTitle: $ruleTitle, ruleId: $ruleId, factId: $factId")
                onAlarm(ruleTitle, ruleId, factId)
            }
        }
    }

    fun schedule(rule: Rule, alarm: Alarm, reason: String) = kotlin.runCatching {
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(alarm),
            Intent(BROADCAST_ACTION)
                .setPackage("android")
                .putExtra(BROADCAST_EXTRA_RULE_ID, rule.id)
                .putExtra(BROADCAST_EXTRA_RULE_TITLE, rule.title)
                .putExtra(BROADCAST_EXTRA_FACT_ID, alarm.id)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerOffset = alarm.getTriggerTimeOffset()

        if (triggerOffset <= 0) {
            logger.i("AlarmScheduler, skip this alarm since offset < 0, maybe we have missed this alarm")
        } else {
            logger.i(
                "AlarmScheduler schedule for rule: ${rule.title}, reason: $reason, pi: $pi, trigger offset: ${
                    formatDuration(Duration.ofMillis(triggerOffset))
                }"
            )

            // java.lang.NullPointerException:
            // Attempt to invoke virtual method 'boolean android.content.Intent.migrateExtraStreamToClipData()' on a null object reference
            //  at android.app.PendingIntent.getActivity(PendingIntent.java:348)
            //  at android.app.PendingIntent.getActivity(PendingIntent.java:311)
            //  at tornaco.apps.shortx.services.rule.fact.alarm.AlarmScheduler.schedule-0E7RQCE(AlarmScheduler.kt:108)
            //  at tornaco.apps.shortx.services.rule.fact.trigger.AlarmTrigger.checkAndScheduleAlarmForRule(AlarmTrigger.kt:78)
            //  at tornaco.apps.shortx.services.rule.fact.trigger.AlarmTrigger.register(AlarmTrigger.kt:68)
            //  at tornaco.apps.shortx.services.rule.RuleService.installListeners(RuleService.kt:313)
            //  at tornaco.apps.shortx.services.rule.RuleService.systemReady(RuleService.kt:206)
            //  at tornaco.apps.shortx.services.ServiceLifecycle.systemReady$lambda$3(ServiceLifecycle.kt:29)
            //  at tornaco.apps.shortx.services.ServiceLifecycle.$r8$lambda$nVP-0IWWFReUPuQb7Qs7_QEy8kA(Unknown Source:0)
            //  at tornaco.apps.shortx.services.ServiceLifecycle$$ExternalSyntheticLambda1.run(Unknown Source:2)
            //  at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
            //  at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
            //  at java.lang.Thread.run(Thread.java:919)
            val viewerIntent = context.alarmClockViewerIntent()
            logger.i("AlarmScheduler, viewerIntent: $viewerIntent")
            AlarmManagerCompat.setAlarmClock(
                alarmManager,
                System.currentTimeMillis() + triggerOffset,
                viewerIntent,
                pi
            )
        }
    }.onFailure {
        logger.e(it, "AlarmScheduler schedule error")
    }

    fun cancel(rule: Rule, alarm: Alarm, reason: String) = kotlin.runCatching {
        // Get pending intent used to create alarm, use FLAG_NO_CREATE if PendingIntent
        // does not already exist, then null will be returned.
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(alarm),
            Intent(BROADCAST_ACTION)
                .setPackage("android")
                .putExtra(BROADCAST_EXTRA_RULE_ID, rule.id)
                .putExtra(BROADCAST_EXTRA_RULE_TITLE, rule.title)
                .putExtra(BROADCAST_EXTRA_FACT_ID, alarm.id)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        logger.i("AlarmScheduler cancel for fact: ${rule.title}, $reason, pi: $pi")
        pi?.let {
            alarmManager.cancel(pi)
        }
    }.onFailure {
        logger.e(it, "AlarmScheduler cancel error")
    }

    // Check req code.
    private fun requestCode(alarm: Alarm) = AlarmReqCodeCache.get(alarm.id)

    fun systemReady() {
        logger.i("AlarmScheduler systemReady")
        ContextCompat.registerReceiver(
            context, receiver, IntentFilter(BROADCAST_ACTION),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun shutdown() {
        logger.i("AlarmScheduler shutdown")
        context.unregisterReceiver(receiver)
    }
}

object AlarmReqCodeCache {
    private val code = AtomicInteger(100)
    private val cache = mutableMapOf<String, Int>()

    fun get(alarmId: String): Int {
        val current = cache[alarmId]
        if (current != null) return current

        val newCode = code.incrementAndGet()
        cache[alarmId] = newCode

        return newCode
    }
}

fun Context.alarmClockViewerIntent(): PendingIntent {
    return PendingIntent.getActivity(
        this,
        System.currentTimeMillis().toString().hashCode(),
        Intent().setPackage(BuildPropInternal.APP_PACKAGE_NAME).setComponent(
            ComponentName.unflattenFromString("${BuildPropInternal.APP_PACKAGE_NAME}/${BuildProp.APP_MAIN_ACTIVITY}")
        ),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}