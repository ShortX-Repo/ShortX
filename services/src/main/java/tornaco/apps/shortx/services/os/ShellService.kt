package tornaco.apps.shortx.services.os

import android.os.RemoteCallbackList
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import java.time.Duration


class ShellService(shortXContext: ShortXContext) : SystemService(shortXContext, "ShellService") {
    private val shellStubs = RemoteCallbackList<ISu>()

    override fun systemReady() {
        super.systemReady()
    }

    fun attachSUImpl(from: String?, su: ISu?) {
        logger.w("attachSUImpl: $from $su")
        su?.let {
            shellStubs.register(su, from)
        }
    }

    fun isInjectedShellEnabled(): Boolean {
        return true
    }

    fun setInjectedShellEnabled(enable: Boolean) {
    }

    fun executeCommand(command: String): ShellRes {
        return executeCommandByInjection(command)
    }

    private fun executeCommandByInjection(command: String): ShellRes {
        val itemCount = shellStubs.registeredCallbackCount
        if (itemCount == 0) {
            logger.d("executeCommand, no registered shell.")
            return errorShellRes("su impl is not attached")
        }
        val first = shellStubs.getRegisteredCallbackItem(0)
        logger.d("Using shell: $first")
        return runCatching {
            val receiver = SynchronousResultReceiver.get()
            first.exe(arrayOf(command), receiver)
            val receiverRes = receiver.awaitResultNoInterrupt(Duration.ofSeconds(20))
            logger.d("receiver get res: $receiverRes")
            val byteRes: ByteArrayWrapper? = receiverRes.getValue(null)
            byteRes?.let { ShellRes.parseFrom(byteRes.byteData) }
                ?: errorShellRes("No shell res returned.")
        }.getOrElse {
            logger.e(it, "Got error call shell")
            errorShellRes(it.toString())
        }
    }

    private fun errorShellRes(error: String): ShellRes {
        throw IllegalStateException("Execute fail: $error")
    }
}

