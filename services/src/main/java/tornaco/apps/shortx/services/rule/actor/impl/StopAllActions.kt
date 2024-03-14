package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StopAllActions : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StopAllActions::class.java
    }

    @ExperimentalCoroutinesApi
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        shortXContext.rule.cancelAllJobs("Action")
    }
}