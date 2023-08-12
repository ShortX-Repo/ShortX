package tornaco.apps.shortx.core.su

import com.topjohnwu.superuser.Shell
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.emptyByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.Executors

object LibSuShellImpl : ISu.Stub() {
    private val logger = Logger("LibSuShellImpl")
    private val executors = Executors.newFixedThreadPool(4)
    private val callbackExecutor = Executors.newSingleThreadExecutor()

    init {
        logger.w("Init shell")
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR or Shell.FLAG_MOUNT_MASTER)
                .setTimeout(10)
        )
    }

    override fun exe(
        command: Array<out String>,
        receiver: SynchronousResultReceiver
    ) {
        logger.i("about to execute: ${command.joinToString()}")
        executors.execute {
            logger.i("execute: ${command.joinToString()}")
            runCatching {
                Shell.cmd(*command).submit(callbackExecutor) {
                    logger.i("Shell res: out: ${it.out} err: ${it.err}")
                    val proto = ShellRes.newBuilder().setCode(it.code).addAllOut(it.out)
                        .addAllErr(it.err)
                        .build()
                    val byteArrayWrapper = ByteArrayWrapper(proto.toByteArray())
                    receiver.send(byteArrayWrapper)
                }
            }.onFailure {
                logger.e("Shell onFailure: $it")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    override fun close(receiver: SynchronousResultReceiver) {
        logger.w("close")
        runCatching {
            Shell.resetShell()
            receiver.send(emptyByteArrayWrapper)
            logger.i("Shell closed.")
        }.onFailure {
            logger.e(it, "unable to close Shell.")
            receiver.propagateException(RuntimeException(it))
        }
    }
}