package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.fact.UserPresent
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

private var systemReadyHasBeenTriggered: Boolean = false

class ScreenStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            intent.action?.let {
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
                        if (!systemReadyHasBeenTriggered) {
                            systemReadyHasBeenTriggered = true
                            shortXContext.rule.publishFact(
                                "ACTION_USER_PRESENT - SystemReady",
                                SystemReady.newBuilder().build()
                            )
                        }

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