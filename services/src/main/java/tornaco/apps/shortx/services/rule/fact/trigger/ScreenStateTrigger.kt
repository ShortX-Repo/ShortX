package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.UserPresent
import tornaco.apps.shortx.services.ShortXContext

class ScreenStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.let {
                when (it) {
                    Intent.ACTION_SCREEN_OFF -> {
                        shortXContext.rule.publishFact(
                            "ACTION_SCREEN_OFF",
                            ScreenOff.newBuilder().build()
                        )

                    }
                    Intent.ACTION_SCREEN_ON -> {
                        shortXContext.rule.publishFact(
                            "ACTION_SCREEN_ON",
                            ScreenOn.newBuilder().build()
                        )
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        shortXContext.rule.publishFact(
                            "ACTION_USER_PRESENT",
                            UserPresent.newBuilder().build()
                        )
                    }
                }
            }
        }
    }


    override fun register() {
        val screenFilter = IntentFilter()
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF)
        screenFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenFilter.addAction(Intent.ACTION_USER_PRESENT)
        context.registerReceiver(receiver, screenFilter)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}