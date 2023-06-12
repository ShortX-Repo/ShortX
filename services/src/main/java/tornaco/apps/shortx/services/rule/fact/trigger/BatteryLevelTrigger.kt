package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.fact.BatteryLevelChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.mvel.handle.BatteryState
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.BatteryImpl
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

class BatteryLevelTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("BatteryLevelTrigger")
    private val _state = MutableStateFlow(BatteryState())

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val newState = BatteryImpl.retrieveBatteryState(intent)
                logger.d("newState: $newState")
                val stateChanged = (newState != _state.value)
                if (stateChanged) {
                    logger.d("state changed")
                    _state.value = newState

                    shortXContext.rule.publishFact(
                        reason = "BatteryState changed",
                        fact = BatteryLevelChanged.newBuilder().setLevel(newState.batteryLevel)
                            .build(),
                        initialContextData = emptyMap()
                    )
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