package tornaco.apps.shortx.ui

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.android.sec.net.S
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.isSuccess
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.res.PicassoInitializer
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.I18NCache
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.initContextVM
import tornaco.apps.shortx.ui.subscribe.util.getLVLToken
import tornaco.apps.shortx.ui.subscribe.util.isGPToken
import tornaco.apps.shortx.ui.subscribe.util.setLVLToken
import tornaco.apps.shortx.ui.util.LCRulesAdapter
import tornaco.apps.shortx.ui.util.StubApkGen
import tornaco.apps.shortx.ui.util.verifyOtp


object EntryPoint {
    private val logger = Logger("EntryPoint")

    fun onCreate(context: Context) {
        MMKVExt.initMMKV(context)
        DeviceId.initDeviceId()
        I18NCache.init(context)
        LVLStateHolder.init(context)
        checkCB(context)
        initContextVM()

        PicassoInitializer.install(
            context,
            context.cacheDir,
            BuildPropInternal.APP_PACKAGE_NAME
        )

        // Async init.
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                LCRulesAdapter.initRules(context)
                StubApkGen.cleanUp(context)
            }
        }

        devTest(context)
    }

    private fun checkCB(context: Context) {
        logger.logOnDebug("check CB")
        if (shortXManager.isInstalled) {
            getLVLToken { token ->
                if (!token.isNullOrEmpty() && !token.isGPToken()) {
                    val deviceId = DeviceId.requireDeviceId()
                    logger.logOnDebug("check CB, $token $deviceId")
                    shortXManager.loadOnlineRules(token, deviceId) {
                        logger.logOnDebug("check CB res: $it")
                        if (it.isSuccess) {
                            S.c(token, it.k)
                            logger.logOnDebug("check CB success")
                            verifyOtp(token, it.i,
                                onRes = {
                                    logger.logOnDebug("check OTP success")
                                },
                                onError = {
                                    logger.logOnDebug("check OTP fail")
                                    // Usually make mistake?
                                    // setLVLToken(null)
                                })
                        } else {
                            logger.logOnDebug("check CB fail, uncheck")
                            setLVLToken(null)
                        }
                    }
                }
            }
        } else {
            logger.logOnDebug("check CB skip")
        }
    }


}