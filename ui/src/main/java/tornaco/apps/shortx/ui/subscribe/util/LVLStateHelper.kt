package tornaco.apps.shortx.ui.subscribe.util

import android.content.Context
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.EntryPoint

private val logger = Logger()
private var skipTimes = 0
var isSkipping = false

const val LVL_SRC_GP = "GP"

fun String.isGPToken() = LVL_SRC_GP == this

fun isLVLChecked(context: Context): Boolean {
    val deviceId: String = EntryPoint.getDeviceId(context)
    val pref: EncryptedPreferences = EncryptedPreferences.Builder(context)
        .withEncryptionPassword(deviceId)
        .build()
    return try {
        val data = pref.getString(
            BuildProp.APP_PACKAGE_NAME + "_lang.vertical.lemon",
            Nothing::class.java.name
        )
        val bool = data.split("-")[0]
        java.lang.Boolean.parseBoolean(bool)
    } catch (e: Throwable) {
        false
    }
}

fun getLVLToken(context: Context): String {
    val deviceId: String = EntryPoint.getDeviceId(context)
    val pref: EncryptedPreferences = EncryptedPreferences.Builder(context)
        .withEncryptionPassword(deviceId)
        .build()
    return try {
        val data = pref.getString(
            BuildProp.APP_PACKAGE_NAME + "_lang.vertical.lemon",
            Nothing::class.java.name
        )
        val token = data.split("-")[1]
        token
    } catch (e: Throwable) {
        ""
    }
}

fun setLVLChecked(context: Context, checked: Boolean, token: String = "") {
    val deviceId: String = EntryPoint.getDeviceId(context)
    val pref: EncryptedPreferences = EncryptedPreferences.Builder(context)
        .withEncryptionPassword(deviceId)
        .build()
    try {
        pref.edit()
            .putString(BuildProp.APP_PACKAGE_NAME + "_lang.vertical.lemon", "$checked-$token")
            .apply()
    } catch (e: Throwable) {
        logger.e(e)
    }
}

fun skipLicenseCheckOnce(context: Context) {
    skipTimes += 1
}

fun hasAChanceToSkipLicenseCheck(context: Context): Boolean {
    return skipTimes <= 9
}