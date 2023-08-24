package tornaco.apps.shortx.ui

import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.TextUtils
import lang3.RandomStringUtils
import tornaco.android.sec.net.S
import tornaco.apps.shortx.core.isSuccess
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.I18NCache
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.util.getLVLToken
import tornaco.apps.shortx.ui.subscribe.util.isGPToken
import tornaco.apps.shortx.ui.subscribe.util.setLVLChecked
import tornaco.apps.shortx.ui.util.LCRulesAdapter
import java.util.UUID

object EntryPoint {
    private val logger = Logger("EntryPoint")

    fun onCreate(context: Context) {
        initDeviceId(context)
        I18NCache.init(context)
        LVLStateHolder.init(context)
        checkCB(context)
        LCRulesAdapter.initRules(context)
    }

    private fun checkCB(context: Context) {
        logger.d("check CB")
        if (shortXManager.isInstalled && LVLStateHolder.subscribeState.value.isSubscribed) {
            getLVLToken(context) { token ->
                if (!token.isGPToken()) {
                    val deviceId = getDeviceId(context)
                    logger.p("check CB, $token $deviceId")
                    shortXManager.vb(token, deviceId) {
                        logger.d("check CB res: $it")
                        if (it.isSuccess) {
                            S.c(token, it.k)
                            logger.d("check CB success")
                        } else {
                            logger.d("check CB fail, uncheck")
                            setLVLChecked(context, false)
                        }
                    }
                }
            }
        } else {
            logger.d("check CB skip")
        }
    }

    private fun initDeviceId(context: Context) {
        if (!TextUtils.isEmpty(getDeviceId(context))) {
            return
        }
        val id: String = generateDeviceId(context)

        logger.d("generateDeviceId: $id")
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString("did", id)
            .apply()
    }

    fun getDeviceId(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString("did", "") ?: ""
    }

    private fun getAndroidId(context: Context): String {
        return try {
            Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Throwable) {
            logger.e(e, "Error getAndroidId")
            ""
        }
    }

    private fun generateDeviceId(context: Context): String {
        return getAndroidId(context) + "_" + RandomStringUtils.randomAlphanumeric(12) + "_" + UUID.randomUUID()
            .toString().hashCode()
    }
}