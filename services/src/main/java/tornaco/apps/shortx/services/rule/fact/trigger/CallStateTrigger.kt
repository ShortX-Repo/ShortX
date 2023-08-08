package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.CallState
import tornaco.apps.shortx.core.proto.fact.CallStateChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver
import java.util.concurrent.atomic.AtomicReference


class CallStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("CallStateTrigger")
    private val lastCallState = AtomicReference(CallState.UNRECOGNIZED)

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
                    val callState = state.toCallState()
                    val incomingNumber: String? =
                        intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                    if (callState != lastCallState.get()) {
                        logger.d("callState changed: $callState, incomingNumber: $incomingNumber")
                        shortXContext.rule.publishFact(
                            reason = "CallState changed",
                            fact = CallStateChanged.newBuilder()
                                .setCallState(callState)
                                .setIncomingNumber(incomingNumber ?: "")
                                .build(),
                            initialContextData = mapOf(
                                ContextDataMapping.CallStateChanged.callState.name to callState.name,
                                ContextDataMapping.CallStateChanged.incomingNumber.name to (incomingNumber
                                    ?: ""),
                            )
                        )
                    }
                    lastCallState.set(callState)
                }
            }
        }
    }

    private fun String.toCallState(): CallState {
        return if (this == TelephonyManager.EXTRA_STATE_RINGING) {
            CallState.CallStateRinging
        } else if (this == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            CallState.CallStateOffHook
        } else if (this == TelephonyManager.EXTRA_STATE_IDLE) {
            CallState.CallStateIdle
        } else {
            CallState.UNRECOGNIZED
        }
    }
}