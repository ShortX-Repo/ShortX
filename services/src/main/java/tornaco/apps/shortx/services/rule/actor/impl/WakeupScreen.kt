package tornaco.apps.shortx.services.rule.actor.impl

import android.os.PowerManager
import android.os.SystemClock
import com.google.protobuf.Message
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WakeupScreen : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WakeupScreen::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        context.powerManager.wakeUp(
            SystemClock.uptimeMillis(),
            PowerManager.WAKE_REASON_POWER_BUTTON, "Wakeup"
        )
    }

}