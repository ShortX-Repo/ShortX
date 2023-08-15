package tornaco.apps.shortx.services.rule.cond.impl

import android.content.Context
import android.telephony.TelephonyManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object IsInCall : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.IsInCall::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.IsInCall::class.java)
        val isInCall = CallStateHelper(param.context).isPhoneInCall()

        logger.d("Is in call: $isInCall")

        return if (data.isInvert) {
            !isInCall
        } else {
            isInCall
        }.toResult()
    }

    class CallStateHelper(private val context: Context) {

        private val telephonyManager: TelephonyManager by lazy {
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        }

        fun isPhoneInCall(): Boolean {
            val callState = telephonyManager.callState
            return callState == TelephonyManager.CALL_STATE_OFFHOOK
        }
    }
}