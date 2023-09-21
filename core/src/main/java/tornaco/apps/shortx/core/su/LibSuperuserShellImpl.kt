package tornaco.apps.shortx.core.su

import eu.chainfire.libsuperuser.Debug
import eu.chainfire.libsuperuser.Shell.SU.shellMountMaster
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
        Debug.setDebug(true)
        Debug.setOnLogListener { type, typeIndicator, message ->
            logger.d("OnLog: $type $typeIndicator --> $message")
        }
    }

    override fun exe(
        command: Array<out String>,
        receiver: SynchronousResultReceiver,
        singleShot: Boolean
    ) {
        logger.i("about to execute: ${command.joinToString()}")

        executors.execute {
            logger.i("execute: ${command.joinToString()}")

            val maxRetries = 3
            var retries = 0
            var operationSucceeded = false
            var shellRes: ShellResult? = null

            val errorMessages = mutableListOf<Throwable>()

            while (retries < maxRetries && !operationSucceeded) {
                runCatching {
                    val stdOut = mutableListOf<String>()
                    val stdError = mutableListOf<String>()
                    val code =
                        eu.chainfire.libsuperuser.Shell.Pool.getWrapper(shellMountMaster())
                            .run(command, stdOut, stdError, false)
                    shellRes = ShellResult(code, stdOut, stdError)
                    logger.i("shellRes: $shellRes")
                    operationSucceeded = true
                }.onFailure { err ->
                    logger.e("Shell onFailure: $err, will retry")
                    operationSucceeded = false
                    if (errorMessages.none { it.javaClass == err.javaClass }) {
                        errorMessages.add(err)
                    }
                }

                if (!operationSucceeded) {
                    retries++
                    logger.w("Shell operation failed. Retrying (Attempt $retries)...")
                }
            }

            if (operationSucceeded) {
                logger.i("Shell operation succeeded!")
                val nonNullRes =
                    requireNotNull(shellRes) { "operationSucceeded but, nonNullRes is null." }
                val proto = ShellRes.newBuilder().setCode(nonNullRes.code).addAllOut(nonNullRes.out)
                    .addAllErr(nonNullRes.error)
                    .build()
                val byteArrayWrapper = ByteArrayWrapper(proto.toByteArray())
                receiver.send(byteArrayWrapper)
            } else {
                logger.e("Operation failed after $maxRetries attempts.")
                receiver.propagateException(RuntimeException(
                    errorMessages.joinToString(System.lineSeparator()) { it.stackTraceToString() }
                ))
            }
        }
    }

    override fun close(receiver: SynchronousResultReceiver) {
        logger.w("close. no impl.")
    }

    data class ShellResult(val code: Int, val out: List<String>, val error: List<String>)
}