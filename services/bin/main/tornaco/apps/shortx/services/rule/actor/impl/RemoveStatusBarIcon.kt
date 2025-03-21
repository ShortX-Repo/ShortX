package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object RemoveStatusBarIcon : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.RemoveStatusBarIcon::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d =
            params.action unpack_ tornaco.apps.shortx.core.proto.action.RemoveStatusBarIcon::class.java
        val cv = RuleContextAndVars(params)
        val slot = d.slot.compileContextAndVars(cv)
        withContext(Dispatchers.Main) {
            shortXContext.statusbar.removeStatusBarIcon(slot)
        }
        Actor.Res.EMPTY
    }
}