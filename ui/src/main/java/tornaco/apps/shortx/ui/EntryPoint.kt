package tornaco.apps.shortx.ui

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import tornaco.apps.shortx.ui.util.ShortcutHelper
import tornaco.apps.shortx.ui.util.verifyOtp


object EntryPoint {
    private val logger = Logger("EntryPoint")

    fun onCreate(context: Context) {
        MMKVExt.initMMKV(context)
        DeviceId.initDeviceId()
        I18NCache.init(context)
        LVLStateHolder.init(context)
        checkCB(context)
        LCRulesAdapter.initRules(context)

        // Async clean up.
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                ShortcutHelper.cleanUp(context)
            }
        }
    }

    private fun checkCB(context: Context) {
        logger.p("check CB")
        if (shortXManager.isInstalled) {
            getLVLToken { token ->
                if (!token.isNullOrEmpty() && !token.isGPToken()) {
                    val deviceId = DeviceId.requireDeviceId()
                    logger.p("check CB, $token $deviceId")
                    shortXManager.vb(token, deviceId) {
                        logger.p("check CB res: $it")
                        if (it.isSuccess) {
                            S.c(token, it.k)
                            logger.p("check CB success")
                            verifyOtp(token, it.i,
                                onRes = {
                                    logger.p("check OTP success")
                                },
                                onError = {
                                    logger.p("check OTP fail")
                                    // Usually make mistake?
                                    // setLVLToken(null)
                                })
                        } else {
                            logger.p("check CB fail, uncheck")
                            setLVLToken(null)
                        }
                    }
                }
            }
        } else {
            logger.p("check CB skip")
        }
    }


}