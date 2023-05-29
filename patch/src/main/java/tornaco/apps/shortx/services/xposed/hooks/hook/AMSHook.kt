package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Context
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Fields
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.amsClass

fun XposedEntry.hookAMS(lpparam: XC_LoadPackage.LoadPackageParam) {
    afterMethod(lpparam.classLoader.amsClass(), Methods.start) { param ->
        val context = param.thisObject.field<Context>(Fields.mContext)
        logger.w("AMS start")
        ShortXService.start(context)
    }

    afterMethod(lpparam.classLoader.amsClass(), Methods.systemReady) {
        logger.w("AMS systemReady")
        ShortXService.systemReady()
    }
}