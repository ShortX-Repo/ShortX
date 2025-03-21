package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.powerManager
import tornaco.apps.shortx.services.input.InputShellCommand
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object InputTap : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.InputTap::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.InputTap::class.java
        require(androidContext.powerManager.isInteractive) {
            "Can not InputTap when device is not interactive"
        }

        val cv = RuleContextAndVars(params)
        val x = data.xs.ifBlank { data.x.toString() }.compileContextAndVars(cv).trim().toInt()
        val y = data.ys.ifBlank { data.y.toString() }.compileContextAndVars(cv).trim().toInt()

        runCatching {
            InputShellCommand.onCommandOrThrow("tap $x $y")
        }.onFailure { err ->
            logger.d("onCommandOrThrow error: ${err.stackTraceToString()}")
            shortXContext.wm.withUiAutomation("InputTap") {
                it.click(x, y)
            }
        }
        Actor.Res.EMPTY
    }
}