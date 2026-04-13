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

package tornaco.apps.shortx.services.rule.fact.fixed

import android.app.AlarmManager
import android.app.AlarmManager.OnAlarmListener
import android.content.Context
import android.os.Handler
import tornaco.apps.shortx.core.alarm.formatDuration
import tornaco.apps.shortx.core.alarm.getTriggerTimeOffset
import tornaco.apps.shortx.core.proto.fact.FixedInPeriod
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.alarmManager
import tornaco.apps.shortx.services.calendar.workday.WorkdayRepository
import tornaco.apps.shortx.services.calendar.workday.WorkdayResolver
import java.time.Duration

class FixedInPeriodScheduler(
    private val context: Context,
    private val handler: Handler,
    private val onFixedInPeriod: FixedInPeriodScheduler.(ruleTitle: String, ruleId: String, factId: String) -> Unit,
) {
    private val logger = Logger("FixedInPeriodScheduler")
    private val alarmManager get() = context.alarmManager
    private val workdayResolver by lazy { WorkdayResolver(WorkdayRepository(context)) }

    private val idToListeners = mutableMapOf<String, OnAlarmListener>()

    @Suppress("ObjectLiteralToLambda")
    fun schedule(rule: Rule, alarm: FixedInPeriod, reason: String) = kotlin.runCatching {
        // Cancel anyway
        cancel(rule, alarm, "Before schedule.")

        val triggerOffset = alarm.getTriggerTimeOffset(dateFilter = alarm.workdayDateFilter())

        if (triggerOffset <= 0) {
            logger.w("FixedInPeriodScheduler, skip this alarm since offset < 0, maybe we have missed this alarm and it has no repeat")
        } else {
            logger.w(
                "FixedInPeriodScheduler schedule for rule: ${rule.title}, reason: $reason, trigger offset: ${
                    formatDuration(Duration.ofMillis(triggerOffset))
                }"
            )
            val listener = object : OnAlarmListener {
                override fun onAlarm() {
                    logger.w("FixedInPeriodScheduler onAlarm - ${rule.title} ${rule.id} ${alarm.id}")
                    onFixedInPeriod(rule.title, rule.id, alarm.id)
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
                idToListeners[alarm.id] = listener
            }
        }
    }.onFailure {
        logger.e(it, "FixedInPeriodScheduler schedule error")
    }

    fun cancel(rule: Rule, period: FixedInPeriod, reason: String) = kotlin.runCatching {
        synchronized(idToListeners) {
            val listener = idToListeners.remove(period.id)
            logger.w("FixedInPeriodScheduler cancel for fact: ${rule.title}, $reason ${period.id} listener: $listener")
            listener?.let { alarmManager.cancel(it) }
        }
    }.onFailure {
        logger.e(it, "FixedInPeriodScheduler cancel error")
    }

    private fun FixedInPeriod.workdayDateFilter(): ((java.time.LocalDate) -> Boolean)? {
        if (!hasWorkdayFilter()) {
            return null
        }

        val dayType = workdayFilter.dayType
        if (dayType == tornaco.apps.shortx.core.proto.condition.CalendarDayType.AnyDay
            || dayType == tornaco.apps.shortx.core.proto.condition.CalendarDayType.UNRECOGNIZED
        ) {
            return null
        }

        return { date ->
            workdayResolver.resolve(date).matches(dayType)
        }
    }
}