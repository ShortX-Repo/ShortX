package tornaco.apps.shortx.services.os

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.su.SHELL_TIMEOUT_MINUTES
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.enforceCallingPermissions
import tornaco.apps.shortx.services.util.AppServiceProxy
import java.time.Duration
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference


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
        return store.read(Settings.BoolSettings.EnableInjectedShell)
    }

    fun setInjectedShellEnabled(enable: Boolean) {
        enforceCallingPermissions()
        store.write(Settings.BoolSettings.EnableInjectedShell, enable)
    }

    suspend fun executeCommand(command: String, singleShot: Boolean): ShellRes {
        return isInjectedShellEnabled().select(
            t = { executeCommandByInjection(command, singleShot) },
            f = {
                AppShellServiceProxy.exe(context, command, singleShot)
                    ?: errorShellRes("AppShellServiceProxy error")
            }
        )
    }

    private suspend fun executeCommandByInjection(command: String, singleShot: Boolean): ShellRes {
        val itemCount = shellStubs.registeredCallbackCount
        if (itemCount == 0) {
            logger.d("executeCommandByInjection executeCommand, no registered shell.")
            return errorShellRes("executeCommandByInjection su impl is not attached")
        }
        val first = shellStubs.getRegisteredCallbackItem(0)
        logger.d("executeCommandByInjection Using shell: $first")
        val requestId = ShellSerialNum.nextId().toString()
        return runCatching {
            val receiver = SynchronousResultReceiver.get()
            logger.d("executeCommandByInjection Using receiver: $receiver")
            first.exe(requestId, arrayOf(command), receiver, singleShot)
            logger.d("executeCommandByInjection After call exe, requestId: $requestId")
            val receiverRes = receiver.awaitResultNoInterrupt(
                Duration.ofMinutes(
                    SHELL_TIMEOUT_MINUTES
                )
            )
            logger.d("executeCommandByInjection receiver get res: $receiverRes")
            val byteRes: ByteArrayWrapper? = receiverRes.getValue(null)
            byteRes?.let { ShellRes.parseFrom(byteRes.byteData) }
                ?: errorShellRes("executeCommandByInjection No shell res returned.")
        }.getOrElse {
            logger.e(it, "executeCommandByInjection Got error call shell: $it")
            // Notify Shell to shutdown
            if (it is CancellationException) {
                logger.i("Shutdown shell: $requestId since it is CancellationException.")
                first.close(requestId)
            }
            errorShellRes(it.toString())
        }
    }

    private fun errorShellRes(error: String): ShellRes {
        val prefix =
            "Shell execute fail"
        throw IllegalStateException("$prefix \n  $error")
    }
}


internal class AppShellServiceProxy(context: Context, intent: Intent?) :
    AppServiceProxy(context, intent) {
    private val logger = tornaco.apps.shortx.core.util.Logger("AppShellServiceProxy")
    private var impl: ISu? = null

    override fun onConnected(binder: IBinder) {
        impl = ISu.Stub.asInterface(binder)
    }

    fun exe(command: Array<String?>, singleShot: Boolean): ShellRes? {
        logger.d("execute: $command")
        val res: AtomicReference<ShellRes> = AtomicReference()
        val requestId = ShellSerialNum.nextId().toString()
        setTask(object : ProxyTask() {
            override fun run() {
                runCatching {
                    val receiver = SynchronousResultReceiver.get()
                    requireNotNull(impl) { "Impl is null." }.exe(
                        requestId,
                        command,
                        receiver,
                        singleShot
                    )
                    val receiverRes =
                        runBlocking { receiver.awaitResultNoInterrupt(Duration.ofMinutes(2)) }
                    logger.d("receiver get res: $receiverRes")
                    val byteRes: ByteArrayWrapper? = receiverRes.getValue(null)
                    res.set(
                        if (byteRes == null) {
                            null
                        } else {
                            ShellRes.parseFrom(byteRes.byteData)
                        }
                    )
                }.onFailure {
                    logger.e(it, "ProxyTask")
                    res.set(null)
                }
            }

        })
        waitForCompletion()
        return res.get()
    }

    companion object {
        fun exe(context: Context, command: String, singleShot: Boolean): ShellRes? {
            val intent = Intent()
            intent.setPackage(BuildProp.APP_PACKAGE_NAME)
            intent.component =
                ComponentName.unflattenFromString("${BuildProp.APP_PACKAGE_NAME}/${BuildProp.APP_SHELL_SERVICE_NAME}")
            return AppShellServiceProxy(context, intent).exe(arrayOf(command), singleShot)
        }
    }
}


object ShellSerialNum {
    private val _id = AtomicInteger(0)

    fun nextId() = _id.getAndIncrement()
}