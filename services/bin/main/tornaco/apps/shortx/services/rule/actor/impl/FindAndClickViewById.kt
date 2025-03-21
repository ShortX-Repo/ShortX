package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object FindAndClickViewById : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.FindAndClickViewById::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.FindAndClickViewById::class.java
        require(androidContext.powerManager.isInteractive) {
            "Can not FindAndClickViewByText when device is not interactive"
        }
        shortXContext.wm.withUiAutomation("FindAndClickViewById") {
            val cv = RuleContextAndVars(params)
            it.findAndClickViewByViewId(
                viewId = d.viewId.compileContextAndVars(cv),
                isRegex = d.isRegex,
                timeout = maxOf(d.timeout, 1000L)
            )
        }
        Actor.Res.EMPTY
    }
}