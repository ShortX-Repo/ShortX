package tornaco.apps.shortx.services.os

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.enforceCallingPermissions
import tornaco.apps.shortx.services.util.AppServiceProxy
import java.time.Duration
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

    fun executeCommand(command: String, singleShot: Boolean): ShellRes {
        return isInjectedShellEnabled().select(
            t = { executeCommandByInjection(command, singleShot) },
            f = {
                AppShellServiceProxy.exe(context, command, singleShot)
                    ?: errorShellRes("AppShellServiceProxy error")
            }
        )
    }

    private fun executeCommandByInjection(command: String, singleShot: Boolean): ShellRes {
        val itemCount = shellStubs.registeredCallbackCount
        if (itemCount == 0) {
            logger.d("executeCommand, no registered shell.")
            return errorShellRes("su impl is not attached")
        }
        val first = shellStubs.getRegisteredCallbackItem(0)
        logger.d("Using shell: $first")
        return runCatching {
            val receiver = SynchronousResultReceiver.get()
            first.exe(arrayOf(command), receiver, singleShot)
            val receiverRes = receiver.awaitResultNoInterrupt(Duration.ofDays(30))
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
        val prefix =
            "Fail execute shell: Pls allow ShortX start from background, and grant su permission to ShortX\n 执行失败，请允许ShortX后台自启动，授予ShortX Root权限"
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
        setTask(object : ProxyTask() {
            override fun run() {
                runCatching {
                    val receiver = SynchronousResultReceiver.get()
                    requireNotNull(impl) { "Impl is null." }.exe(command, receiver, singleShot)
                    val receiverRes = receiver.awaitResultNoInterrupt(Duration.ofMinutes(2))
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
