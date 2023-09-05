package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Context
import android.os.ShellCommand
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.amsClass
import tornaco.apps.shortx.core.platform.Classes.amsShellCommandClass
import tornaco.apps.shortx.core.platform.Fields
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import util.field

object AMSHook {
    private val logger = Logger("ShortXHook.AMSHook")

    fun hookAMS(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        afterMethod(lpparam.classLoader.amsClass(), Methods.start) { param ->
            val context = param.thisObject.field<Context>(Fields.mContext)
            logger.w("AMS start")
            ShortXService.start(context)
        }

        afterMethod(lpparam.classLoader.amsClass(), Methods.systemReady) {
            logger.w("AMS systemReady")
            ShortXService.systemReady()
        }
    }.onFailure {
        logger.e(it, "hookAMS")
    }

    fun hookAMSShellCommand(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        beforeMethod(lpparam.classLoader.amsShellCommandClass(), "onCommand") { param ->
            val command = param.args[0].toString()
            logger.d("onCommand: $command")
            if (command.startsWith("shortx")) {
                val shellCommand = param.thisObject as ShellCommand
                param.result = ShortXService.onCommand(shellCommand)
            }
        }
    }.onFailure {
        logger.e(it, "hookAMSShellCommand")
    }
}
