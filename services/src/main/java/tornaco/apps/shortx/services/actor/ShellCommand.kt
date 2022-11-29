package tornaco.apps.shortx.services.actor

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.services.util.AppServiceProxy

class ShellCommand(
    private val context: Context,
    private val shellCommand: String,
) {
    operator fun invoke() {
        ShellServiceProxy.exe(context, shellCommand)
    }
}

internal class ShellServiceProxy(context: Context, intent: Intent?) :
    AppServiceProxy(context, intent) {
    private var impl: ISu? = null

    override fun onConnected(binder: IBinder) {
        impl = ISu.Stub.asInterface(binder)
    }

    fun exe(command: Array<String?>) {
        setTask(object : ProxyTask() {
            override fun run() {
                impl?.exe(command)
            }

        })
        waitForCompletion()
    }

    companion object {
        fun exe(context: Context, command: String) {
            val intent = Intent()
            intent.setPackage("")
            intent.component =
                ComponentName.unflattenFromString("${BuildProp.APP_PACKAGE_NAME}/${BuildProp.APP_SHELL_SERVICE_NAME}")
            return ShellServiceProxy(context, intent).exe(arrayOf(command))
        }
    }
}