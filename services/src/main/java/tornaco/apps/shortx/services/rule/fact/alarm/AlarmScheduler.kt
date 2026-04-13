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

import android.app.AlarmManager
import android.app.AlarmManager.OnAlarmListener
import android.content.Context
import android.os.Handler
import tornaco.apps.shortx.core.alarm.formatDuration
import tornaco.apps.shortx.core.alarm.getTriggerTimeOffset
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.alarmManager
import tornaco.apps.shortx.services.calendar.workday.WorkdayRepository
import tornaco.apps.shortx.services.calendar.workday.WorkdayResolver
import java.time.Duration

class AlarmScheduler(
    private val context: Context,
    private val handler: Handler,
    private val onAlarm: AlarmScheduler.(ruleTitle: String, ruleId: String, factId: String) -> Unit,
) {
    private val logger = Logger("ShortXAlarm")
    private val alarmManager get() = context.alarmManager
    private val workdayResolver by lazy { WorkdayResolver(WorkdayRepository(context)) }
    private val idToListeners = mutableMapOf<String, OnAlarmListener>()

    @Suppress("ObjectLiteralToLambda")
    fun schedule(rule: Rule, alarm: Alarm, reason: String) = kotlin.runCatching {
        // Cancel anyway
        cancel(rule, alarm, "Before schedule.")

        val triggerOffset = alarm.getTriggerTimeOffset(dateFilter = alarm.workdayDateFilter())

        if (triggerOffset <= 0) {
            logger.w("AlarmScheduler AlarmScheduler, skip this alarm since offset < 0, maybe we have missed this alarm")
        } else {
            logger.w(
                "AlarmScheduler AlarmScheduler schedule for rule: ${rule.title}, reason: $reason, trigger offset: ${
                    formatDuration(Duration.ofMillis(triggerOffset))
                }"
            )
            val listener = object : OnAlarmListener {
                override fun onAlarm() {
                    logger.w("AlarmScheduler onAlarm - ${rule.title} ${rule.id} ${alarm.id}")
                    onAlarm(rule.title, rule.id, alarm.id)
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
        logger.e(it, "AlarmScheduler schedule error")
    }

    fun cancel(rule: Rule, alarm: Alarm, reason: String) = kotlin.runCatching {
        synchronized(idToListeners) {
            val listener = idToListeners.remove(alarm.id)
            logger.w("AlarmScheduler cancel for fact: ${rule.title}, $reason ${alarm.id} listener: $listener")
            listener?.let { alarmManager.cancel(it) }
        }
    }.onFailure {
        logger.e(it, "AlarmScheduler cancel error")
    }

    private fun Alarm.workdayDateFilter(): ((java.time.LocalDate) -> Boolean)? {
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