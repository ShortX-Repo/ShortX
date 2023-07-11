package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.ShellCommand
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShellCommand : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShellCommand::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val sc = action unpack_ ShellCommand::class.java
        val command = sc.command.inflateContextMap(contextData).replaceGlobalVarValues {
            shortXContext.rule.getAllGlobalVars()
        }
        val res =
            shortXContext.shell.executeCommand(command)
        logger.d("Shell command: $command")
        logger.d("Shell res: $res")
        requireNotNull(res) {
            "Fail execute shell: Can not connect to ShortX App. Pls allow ShortX start from background!!!"
        }
        contextData.add(ContextDataMapping.ShellCommand.shellCode.name, res.code)
        contextData.add(ContextDataMapping.ShellCommand.shellErr.name, res.errList?.joinToString())
        contextData.add(ContextDataMapping.ShellCommand.shellOut.name, res.outList?.joinToString())
    }
}