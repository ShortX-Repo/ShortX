package tornaco.apps.shortx.services.xposed.hooks.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.nfcServiceHandlerClass
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object NFCServiceHook {
    private val logger = Logger("ShortXHook.NFCServiceHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == PackageNames.nfc) {
            afterMethod(lpparam.classLoader.nfcServiceHandlerClass(), "dispatchTagEndpoint") {
                logger.d("dispatchTagEndpoint: ${it.args[0]}")
                val tag = it.args[0]
                val uid = Reflections.callMethod(tag, "getUid") as ByteArray
                logger.d("dispatchTagEndpoint, uid: $uid ${String(uid)}")
            }
        }
    }
}