package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.BatteryTemperatureChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

class BatteryTemperatureTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("BatteryTemperatureTrigger")
    private val _temp = MutableStateFlow(-1)

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
                logger.d("temp: $temp")
                if (temp < 0) return

                if (temp != _temp.value) {
                    logger.d("temp changed from: ${_temp.value} to: $temp")
                    _temp.value = temp

                    shortXContext.rule.publishFact(
                        reason = "Battery temp changed",
                        fact = BatteryTemperatureChanged.newBuilder().setTemp(temp / 10f).build(),
                        initialContextData = mapOf(ContextDataMapping.BatteryTemperatureChanged.batteryTemperature.name to temp / 10f)
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