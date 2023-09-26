package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.fact.ChargerPlug
import tornaco.apps.shortx.core.proto.fact.ChargerUnplug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.BatteryImpl
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

class ChargeStateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("ChargeStateTrigger")
    private val _isCharging = MutableStateFlow<Boolean?>(null)

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val newState = BatteryImpl.retrieveBatteryState(intent)
                logger.d("newState: $newState")
                val stateChanged = (newState.isCharging != _isCharging.value)
                if (stateChanged) {
                    logger.d("state changed")
                    _isCharging.value = newState.isCharging

                    if (shortXContext.user.isSystemReadyTriggered) {
                        if (newState.isCharging) {
                            shortXContext.rule.publishFact(
                                reason = "Charge state changed",
                                fact = ChargerPlug.newBuilder().build(),
                            )
                        } else {
                            shortXContext.rule.publishFact(
                                reason = "Charge state changed",
                                fact = ChargerUnplug.newBuilder().build(),
                            )
                        }
                    } else {
                        logger.w("isSystemReadyTriggered is false. ignore.")
                    }
                }
            }
        }
    }

    override fun register() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }

}