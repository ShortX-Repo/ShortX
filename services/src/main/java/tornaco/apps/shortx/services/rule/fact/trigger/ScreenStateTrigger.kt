package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.fact.UserPresent
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

// Screen off/on has delay, so we listen on off state by Usage stats hook.
class ScreenStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            intent.action?.let {
                when (it) {
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
        screenFilter.addAction(Intent.ACTION_USER_PRESENT)
        ContextCompat.registerReceiver(
            context,
            receiver,
            screenFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}