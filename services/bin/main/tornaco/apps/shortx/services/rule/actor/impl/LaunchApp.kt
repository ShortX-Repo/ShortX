package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.LaunchApp
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object LaunchApp : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.LaunchApp::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        val app = params.action unpack_ LaunchApp::class.java
        params.shortXContext.appState.launchApp(app.appPkg)
        return Actor.Res.EMPTY
    }
}