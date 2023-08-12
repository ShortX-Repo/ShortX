package tornaco.apps.shortx.core.su

import eu.chainfire.libsuperuser.Debug
import eu.chainfire.libsuperuser.Shell.SU.shellMountMaster
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.Executors

object LibSuperuserShellImpl : ISu.Stub() {
    private val logger = Logger("LibSuperuserShellImpl")
    private val executors = Executors.newFixedThreadPool(4)

    init {
        Debug.setDebug(BuildProp.APP_IS_DEBUG)
    }

    override fun exe(
        command: Array<out String>,
        receiver: SynchronousResultReceiver
    ) {
        logger.i("about to execute: ${command.joinToString()}")

        executors.execute {
            logger.i("execute: ${command.joinToString()}")
            val stdOut = mutableListOf<String>()
            val stdError = mutableListOf<String>()
            runCatching {
                val code =
                    eu.chainfire.libsuperuser.Shell.Pool.getWrapper(shellMountMaster())
                        .run(command, stdOut, stdError, false)
                logger.i("Shell res: out: $stdOut err: $stdError")
                val proto = ShellRes.newBuilder().setCode(code).addAllOut(stdOut)
                    .addAllErr(stdError)
                    .build()
                val byteArrayWrapper = ByteArrayWrapper(proto.toByteArray())
                receiver.send(byteArrayWrapper)
            }.onFailure {
                logger.e("Shell onFailure: $it")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    override fun close(receiver: SynchronousResultReceiver) {
        logger.w("close. no impl.")
    }
}