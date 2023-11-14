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

package tornaco.apps.shortx.services.rule.actor.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.SystemClock
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.alarmManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.AlarmManagerCompat
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver
import java.util.concurrent.atomic.AtomicInteger

private const val BROADCAST_ACTION = "tornaco.apps.shortx.services.action.delay.alarm.action"
private const val BROADCAST_EXTRA_REQUEST_ID =
    "tornaco.apps.shortx.services.action.alarm.extra.request.id"

class DelayScheduler(
    private val context: Context,
    private val shortXContext: ShortXContext
) {
    private val logger = Logger("DelayScheduler")
    private val alarmManager get() = context.alarmManager

    private val callbacks = mutableMapOf<String, (requestId: String) -> Unit>()

    private val receiver: BroadcastReceiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            if (action != BROADCAST_ACTION) return
            val requestId = intent.getStringExtra(BROADCAST_EXTRA_REQUEST_ID)
            if (requestId == null) {
                logger.w("onReceive, ruleId extra is null.")
                return
            }
            val callback = callbacks.remove(requestId)
            logger.d("handleReceive, callback for this request: $callback")
            if (callback == null) {
                logger.e("Callback not exists: $action")
            } else {
                callback.invoke(requestId)
            }
        }
    }

    fun schedule(
        requestId: String, delay: Long,
        onAlarm: (requestId: String) -> Unit,
    ) = kotlin.runCatching {
        logger.d("schedule: $requestId $delay")
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(requestId),
            Intent(BROADCAST_ACTION)
                .setPackage("android")
                .putExtra(BROADCAST_EXTRA_REQUEST_ID, requestId)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        callbacks[requestId] = onAlarm

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + delay,
            pi
        )
    }.onFailure {
        logger.e(it, "DelayScheduler schedule error")
    }

    fun cancel(requestId: String, reason: String) = kotlin.runCatching {
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode(requestId),
            Intent(BROADCAST_ACTION)
                .setPackage("android")
                .putExtra(BROADCAST_EXTRA_REQUEST_ID, requestId)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        logger.i("DelayScheduler cancel for delay: ${requestId}, $reason, pi: $pi")
        pi?.let {
            alarmManager.cancel(pi)
        }
    }.onFailure {
        logger.e(it, "DelayScheduler cancel error")
    }

    // Check req code.
    private fun requestCode(requestId: String) = AlarmReqCodeCache.get(requestId)

    fun init() {
        logger.i("DelayScheduler init")
        ContextCompat.registerReceiver(
            context, receiver, IntentFilter(BROADCAST_ACTION),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun shutdown() {
        logger.i("DelayScheduler shutdown")
        context.unregisterReceiver(receiver)
    }
}

private object AlarmReqCodeCache {
    private val code = AtomicInteger(100)
    private val cache = mutableMapOf<String, Int>()

    fun get(requestId: String): Int {
        val current = cache[requestId]
        if (current != null) return current

        val newCode = code.incrementAndGet()
        cache[requestId] = newCode

        return newCode
    }
}