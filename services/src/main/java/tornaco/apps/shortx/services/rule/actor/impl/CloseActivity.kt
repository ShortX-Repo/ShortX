package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.CloseActivityMode
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object CloseActivity : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.CloseActivity::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.CloseActivity::class.java
        val finished = when (d.mode) {
            CloseActivityMode.CloseActivityMode_Component -> {
                shortXContext.appState.finishActivity(
                    d.component.toComponentName(),
                    d.component.pkg.userId
                )
            }

            CloseActivityMode.CloseActivityMode_Focused,
            CloseActivityMode.UNRECOGNIZED -> {
                shortXContext.appState.finishCurrentFocusedActivity()
            }
        }
        require(finished) { "No matching Activity to close." }
        Actor.Res.EMPTY
    }
}
