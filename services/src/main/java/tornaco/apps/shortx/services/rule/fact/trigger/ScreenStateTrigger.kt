package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.UserPresent
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

// Android 31+ screen on/off is handled by ActiveServicesHook.
class ScreenStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            intent.action?.let {
                when (it) {
                    Intent.ACTION_SCREEN_OFF -> {
                        if (!OsUtils.isSOrAbove()) {
                            shortXContext.rule.publishFact(
                                "ACTION_SCREEN_OFF",
                                ScreenOff.newBuilder().build()
                            ) { emptyMap() }
                        }
                    }

                    Intent.ACTION_SCREEN_ON -> {
                        if (!OsUtils.isSOrAbove()) {
                            shortXContext.rule.publishFact(
                                "ACTION_SCREEN_ON",
                                ScreenOn.newBuilder().build()
                            ) { emptyMap() }
                        }
                    }

                    Intent.ACTION_USER_PRESENT -> {
                        shortXContext.rule.publishFact(
                            "ACTION_USER_PRESENT",
                            UserPresent.newBuilder().build()
                        ) { emptyMap() }
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