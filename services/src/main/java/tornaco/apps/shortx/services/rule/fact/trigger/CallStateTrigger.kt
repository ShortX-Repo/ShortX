package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver


@Suppress("OVERRIDE_DEPRECATION")
class CallStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("CallStateTrigger")

    override fun register() {
        val filter = IntentFilter()
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        context.registerReceiver(receiver, filter)

    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }


    private val receiver = object : SafeBroadcastReceiver(shortXContext) {

        override fun handleReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                if (state != null) {
                    logger.d("state: $state")
                    if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                        // 电话响铃
                        val incomingNumber =
                            intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        // 处理逻辑
                    } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                        // 电话接通
                        // 处理逻辑
                    } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                        // 电话挂断
                        // 处理逻辑
                    }
                }
            }
        }
    }

}