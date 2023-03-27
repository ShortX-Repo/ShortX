package tornaco.apps.shortx.services.rule.fact.trigger

import android.app.NotificationManager.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.DNDStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.notificationManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext

class DNDStateTrigger(
    override val context: Context, override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("DNDStateTrigger")

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            intent?.action?.let {
                if (ACTION_INTERRUPTION_FILTER_CHANGED == it) {
                    val manager = context.notificationManager
                    val mode = manager.currentInterruptionFilter
                    // currentInterruptionFilter是一个int类型的值，用来表示当前免打扰模式的状态，具体取值如下
                    // INTERRUPTION_FILTER_UNKNOWN：未知状态
                    //INTERRUPTION_FILTER_ALL：所有通知都会被打断
                    //INTERRUPTION_FILTER_PRIORITY：只有优先级高的通知可以打断
                    //INTERRUPTION_FILTER_NONE：所有通知都不会被打断
                    //INTERRUPTION_FILTER_ALARMS：只有闹钟可以打断

                    logger.d("currentInterruptionFilter: ${mode.modeToString()}")

                    shortXContext.rule.publishFact(
                        "ACTION_INTERRUPTION_FILTER_CHANGED",
                        DNDStatusChanged.newBuilder()
                            .setOoa(
                                (mode == INTERRUPTION_FILTER_ALL).select(
                                    OnOffAny.Off,
                                    OnOffAny.On
                                )
                            )
                            .build()
                    )
                }
            }
        }
    }


    override fun register() {
        val filter = IntentFilter()
        filter.addAction(ACTION_INTERRUPTION_FILTER_CHANGED)
        context.registerReceiver(receiver, filter)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }

    private fun Int.modeToString(): String {
        return when (this) {
            INTERRUPTION_FILTER_UNKNOWN -> {
                "INTERRUPTION_FILTER_UNKNOWN"
            }
            INTERRUPTION_FILTER_ALL -> {
                "INTERRUPTION_FILTER_ALL"
            }
            INTERRUPTION_FILTER_PRIORITY -> {
                "INTERRUPTION_FILTER_PRIORITY"
            }
            INTERRUPTION_FILTER_NONE -> {
                "INTERRUPTION_FILTER_NONE"
            }
            INTERRUPTION_FILTER_ALARMS -> {
                "INTERRUPTION_FILTER_ALARMS"
            }
            else -> {
                "N/A"
            }
        }
    }
}