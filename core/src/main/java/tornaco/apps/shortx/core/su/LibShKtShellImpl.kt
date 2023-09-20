package tornaco.apps.shortx.core.su

import com.jaredrummler.ktsh.Shell
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object LibShKtShellImpl : ISu.Stub() {
    private val logger = Logger("LibShKtShellImpl")
    private val executors = Executors.newFixedThreadPool(4)

    override fun exe(
        command: Array<out String>,
        receiver: SynchronousResultReceiver
    ) {
        logger.i("about to execute: ${command.joinToString()}")

        executors.execute {
            logger.i("execute: ${command.joinToString()}")
            val shell = Shell("su --mount-master")
            val res = shell.run(
                command[0],
                Shell.Command.Config.Builder().apply {
                    redirectErrorStream = true
                    timeout = Shell.Timeout(3, TimeUnit.MINUTES)
                }.create()
            )
            logger.i("Shell res: $res")
            val proto = ShellRes.newBuilder()
                .setCode(res.exitCode)
                .addAllOut(res.stdout)
                .addAllErr(res.stderr)
                .build()

            val byteArrayWrapper = ByteArrayWrapper(proto.toByteArray())
            receiver.send(byteArrayWrapper)

            shell.shutdown()
            logger.i("shell.shutdown complete.")
        }
    }

    override fun close(receiver: SynchronousResultReceiver) {
        logger.w("close. no impl.")
    }
}