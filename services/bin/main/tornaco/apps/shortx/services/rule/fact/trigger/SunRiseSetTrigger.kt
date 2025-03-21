package tornaco.apps.shortx.services.rule.fact.trigger

import android.app.AlarmManager
import android.app.AlarmManager.OnAlarmListener
import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.fact.SunRise
import tornaco.apps.shortx.core.proto.fact.SunRiseSet
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.alarmManager
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.location.TwilightListener
import tornaco.apps.shortx.services.location.TwilightState
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.rule.fact.alarm.acquireWakeLockForPublishFactAndNextAlarmSchedule

class SunRiseSetTrigger(
    context: Context,
    shortXContext: ShortXContext
) :
    DAAndRuleAwareTrigger(context, shortXContext) {
    companion object {
        const val LOG_TAG = "SunRiseSetTrigger"
    }

    private val logger = Logger(LOG_TAG)
    private val scope = CoroutineScope(SupervisorJob())

    private var currentTwilightState: TwilightState? = null

    private val handler by lazy { newHandlerOfNewThread(LOG_TAG) }
    private val twilightListener = TwilightListener {
        logger.d("TwilightListener: $it")
        currentTwilightState = it
        scheduleNextAlarm()
    }


    private val alarmRiseListener = OnAlarmListener {
        logger.w("alarmRiseListener onAlarm")
        shortXContext.rule.publishFactChecked(SunRiseSet.getDefaultInstance()) {
            FactPublishData(
                reason = "alarmRiseListener.",
                fact = SunRiseSet.newBuilder()
                    .setRise(SunRise.SunRise_Rise)
                    .build()
            )
        }
        onAlarmHandled()
    }

    private fun onAlarmHandled() {
        val wakeLock = context.acquireWakeLockForPublishFactAndNextAlarmSchedule(LOG_TAG)
        scope.launch {
            delay(3000)
            scheduleNextAlarm()
            wakeLock?.release()
        }
    }

    private val alarmSetListener = OnAlarmListener {
        logger.w("alarmSetListener onAlarm")
        shortXContext.rule.publishFactChecked(SunRiseSet.getDefaultInstance()) {
            FactPublishData(
                reason = "alarmSetListener.",
                fact = SunRiseSet.newBuilder()
                    .setRise(SunRise.SunRise_Set)
                    .build()
            )
        }

        onAlarmHandled()
    }

    private fun scheduleNextAlarm() {
        logger.d("scheduleNextAlarm. currentTwilightState: $currentTwilightState isNight? ${currentTwilightState?.isNight}")
        cancelAllAlarm()

        currentTwilightState?.let { state ->
            if (state.isNight) {
                scheduleRiseAlarm()
            } else {
                scheduleSetAlarm()
            }
        }
    }

    private fun cancelAllAlarm() {
        context.alarmManager.cancel(alarmRiseListener)
        context.alarmManager.cancel(alarmSetListener)
    }

    private fun scheduleRiseAlarm() {
        logger.w("scheduleRiseAlarm, currentTwilightState: $currentTwilightState")
        val twilightState = currentTwilightState ?: return
        val riseTime = twilightState.sunriseTimeMillis()
        logger.d("Rise time: ${DateUtils.formatLong(riseTime)}")

        if (riseTime < System.currentTimeMillis()) {
            logger.w("riseTime < System.currentTimeMillis")
        } else {
            context.alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                riseTime,
                LOG_TAG,
                alarmRiseListener,
                handler
            )
        }
    }

    private fun scheduleSetAlarm() {
        logger.w("scheduleSetAlarm, currentTwilightState: $currentTwilightState")
        val twilightState = currentTwilightState ?: return
        val setTime = twilightState.sunsetTimeMillis()
        logger.d("Set time: ${DateUtils.formatLong(setTime)}")
        if (setTime < System.currentTimeMillis()) {
            logger.w("setTime < System.currentTimeMillis")
        } else {
            context.alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                setTime,
                LOG_TAG,
                alarmSetListener,
                handler
            )
        }
    }

    override fun register() {
        updateTriggerState()
    }

    override fun unRegister() {
    }

    override fun updateTriggerState() {
        var anyFat = false
        run loop@{
            shortXContext.rule.forEachEnabledFacts { _, factProtoAny ->
                if (factProtoAny is_ SunRiseSet::class.java) {
                    val fact = factProtoAny unpack_ SunRiseSet::class.java
                    if (!fact.isDisabled) {
                        anyFat = true
                        return@loop
                    }
                }
            }
        }
        logger.d("anyFat: $anyFat")
        if (anyFat) {
            shortXContext.twilight.manager.registerListener(twilightListener, handler)
        } else {
            shortXContext.twilight.manager.unregisterListener(twilightListener)
            cancelAllAlarm()
        }
    }

    override val factClass: Class<out Message> = SunRiseSet::class.java
}