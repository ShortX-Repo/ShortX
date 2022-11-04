package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Context
import android.view.InputEvent
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.amsClass
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.inputManagerService
import tornaco.apps.shortx.services.xposed.hooks.hook.Fields.mContext
import tornaco.apps.shortx.services.xposed.hooks.hook.Methods.filterInputEvent
import tornaco.apps.shortx.services.xposed.hooks.hook.Methods.start
import tornaco.apps.shortx.services.xposed.hooks.hook.Methods.systemReady
import tornaco.apps.shortx.services.xposed.hooks.hook.PackageNames.android

class XposedEntry : IXposedHookLoadPackage {
    private val logger = Logger("XposedEntry")

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (android == lpparam?.packageName) {
            logger.i("Loading android. ${XposedHelpers::class.java.classLoader}")

            afterMethod(lpparam.classLoader.amsClass(), start) { param ->
                val context = param.thisObject.field<Context>(mContext)
                ShortXService.start(context)
            }

            afterMethod(lpparam.classLoader.amsClass(), systemReady) {
                ShortXService.systemReady()
            }

            afterMethod(lpparam.classLoader.inputManagerService(), start) { param ->
                val nat = param.thisObject.field<Any>("mNative")
                XposedHelpers.callMethod(nat, "setInputFilterEnabled", true)
            }

            beforeMethod(lpparam.classLoader.inputManagerService(), filterInputEvent) { param ->
                val res = ShortXService.filterInput(param.args[0] as InputEvent)
                res?.let {
                    param.result = it
                }
            }
        }
    }
}