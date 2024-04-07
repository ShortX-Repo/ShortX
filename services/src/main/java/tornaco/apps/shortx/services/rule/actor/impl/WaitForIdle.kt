package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WaitForIdle : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WaitForIdle::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        if (!androidContext.powerManager.isInteractive) return@with
        delay(3000)
    }
}