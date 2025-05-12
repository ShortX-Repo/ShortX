package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.pm.ApplicationInfo
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.platform.Classes.atmsClass
import tornaco.apps.shortx.core.util.Logger

object ATMSHook {
    private val logger = Logger("ShortXHook.ATMSHook")

    fun hookATMS(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (BuildPropInternal.APP_IS_DEBUG) {
            kotlin.runCatching {
                //  public PreBindInfo preBindApplication(WindowProcessController wpc, ApplicationInfo info)
                afterMethod(lpparam.classLoader.atmsClass(), "preBindApplication") { param ->
                    param.logParamsOnDebug()
                    val info = param.args[1] as ApplicationInfo
                    if (info.packageName == BuildPropInternal.APP_PACKAGE_NAME) {
                        logger.d("preBindApplication- ShortX")
                        val preBindInfo = param.result
                        
                    }
                }
            }.onFailure {
                logger.e(it, "hookAMS.start")
            }
        }
    }
}
