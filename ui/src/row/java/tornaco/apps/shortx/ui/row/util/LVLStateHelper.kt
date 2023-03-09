package tornaco.apps.shortx.ui.row.util

import android.content.Context
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.EntryPoint

private val logger = Logger()
private var skipTimes = 0
var isSkipping = false

fun isLVLChecked(context: Context): Boolean {
    val deviceId: String = EntryPoint.getDeviceId(context)
    val pref: EncryptedPreferences = EncryptedPreferences.Builder(context)
        .withEncryptionPassword(deviceId)
        .build()
    return try {
        // Verify only once.
        java.lang.Boolean.parseBoolean(
            pref.getString(
                BuildProp.APP_PACKAGE_NAME + "@LVL",
                "FFFFFFFFFFFFFFFFFF"
            )
        )
    } catch (e: Throwable) {
        false
    }
}

fun setLVLChecked(context: Context, checked: Boolean) {
    val deviceId: String = EntryPoint.getDeviceId(context)
    val pref: EncryptedPreferences = EncryptedPreferences.Builder(context)
        .withEncryptionPassword(deviceId)
        .build()
    try {
        pref.edit().putBoolean(BuildProp.APP_PACKAGE_NAME + "@LVL", checked).apply()
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