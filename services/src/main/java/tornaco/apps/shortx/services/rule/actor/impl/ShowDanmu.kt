package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.ShowDanmu
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowDanmu : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowDanmu::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        // java.lang.NoClassDefFoundError: kotlinx.coroutines.internal.MainDispatcherLoader
        // Clean build will fix this error.
        logger.d("Show.")
        withContext(Dispatchers.Main) {
            val d = action unpack_ ShowDanmu::class.java
            val cv = RuleContextAndVars(params)
            shortXContext.ui.showDanmu(
                id = evaluateContext.srcId,
                icon = d.icon.compileContextAndVars(cv),
                text = d.text.compileContextAndVars(cv)
            )
        }
    }
}