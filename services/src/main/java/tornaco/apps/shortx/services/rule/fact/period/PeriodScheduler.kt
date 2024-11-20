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

import android.app.AlarmManager
import android.app.AlarmManager.OnAlarmListener
import android.content.Context
import android.os.Handler
import tornaco.apps.shortx.core.alarm.formatDuration
import tornaco.apps.shortx.core.alarm.getTriggerTimeOffset
import tornaco.apps.shortx.core.alarm.getTriggerTimeOffsetForRepeat
import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.alarmManager
import java.time.Duration

class RandomInPeriodScheduler(
    private val context: Context,
    private val handler: Handler,
    private val onAlarm: RandomInPeriodScheduler.(ruleTitle: String, ruleId: String, factId: String) -> Unit,
) {
    private val logger = Logger("RandomInPeriodScheduler")
    private val alarmManager get() = context.alarmManager

    private val idToListeners = mutableMapOf<String, OnAlarmListener>()

    @Suppress("ObjectLiteralToLambda")
    fun schedule(rule: Rule, period: RandomInPeriod, isForRepeat: Boolean, reason: String) =
        kotlin.runCatching {
            // Cancel anyway
            cancel(rule, period, "Before schedule.")

            val triggerOffset = if (isForRepeat) {
                period.getTriggerTimeOffsetForRepeat()
            } else {
                period.getTriggerTimeOffset()
            }

            if (triggerOffset <= 0) {
                logger.w("RandomInPeriodScheduler skip this period since offset < 0, maybe we have missed this alarm")
            } else {
                logger.w(
                    "RandomInPeriodScheduler schedule for rule: ${rule.title}, reason: $reason, isRepeat: $isForRepeat, trigger offset: ${
                        formatDuration(Duration.ofMillis(triggerOffset))
                    }"
                )
                val listener = object : OnAlarmListener {
                    override fun onAlarm() {
                        logger.w("RandomInPeriodScheduler onAlarm - ${rule.title} ${rule.id} ${period.id}")
                        onAlarm(rule.title, rule.id, period.id)
                    }
                }
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + triggerOffset,
                    rule.title,
                    listener,
                    handler
                )
                synchronized(idToListeners) {
                    idToListeners[period.id] = listener
                }
            }
        }.onFailure {
            logger.e(it, "RandomInPeriodScheduler schedule error")
        }

    fun cancel(rule: Rule, period: RandomInPeriod, reason: String) = kotlin.runCatching {
        synchronized(idToListeners) {
            val listener = idToListeners.remove(period.id)
            logger.w("RandomInPeriodScheduler cancel for fact: ${rule.title}, $reason ${period.id} listener: $listener")
            listener?.let { alarmManager.cancel(it) }
        }
    }.onFailure {
        logger.e(it, "RandomInPeriodScheduler cancel error")
    }
}