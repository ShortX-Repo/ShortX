package tornaco.apps.shortx.ui.subscribe.util

import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.DeviceId
import tornaco.apps.shortx.ui.KEY_SUB_TOKEN
import tornaco.apps.shortx.ui.KEY_SUB_TOKEN_SERVER
import tornaco.apps.shortx.ui.MMKVExt
import tornaco.apps.shortx.ui.TOKEN_GOOGLE_PLAY
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder

val lvlLogger = Logger("ShortXApp")
private var skipTimes = 0
var isSkipping = false

fun String.isGPToken() = TOKEN_GOOGLE_PLAY == this

inline fun getLVLToken(onRes: (String?) -> Unit) {
    val token = MMKVExt.encMMKV(DeviceId.requireDeviceId()).decodeString(KEY_SUB_TOKEN)
    lvlLogger.p("getLVLToken return: $token")
    onRes(token)
}

inline fun getLVLTokenForServer(onRes: (String?) -> Unit) {
    val token = MMKVExt.encMMKV(DeviceId.requireDeviceId()).decodeString(KEY_SUB_TOKEN_SERVER)
    lvlLogger.p("getLVLTokenForServer return: $token")
    onRes(token)
}

fun setLVLToken(token: String?) {
    val kv = MMKVExt.encMMKV(DeviceId.requireDeviceId())
    kv.encode(KEY_SUB_TOKEN, token)
    kv.encode(KEY_SUB_TOKEN_SERVER, token)
    lvlLogger.p("setLVLToken token: $token")
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