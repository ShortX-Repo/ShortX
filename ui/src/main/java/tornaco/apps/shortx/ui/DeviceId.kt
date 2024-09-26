package tornaco.apps.shortx.ui

import lang3.RandomStringUtils
import tornaco.apps.shortx.core.util.Logger
import java.util.UUID

object DeviceId {
    private val logger = Logger("ShortXApp")

    fun requireDeviceId() = requireNotNull(getDeviceId())
    fun requireTokenKey() =
        requireNotNull(MMKVExt.defaultMMKV().decodeString(generateTokenKeyKey()))

    fun requireTokenForServerKey() =
        requireNotNull(MMKVExt.defaultMMKV().decodeString(generateTokenForServerKeyKey()))

    fun requireGPToken() = requireNotNull(MMKVExt.defaultMMKV().decodeString(generateGPTokenKey()))

    fun requireCrackFlagKey() =
        requireNotNull(MMKVExt.defaultMMKV().decodeString(generateCrackFlagKey()))

    fun initDeviceId() {
        if (getDeviceId().isNullOrEmpty()) {
            val id: String = generateDeviceId()
            logger.p("generateDeviceId: $id")
            MMKVExt.defaultMMKV().encode(KEY_DEVICE_ID, id)
            // Token key
            val tokenKeyKey = generateTokenKeyKey()
            logger.p("tokenKey: $tokenKeyKey")
            MMKVExt.defaultMMKV().encode(tokenKeyKey, generateKeyValue())
            // Token for server key
            val tokenForServerKeyKey = generateTokenForServerKeyKey()
            logger.p("tokenForServerKey: $tokenForServerKeyKey")
            MMKVExt.defaultMMKV().encode(tokenForServerKeyKey, generateKeyValue())
            // GP token
            val gpTokenKeyKey = generateGPTokenKey()
            logger.p("gpTokenKeyKey: $gpTokenKeyKey")
            MMKVExt.defaultMMKV().encode(gpTokenKeyKey, generateKeyValue())
        } else {
            logger.p("deviceId exists: ${getDeviceId()}")
        }

        // Added 2024-09-27
        // Crack key
        runCatching {
            requireCrackFlagKey()
        }.onFailure {
            val crackFlagKey = generateCrackFlagKey()
            logger.p("Generated crackFlagKey: $crackFlagKey")
            MMKVExt.defaultMMKV().encode(crackFlagKey, generateKeyValue())
        }

        requireTokenKey().apply { logger.p("Double check requireTokenKey: $this") }
        requireTokenForServerKey().apply { logger.p("Double check requireTokenForServerKey: $this") }
        requireGPToken().apply { logger.p("Double check requireGPToken: $this") }
        requireCrackFlagKey().apply { logger.p("Double check requireCrackFlagKey: $this") }
    }

    private fun generateTokenKeyKey() = requireDeviceId().reversed()
    private fun generateTokenForServerKeyKey() = requireDeviceId().repeat(2)
    private fun generateGPTokenKey() = requireDeviceId().repeat(3).reversed()
    private fun generateCrackFlagKey() = requireDeviceId().repeat(4).reversed().take(10)

    private fun getDeviceId(): String? {
        return MMKVExt.defaultMMKV().decodeString(KEY_DEVICE_ID)
    }

    private fun generateDeviceId(): String {
        return RandomStringUtils.randomAlphanumeric(12) + "_" + UUID.randomUUID().toString()
            .hashCode()
    }

    private fun generateKeyValue(): String {
        return RandomStringUtils.randomAlphanumeric(21)
    }
}