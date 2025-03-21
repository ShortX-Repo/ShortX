package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.ShellCommand
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.Interceptors
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShellCommand : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShellCommand::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        require(Interceptors.checkExecuteShell(params.shortXContext, params.androidContext))

        val sc = action unpack_ ShellCommand::class.java
        val cv = RuleContextAndVars(params)
        val command = sc.command.compileContextAndVars(cv)
        val res =
            shortXContext.shell.executeCommand(command, sc.singleShot)
        logger.d("Shell command: $command")
        logger.d("Shell res: $res")

        contextData.add(
            sc.customContextDataKey.forKey(ContextDataMapping.ShellCommand.shellCode.name)
                .fallbackOnEmpty { ContextDataMapping.ShellCommand.shellCode.name },
            res.code
        )
        val errorString = res.errList?.joinToString(System.lineSeparator()).also {
            logger.logOnDebug("shellErr: [$it]")
        }
        contextData.add(
            sc.customContextDataKey.forKey(ContextDataMapping.ShellCommand.shellErr.name)
                .fallbackOnEmpty { ContextDataMapping.ShellCommand.shellErr.name },
            errorString
        )
        val outString = res.outList?.joinToString(System.lineSeparator()).also {
            logger.logOnDebug("shellOut: [$it]")
        }
        contextData.add(
            sc.customContextDataKey.forKey(ContextDataMapping.ShellCommand.shellOut.name)
                .fallbackOnEmpty { ContextDataMapping.ShellCommand.shellOut.name },
            outString
        )
        Actor.Res(
            listOf(
                "code: " + res.code,
                "error: $errorString",
                "out: $outString"
            ).joinToString(System.lineSeparator())
        )
    }
}