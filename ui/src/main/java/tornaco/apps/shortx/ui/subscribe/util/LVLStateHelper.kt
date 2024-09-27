package tornaco.apps.shortx.ui.subscribe.util

import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.DeviceId
import tornaco.apps.shortx.ui.MMKVExt
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder

val lvlLogger = Logger("ShortXApp")
private var skipTimes = 0
var isSkipping = false

inline fun String.isGPToken(consumer: () -> Unit = {}) = DeviceId.requireGPToken() == this

inline fun getLVLToken(onRes: (String?) -> Unit) {
    val token = MMKVExt.encMMKV().decodeString(DeviceId.requireTokenKey())
    lvlLogger.logOnDebug("getLVLToken return: $token")
    onRes(token)
}

inline fun getLVLTokenForServer(onRes: (String?) -> Unit) {
    val key = DeviceId.requireTokenForServerKey()
    lvlLogger.logOnDebug("getLVLTokenForServer, using key:$key")
    val token = MMKVExt.encMMKV().decodeString(key)
    lvlLogger.logOnDebug("getLVLTokenForServer, return: $token")
    onRes(token)
}

fun setLVLToken(token: String?) {
    val kv = MMKVExt.encMMKV()
    kv.encode(DeviceId.requireTokenKey(), token)
    kv.encode(DeviceId.requireTokenForServerKey(), token)
    lvlLogger.logOnDebug("setLVLToken token: ${DeviceId.requireTokenKey()} $token")
    lvlLogger.logOnDebug("setLVLTokenForServer token: ${DeviceId.requireTokenForServerKey()} $token")
    if (token != null) {
        runBlocking { LVLStateHolder.setSubscribed(token) }
    } else {
        runBlocking { LVLStateHolder.setNotSubscribed() }
    }
}

fun skipLicenseCheckOnce() {
    skipTimes += 1
}

fun hasAChanceToSkipLicenseCheck(): Boolean {
    return skipTimes <= 9
}