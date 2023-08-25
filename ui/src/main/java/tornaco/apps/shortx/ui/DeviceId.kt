package tornaco.apps.shortx.ui

import lang3.RandomStringUtils
import tornaco.apps.shortx.core.util.Logger
import java.util.UUID

object DeviceId {
    private val logger = Logger("ShortXApp")

    fun requireDeviceId() = requireNotNull(getDeviceId())

    fun initDeviceId() {
        if (getDeviceId().isNullOrEmpty()) {
            val id: String = generateDeviceId()
            logger.p("generateDeviceId: $id")
            MMKVExt.defaultMMKV().encode(KEY_DEVICE_ID, id)
        } else {
            logger.p("deviceId exists: ${getDeviceId()}")
        }
    }

    private fun getDeviceId(): String? {
        return MMKVExt.defaultMMKV().decodeString(KEY_DEVICE_ID)
    }


    private fun generateDeviceId(): String {
        return RandomStringUtils.randomAlphanumeric(12) + "_" + UUID.randomUUID().toString()
            .hashCode()
    }
}