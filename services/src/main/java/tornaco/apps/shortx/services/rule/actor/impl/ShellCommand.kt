package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.protobuf.Message
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.action.ShellCommand
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.util.AppServiceProxy


object ShellCommand : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShellCommand::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val sc = action unpack_ ShellCommand::class.java
        ShellServiceProxy.exe(context, sc.command)
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