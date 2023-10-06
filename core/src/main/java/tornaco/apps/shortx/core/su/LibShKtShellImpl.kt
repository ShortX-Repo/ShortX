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
    private val executors = Executors.newCachedThreadPool()

    override fun exe(
        command: Array<out String>,
        receiver: SynchronousResultReceiver,
        singleShot: Boolean
    ) {
        logger.d("exe: $command $singleShot")
        if (singleShot) {
            exeSingleShot(command, receiver)
        } else {
            exeSharedProcess(command, receiver)
        }
    }

    fun exeSharedProcess(
        command: Array<out String>,
        receiver: SynchronousResultReceiver,
    ) {
        logger.i("exeSharedProcess, about to execute: ${command.joinToString()}")
        executors.execute {
            runCatching {
                val shell = Shell.SU
                val res = shell.run(
                    command[0],
                    Shell.Command.Config.Builder().apply {
                        redirectErrorStream = true
                        timeout = Shell.Timeout(SHELL_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    }.create()
                )
                logger.i("exeSharedProcess, Shell res: $res")
                logger.i("exeSharedProcess, Shell details: ${res.details}")
                val proto = ShellRes.newBuilder()
                    .setCode(res.exitCode)
                    .addAllOut(res.stdout.removeEOL())
                    .addAllErr(res.stderr.removeEOL())
                    .build()
                val byteArrayWrapper = ByteArrayWrapper(proto.toByteArray())
                receiver.send(byteArrayWrapper)
            }.onFailure {
                logger.e("exeSharedProcess, Shell onFailure: $it")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    private fun exeSingleShot(
        command: Array<out String>,
        receiver: SynchronousResultReceiver,
    ) {
        logger.i("exeSingleShot, about to execute: ${command.joinToString()}")
        executors.execute {
            runCatching {
                logger.i("exeSingleShot, execute: ${command.joinToString()}")
                val shell = Shell("su --mount-master")
                val res = shell.run(
                    command[0],
                    Shell.Command.Config.Builder().apply {
                        redirectErrorStream = true
                        timeout = Shell.Timeout(SHELL_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    }.create()
                )
                logger.i("exeSingleShot, Shell res: $res")
                logger.i("exeSingleShot, Shell details: ${res.details}")
                val proto = ShellRes.newBuilder()
                    .setCode(res.exitCode)
                    .addAllOut(res.stdout.removeEOL())
                    .addAllErr(res.stderr.removeEOL())
                    .build()

                val byteArrayWrapper = ByteArrayWrapper(proto.toByteArray())
                receiver.send(byteArrayWrapper)

                shell.shutdown()
                logger.i("exeSingleShot, shell.shutdown complete.")
            }.onFailure {
                logger.e("exeSingleShot, Shell onFailure: $it")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    override fun close(receiver: SynchronousResultReceiver) {
        logger.w("close. no impl.")
    }

    private fun List<String>.removeEOL(): List<String> {
        return toMutableList().apply {
            if (!isEmpty()) {
                val last = last()
                if (last.isEmpty()) {
                    removeLast()
                }
            }
        }
    }
}