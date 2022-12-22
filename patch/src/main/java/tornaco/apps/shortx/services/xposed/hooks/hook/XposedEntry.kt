package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.usage.UsageEvents
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.InputEvent
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.amsClass
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.inputManagerService
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.printManagerImplClass
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.usageStatsServiceClass
import tornaco.apps.shortx.services.xposed.hooks.hook.Fields.mContext
import tornaco.apps.shortx.services.xposed.hooks.hook.Methods.filterInputEvent
import tornaco.apps.shortx.services.xposed.hooks.hook.Methods.reportEvent
import tornaco.apps.shortx.services.xposed.hooks.hook.Methods.start
import tornaco.apps.shortx.services.xposed.hooks.hook.Methods.systemReady
import tornaco.apps.shortx.services.xposed.hooks.hook.PackageNames.android
import util.Reflections

class XposedEntry : IXposedHookLoadPackage, IXposedHookZygoteInit {
    private val logger = Logger("XposedEntry")

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (android == lpparam?.packageName) {
            ShortXService.main()
            logger.i("Loading android.")

            afterMethod(lpparam.classLoader.amsClass(), start) { param ->
                val context = param.thisObject.field<Context>(mContext)
                ShortXService.start(context)
            }

            afterMethod(lpparam.classLoader.amsClass(), systemReady) {
                ShortXService.systemReady()
            }

            afterMethod(lpparam.classLoader.inputManagerService(), start) { param ->
                val nat = param.thisObject.fieldOrNull<Any>("mNative")
                if (nat != null) {
                    Reflections.callMethod(nat, "setInputFilterEnabled", true)
                } else {
                    val ptr = Reflections.getObjectField(param.thisObject, "mPtr") as Long
                    Reflections.callMethod(
                        param.thisObject,
                        "nativeSetInputFilterEnabled",
                        ptr,
                        true
                    )
                }
            }

            beforeMethod(lpparam.classLoader.inputManagerService(), filterInputEvent) { param ->
                val res =
                    ShortXService.shortXContext.gesture.filterInput(param.args[0] as InputEvent)
                res?.let {
                    param.result = it
                }
            }

            beforeMethod(lpparam.classLoader.usageStatsServiceClass(), reportEvent) { param ->
                val event = param.args[0] as UsageEvents.Event
                val userId = param.args[1] as Int
                val type = event.mEventType
                val packageName = event.packageName
                val className = event.className
                if (type == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    ShortXService.shortXContext.appState.onComponentMoveToBg(
                        ComponentName(
                            packageName,
                            className
                        ), userId
                    )
                } else if (type == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    ShortXService.shortXContext.appState.onComponentMoveToFg(
                        ComponentName(
                            packageName,
                            className
                        ), userId
                    )
                }
            }

            beforeMethod(
                lpparam.classLoader.printManagerImplClass().apply {
                    logger.d("PrintManagerImpl class: $this")
                },
                Methods.print
            ) { hookParam ->
                logger.d("PrintManagerImpl.print: ${hookParam.args.map { it?.toString() }}")
                val printJobName = hookParam.args[0] as String
                if (printJobName == "ShortX") {
                    logger.d("Inject binder for print")
                    val service = ShortXService.serviceBinder
                    hookParam.result = Bundle().apply {
                        putBinder("shortX-Binder", service)
                    }
                }
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {

    }
}
