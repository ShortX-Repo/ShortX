package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.fact.UserPresent
import tornaco.apps.shortx.core.proto.fact.UserPresentAtTheFirstTime
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver
import java.util.concurrent.atomic.AtomicBoolean

// Android 31+ screen on/off is handled by ActiveServicesHook.
class ScreenStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    companion object {
        val firstTimeUserPresentTriggered = AtomicBoolean(false)
    }

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            intent.action?.let {
                when (it) {
                    Intent.ACTION_SCREEN_OFF -> {
                        if (!OsUtils.isSOrAbove()) {
                            shortXContext.serviceLifecycle.screenOff()
                        }
                    }

                    Intent.ACTION_SCREEN_ON -> {
                        if (!OsUtils.isSOrAbove()) {
                            shortXContext.serviceLifecycle.screenOn()
                        }
                    }

                    Intent.ACTION_USER_PRESENT -> {
                        shortXContext.rule.publishFactChecked(UserPresent.getDefaultInstance()) {
                            FactPublishData(
                                reason = "ACTION_USER_PRESENT",
                                fact = UserPresent.newBuilder().build()
                            )
                        }

                        if (firstTimeUserPresentTriggered.compareAndSet(false, true)) {
                            shortXContext.rule.publishFactChecked(
                                UserPresentAtTheFirstTime.getDefaultInstance()
                            ) {
                                FactPublishData(
                                    reason = "ACTION_USER_PRESENT 1st time",
                                    fact = UserPresentAtTheFirstTime.newBuilder().build()
                                )
                            }

                            shortXContext.serviceLifecycle.userPresent(true)
                        } else {
                            shortXContext.serviceLifecycle.userPresent(false)
                        }
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