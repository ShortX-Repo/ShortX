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

    private val shellRequestIdToShell = mutableMapOf<String, Shell>()

    override fun exe(
        requestId: String,
        command: Array<out String>,
        receiver: SynchronousResultReceiver,
        singleShot: Boolean
    ) {
        logger.d("exe: $command $singleShot")
        if (singleShot) {
            exeSingleShot(requestId, command, receiver)
        } else {
            exeSharedProcess(requestId, command, receiver)
        }
    }

    fun exeSharedProcess(
        requestId: String,
        command: Array<out String>,
        receiver: SynchronousResultReceiver,
    ) {
        logger.i("exeSharedProcess, about to execute: ${command.joinToString()}")
        logger.i("exeSharedProcess, current shell map: $shellRequestIdToShell")
        executors.execute {
            runCatching {
                val shell = Shell.SU
                logger.d("exeSharedProcess, requestId: $requestId")
                shellRequestIdToShell[requestId] = shell
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
        requestId: String,
        command: Array<out String>,
        receiver: SynchronousResultReceiver,
    ) {
        logger.i("exeSingleShot, about to execute: ${command.joinToString()}")
        logger.i("exeSingleShot, current shell map: $shellRequestIdToShell")
        executors.execute {
            runCatching {
                logger.i("exeSingleShot, execute: ${command.joinToString()}")
                val shell = Shell("su --mount-master")
                logger.d("exeSingleShot, requestId: $requestId")
                shellRequestIdToShell[requestId] = shell
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
                shellRequestIdToShell.remove(requestId)
                logger.i("exeSingleShot, shell.shutdown complete.")
            }.onFailure {
                logger.e("exeSingleShot, Shell onFailure: $it")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    override fun close(requestId: String) {
        executors.execute {
            shellRequestIdToShell.remove(requestId)?.let { shell ->
                logger.w("Tornaco.Shell Found shell to close: $shell")
                runCatching {
                    logger.w("Tornaco.Shell Calling shutdown: $shell")
                    shell.forceShutdown()
                }.onFailure {
                    logger.e(it, "Tornaco.Shell shutdown shell")
                }
            } ?: logger.w("Tornaco.Shell unable to close shell, request: $requestId not found")
        }
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