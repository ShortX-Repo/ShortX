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

package tornaco.apps.shortx.services.rule.fact.period

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.alarm.formatDuration
import tornaco.apps.shortx.core.alarm.getTriggerTimeOffset
import tornaco.apps.shortx.core.alarm.getTriggerTimeOffsetForRepeat
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.alarmManager
import tornaco.apps.shortx.services.util.AlarmManagerCompat
import tornaco.apps.shortx.services.watchdog.postSafety
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

private const val BROADCAST_ACTION = "tornaco.apps.shortx.services.rule.fact.period.action"
private const val BROADCAST_EXTRA_RULE_ID =
    "tornaco.apps.shortx.services.rule.fact.period.extra.rule.id"
private const val BROADCAST_EXTRA_RULE_TITLE =
    "tornaco.apps.shortx.services.rule.fact.period.extra.rule.title"
private const val BROADCAST_EXTRA_FACT_ID =
    "tornaco.apps.shortx.services.rule.fact.period.extra.fact.id"

class RandomInPeriodScheduler(
    private val context: Context,
    private val handler: Handler,
    private val onAlarm: RandomInPeriodScheduler.(ruleTitle: String, ruleId: String, factId: String) -> Unit,
) {
    private val logger = Logger("ShortXRandomInPeriod")
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

    fun schedule(rule: Rule, period: RandomInPeriod, isForRepeat: Boolean, reason: String) =
        kotlin.runCatching {
            val pi = PendingIntent.getBroadcast(
                context,
                requestCode(period),
                Intent(BROADCAST_ACTION)
                    .setPackage("android")
                    .putExtra(BROADCAST_EXTRA_RULE_ID, rule.id)
                    .putExtra(BROADCAST_EXTRA_RULE_TITLE, rule.title)
                    .putExtra(BROADCAST_EXTRA_FACT_ID, period.id)
                    .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerOffset = if (isForRepeat) {
                period.getTriggerTimeOffsetForRepeat()
            } else {
                period.getTriggerTimeOffset()
            }

            if (triggerOffset <= 0) {
                logger.i("skip this period since offset < 0, maybe we have missed this alarm")
            } else {
                logger.i(
                    "schedule for rule: ${rule.title}, reason: $reason, isRepeat: $isForRepeat, pi: $pi, trigger offset: ${
                        formatDuration(Duration.ofMillis(triggerOffset))
                    }"
                )
                AlarmManagerCompat.setAlarmClock(
                    alarmManager,
                    System.currentTimeMillis() + triggerOffset,
                    PendingIntent.getActivity(
                        context,
                        System.currentTimeMillis().toString().hashCode(),
                        context.packageManager.getLaunchIntentForPackage(BuildProp.APP_PACKAGE_NAME),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    ),
                    pi
                )
            }
        }.onFailure {
            logger.e(it, "schedule error")
        }

    fun cancel(rule: Rule, period: RandomInPeriod, reason: String) = kotlin.runCatching {
        // Get pending intent used to create alarm, use FLAG_NO_CREATE if PendingIntent
        // does not already exist, then null will be returned.
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(period),
            Intent(BROADCAST_ACTION)
                .setPackage("android")
                .putExtra(BROADCAST_EXTRA_RULE_ID, rule.id)
                .putExtra(BROADCAST_EXTRA_RULE_TITLE, rule.title)
                .putExtra(BROADCAST_EXTRA_FACT_ID, period.id)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        logger.i("cancel for fact: ${rule.title}, $reason, pi: $pi")
        pi?.let {
            alarmManager.cancel(pi)
        }
    }.onFailure {
        logger.e(it, "cancel error")
    }

    // Check req code.
    private fun requestCode(alarm: RandomInPeriod) = RandomInPeriodReqCodeCache.get(alarm.id)

    fun systemReady() {
        logger.i("systemReady")
        ContextCompat.registerReceiver(
            context, receiver, IntentFilter(BROADCAST_ACTION),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun shutdown() {
        logger.i("shutdown")
        context.unregisterReceiver(receiver)
    }
}

internal object RandomInPeriodReqCodeCache {
    private val code = AtomicInteger(100)
    private val cache = mutableMapOf<String, Int>()

    fun get(periodId: String): Int {
        val current = cache[periodId]
        if (current != null) return current

        val newCode = code.incrementAndGet()
        cache[periodId] = newCode

        return newCode
    }
}