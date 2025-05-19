package tornaco.apps.shortx.services.xposed.hooks.hook

import android.net.NetworkInfo.DetailedState
import tornaco.apps.shortx.core.platform.Classes.vpnClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

object VPNHook {
    private val logger = Logger("ShortXHook.VPNHook")

    fun hook(lpparam: ISystemServerLoaded.Param) {
        logger.d("hook")
        afterMethod(lpparam.classLoader.vpnClass(), Methods.updateState) { param ->
            logger.d("updateState: ${param.args.map { it }}")
            val details: DetailedState = param.args[0] as DetailedState
            ShortXService.shortXContext.network.onVpnStateChanged(details)
        }
    }
}