package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.StatusBarClipState
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowStatusBarClip : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowStatusBarClip::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        logger.d("Show.")
        withContext(Dispatchers.Main) {
            val d =
                action unpack_ tornaco.apps.shortx.core.proto.action.ShowStatusBarClip::class.java
            val cv = RuleContextAndVars(params)
            shortXContext.statusbar.updateStatusBarClip(
                StatusBarClipState.newBuilder()
                    .setId(evaluateContext.srcId)
                    .setIcon(d.icon.compileContextAndVars(cv))
                    .setText(d.text.compileContextAndVars(cv))
                    .build()
            )
        }
    }
}