package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.fact.HeadsetPlug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

class HeadsetPlugTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("HeadsetPluginTrigger")

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            logger.d("handleReceive: $intent")
            if (intent.action == AudioManager.ACTION_HEADSET_PLUG) {
                when (intent.getIntExtra("state", -1)) {
                    0 -> {
                        logger.d("Headset unplugged")
                        shortXContext.rule.publishFact(
                            "Headset unplug",
                            HeadsetPlug.newBuilder().setIsPlug(false).build(),
                            initialContextDataLazy = {
                                emptyMap()
                            })
                    }

                    1 -> {
                        logger.d("Headset plugged")
                        shortXContext.rule.publishFact(
                            "Headset plugged",
                            HeadsetPlug.newBuilder().setIsPlug(true).build(),
                            initialContextDataLazy = {
                                emptyMap()
                            })
                    }

                    else -> logger.d("Headset state unknown")
                }
            }
        }
    }

    override fun register() {
        val filter = IntentFilter(AudioManager.ACTION_HEADSET_PLUG)
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }

}