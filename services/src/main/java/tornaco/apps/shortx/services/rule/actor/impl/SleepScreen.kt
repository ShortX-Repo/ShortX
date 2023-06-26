package tornaco.apps.shortx.services.rule.actor.impl

import android.os.SystemClock
import com.google.protobuf.Message
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SleepScreen : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SleepScreen::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        context.powerManager.goToSleep(
            SystemClock.uptimeMillis()
        )
    }

}