package tornaco.apps.shortx.services.rule.cond.impl

import android.content.Context
import android.telephony.TelephonyManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object IsRinging : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.IsRinging::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        val data =
            param.condition.unpack_(tornaco.apps.shortx.core.proto.condition.IsRinging::class.java)
        val isRinging = RingingStateHelper(param.context).isPhoneRinging()

        logger.d("Is ringing: $isRinging")

        return if (data.isInvert) {
            !isRinging
        } else {
            isRinging
        }.toResult()
    }

    class RingingStateHelper(private val context: Context) {

        private val telephonyManager: TelephonyManager by lazy {
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        }

        fun isPhoneRinging(): Boolean {
            return telephonyManager.callState == TelephonyManager.CALL_STATE_RINGING
        }
    }
}