package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.IApplicationThread
import android.content.Context
import android.os.ShellCommand
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.platform.Classes.amsClass
import tornaco.apps.shortx.core.platform.Classes.amsLifecycleClass
import tornaco.apps.shortx.core.platform.Classes.amsShellCommandClass
import tornaco.apps.shortx.core.platform.Fields
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.field

object AMSHook {
    private val logger = Logger("ShortXHook.AMSHook")

    fun hookAMS(lpparam: ISystemServerLoaded.Param) {
        // Start.
        kotlin.runCatching {
            afterMethod(lpparam.classLoader.amsClass(), Methods.start) { param ->
                val context = param.thisObject.field<Context>(Fields.mContext)
                logger.w("AMS start")
                if (!ShortXService.isStarted) {
                    logger.w("Invoke ShortXService start")
                    ShortXService.start(context)
                }
            }
        }.onFailure {
            logger.e(it, "hookAMS.start")
        }

        // System ready.
        runCatching {
            afterMethod(lpparam.classLoader.amsClass(), Methods.systemReady) {
                logger.w("AMS systemReady")
                ShortXService.systemReady()
            }
        }.onFailure {
            logger.e(it, "hookAMS.systemReady")
        }

        if (BuildPropInternal.APP_IS_DEBUG) {
            runCatching {
                beforeMethod(lpparam.classLoader.amsClass(), "attachApplication") { param ->
                    param.logParamsOnDebug()
                    val appThread = param.args[0] as IApplicationThread
                    ShortXService.shortXContext.appState.attachApplication(appThread)?.let {
                        param.args[0] = it
                    }
                }
            }.onFailure {
                logger.e(it, "hookAMS.attachApplication")
            }
        }
    }

    fun hookAMSLifecycleStart(lpparam: ISystemServerLoaded.Param) = kotlin.runCatching {
        afterMethod(lpparam.classLoader.amsLifecycleClass(), "onStart") { param ->
            val context = param.thisObject.field<Context>(Fields.mContext)
            logger.w("AMS Lifecycle start")
            if (!ShortXService.isStarted) {
                logger.w("Invoke ShortXService start")
                ShortXService.start(context)
            }
        }
    }.onFailure {
        logger.e(it, "hookAMS")
    }

    fun hookAMSShellCommand(lpparam: ISystemServerLoaded.Param) = kotlin.runCatching {
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
