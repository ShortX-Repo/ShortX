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
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.SystemClock
import tornaco.apps.shortx.core.alarm.getTriggerTimeOffset
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.alarmManager
import tornaco.apps.shortx.services.util.AlarmManagerCompat
import java.time.Duration
import java.util.Locale
import kotlin.math.abs

private const val BROADCAST_ACTION = "github.tornaco.android.thanos.service.alarm.engine.action"
private const val BROADCAST_EXTRA_TAG =
    "github.tornaco.android.thanos.service.alarm.engine.extra.tag"

class AlarmScheduler(
    private val context: Context,
    private val handler: Handler,
    private val onAlarm: (alarmLabel: String) -> Unit,
) {
    private val logger = Logger("Alarm")
    private val alarmManager get() = context.alarmManager

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action ?: return
            if (action != BROADCAST_ACTION) return
            val tag = intent.getStringExtra(BROADCAST_EXTRA_TAG) ?: ""
            handler.post {
                logger.w("AlarmScheduler, on alarm: $tag")
                onAlarm(tag)
            }
        }
    }

    fun schedule(alarm: Alarm, reason: String) = kotlin.runCatching {
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(alarm),
            Intent(BROADCAST_ACTION)
                .setPackage("android")
                .putExtra(BROADCAST_EXTRA_TAG, alarm.tag)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerOffset = alarm.getTriggerTimeOffset()

        if (triggerOffset <= 0) {
            logger.w("AlarmScheduler, skip this alarm since offset < 0, maybe we have missed this alarm")
        } else {
            logger.w(
                "AlarmScheduler schedule: $alarm, reason: $reason, pi: $pi, trigger offset: ${
                    formatDuration(Duration.ofMillis(triggerOffset))
                }"
            )
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + triggerOffset,
                pi
            )
        }
    }.onFailure {
        logger.e(it, "AlarmScheduler schedule error")
    }

    fun cancel(alarm: Alarm, reason: String) = kotlin.runCatching {
        // Get pending intent used to create alarm, use FLAG_NO_CREATE if PendingIntent
        // does not already exist, then null will be returned.
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(alarm),
            Intent(BROADCAST_ACTION)
                .setPackage("android")
                .putExtra(BROADCAST_EXTRA_TAG, alarm.tag)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        logger.w("AlarmScheduler cancel: ${alarm.tag}, $reason, pi: $pi")
        pi?.let {
            alarmManager.cancel(pi)
        }
    }.onFailure {
        logger.e(it, "AlarmScheduler cancel error")
    }

    private fun requestCode(alarm: Alarm) = alarm.tag.hashCode()

    fun systemReady() {
        logger.w("AlarmScheduler systemReady")
        context.registerReceiver(receiver, IntentFilter(BROADCAST_ACTION))
    }

    fun shutdown() {
        logger.w("AlarmScheduler shutdown")
        context.unregisterReceiver(receiver)
    }

    private fun formatDuration(duration: Duration): String {
        val seconds = duration.seconds
        val absSeconds = abs(seconds)
        val positive = String.format(
            Locale.ENGLISH,
            "%d:%02d:%02d", absSeconds / 3600, absSeconds % 3600 / 60, absSeconds % 60
        )
        return if (seconds < 0) "-$positive" else positive
    }
}