package tornaco.apps.shortx.services

import android.content.Context
import android.view.InputEvent
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.services.hook.Classes.amsClass
import tornaco.apps.shortx.services.hook.Fields.mContext
import tornaco.apps.shortx.services.hook.Methods.start
import tornaco.apps.shortx.services.hook.PackageNames.android
import tornaco.apps.shortx.services.hook.afterMethod
import tornaco.apps.shortx.services.hook.field

class XposedEntry : IXposedHookLoadPackage {
    private val logger = Logger("XposedEntry")

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (android == lpparam?.packageName) {
            logger.i("Loading android. ${XposedHelpers::class.java.classLoader}")

            afterMethod(lpparam.classLoader.amsClass(), start) { param ->
                val context = param.thisObject.field<Context>(mContext)
                ShortXService.start(context)
            }

            afterMethod(XposedHelpers.findClass("com.android.server.input.InputManagerService",
                lpparam.classLoader), "filterInputEvent") {
                ShortXService.filterInput(it.args[0] as InputEvent)
            }
        }
    }
}