package tornaco.apps.shortx.ui

import android.content.Context
import com.tencent.mmkv.MMKV
import tornaco.apps.shortx.core.BuildProp

const val MMKV_ID_ENC = "android.m"
const val KEY_DEVICE_ID = "android.d"

const val KEY_SUB_TOKEN = "A772E654-1227-4565-839C-E9C4C9A22891"

// Token key for ShortX server.
const val KEY_SUB_TOKEN_SERVER = "71F53C88-C20C-491C-9A0E-4C81CBC6AAC1"

const val TOKEN_GOOGLE_PLAY = "99ee2a07-2485-4b20-85bd-cc2331f74f59"

object MMKVExt {
    private val logger = tornaco.apps.shortx.core.util.Logger(MMKVExt::class.java.simpleName)

    fun initMMKV(context: Context) {
        MMKV.initialize(context).apply {
            logger.p("mmkl installed: $this")
        }
    }

    fun defaultMMKV() = MMKV.defaultMMKV(MMKV.SINGLE_PROCESS_MODE, BuildProp.APP_PACKAGE_NAME)

    fun encMMKV(deviceId: String) = MMKV.mmkvWithID(MMKV_ID_ENC, MMKV.SINGLE_PROCESS_MODE, deviceId)
}