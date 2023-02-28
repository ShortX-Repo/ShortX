package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.LocationStatusChanged
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.mvel.handle.impl.HwImpl

class LocationStateTrigger(
    override val context: Context, override val shortXContext: ShortXContext
) : Trigger {
    private val hw = HwImpl(context, shortXContext)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.let {
                if (it == LocationManager.MODE_CHANGED_ACTION) {
                    shortXContext.rule.publishFact(
                        "Location mode changed",
                        LocationStatusChanged.newBuilder()
                            .setOoa(hw.isLocationEnabled.select(OnOffAny.On, OnOffAny.Off)).build()
                    )
                }
            }
        }
    }


    override fun register() {
        val filter = IntentFilter()
        filter.addAction(LocationManager.MODE_CHANGED_ACTION)
        context.registerReceiver(receiver, filter)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}