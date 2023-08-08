package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.telephonyManager
import tornaco.apps.shortx.services.ShortXContext


@Suppress("OVERRIDE_DEPRECATION")
class CallStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("CallStateTrigger")

    override fun register() {
        context.telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String) {
                super.onCallStateChanged(state, phoneNumber)
                when (state) {
                    TelephonyManager.CALL_STATE_IDLE -> {
                        logger.p("CALL_STATE_IDLE $phoneNumber")
                    }

                    TelephonyManager.CALL_STATE_RINGING -> {
                        logger.p("CALL_STATE_RINGING $phoneNumber")
                    }

                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        logger.p("CALL_STATE_OFFHOOK $phoneNumber")
                    }
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)

    }

    override fun unRegister() {
    }
}