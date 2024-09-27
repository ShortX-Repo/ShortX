package tornaco.apps.shortx.ui

import android.content.Context
import com.tencent.mmkv.MMKV
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug

const val MMKV_ID_ENC = "07538143-3E64-483B-B607-6E929E6D8CC0"
const val KEY_DEVICE_ID = "39A5DC32-B10D-4370-AB98-A9A809256885"

object MMKVExt {
    private val logger = tornaco.apps.shortx.core.util.Logger(MMKVExt::class.java.simpleName)

    fun initMMKV(context: Context) {
        MMKV.initialize(context).apply {
            logger.logOnDebug("mmkl installed: $this")
        }
    }

    fun defaultMMKV() =
        MMKV.defaultMMKV(MMKV.SINGLE_PROCESS_MODE, BuildPropInternal.APP_PACKAGE_NAME)

    fun encMMKV() =
        MMKV.mmkvWithID(MMKV_ID_ENC, MMKV.SINGLE_PROCESS_MODE, DeviceId.requireDeviceId())
}