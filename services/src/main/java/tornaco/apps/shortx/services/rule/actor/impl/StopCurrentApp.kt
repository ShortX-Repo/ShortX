package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StopCurrentApp : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StopCurrentApp::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        logger.d("stopForegroundApp")
        shortXContext.appState.stopForegroundApp()
    }

}