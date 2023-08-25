package tornaco.apps.shortx.ui

import android.content.Context
import tornaco.android.sec.net.S
import tornaco.apps.shortx.core.isSuccess
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.I18NCache
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.util.getLVLToken
import tornaco.apps.shortx.ui.subscribe.util.isGPToken
import tornaco.apps.shortx.ui.subscribe.util.setLVLToken
import tornaco.apps.shortx.ui.util.LCRulesAdapter


object EntryPoint {
    private val logger = Logger("EntryPoint")

    fun onCreate(context: Context) {
        MMKVExt.initMMKV(context)
        DeviceId.initDeviceId()
        I18NCache.init(context)
        LVLStateHolder.init(context)
        checkCB(context)
        LCRulesAdapter.initRules(context)
    }

    private fun checkCB(context: Context) {
        logger.d("check CB")
        if (shortXManager.isInstalled) {
            getLVLToken { token ->
                if (!token.isNullOrEmpty() && !token.isGPToken()) {
                    val deviceId = DeviceId.requireDeviceId()
                    logger.p("check CB, $token $deviceId")
                    shortXManager.vb(token, deviceId) {
                        logger.d("check CB res: $it")
                        if (it.isSuccess) {
                            S.c(token, it.k)
                            logger.d("check CB success")
                        } else {
                            logger.d("check CB fail, uncheck")
                            setLVLToken(null)
                        }
                    }
                }
            }
        } else {
            logger.d("check CB skip")
        }
    }


}