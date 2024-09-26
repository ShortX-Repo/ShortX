package tornaco.apps.shortx.ui.subscribe

import android.content.Context
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.stubShortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.DeviceId
import tornaco.apps.shortx.ui.MMKVExt
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.subscribe.util.EncryptUtils
import java.io.File

// AntiCrack

val crackLogger = Logger.nameless
val Context.isCrackedKey get() = DeviceId.requireCrackFlagKey()

// crackVersion
val Context.crackVersionAsUserId get() = MMKVExt.encMMKV().decodeInt(isCrackedKey, myUserId)

val crackModPaths = setOf(
    // HookVip
    "8B62FB022B2ABB5DFCAD97BCFD81D8EE"
)

fun initContextVM() {
    ContextViewModel.shortxProvider = {
        shortx {
            // Noop, just let it inline.
        }
    }
}

// Callback if shortx is activated.
inline fun Context.shortx(callback: ShortXManager.() -> Unit = {}): ShortXManager {
    if (BuildPropInternal.APP_IS_DEBUG) {
        crackLogger.d("AntiCrack MMKV isCrackedKey: $isCrackedKey")
        crackLogger.d(
            "AntiCrack MMKV isCrackedKey value: ${
                MMKVExt.encMMKV().decodeInt(isCrackedKey)
            }"
        )
        val hookVipEnc = EncryptUtils.encrypt("HookVip")
        crackLogger.d("AntiCrack hookVipEnc: $hookVipEnc")
        crackLogger.d("AntiCrack hookVipEnc - dec: ${EncryptUtils.decrypt(hookVipEnc)}")
    }
    val crackDetected = crackModPaths.any {
        kotlin.runCatching { File(dataDir, EncryptUtils.decrypt(it)).exists() }.getOrElse {
            crackLogger.p(it.stackTraceToString())
            false
        }
    }
    crackLogger.d("AntiCrack crackDetected: $crackDetected")
    if (crackDetected) {
        MMKVExt.encMMKV().encode(isCrackedKey, BuildPropInternal.APP_VERSION_CODE)
    }
    // Ensure paths it not cracked.
    if (crackModPaths.isNotEmpty()) {
        val shortx = shortXManager
        val appLabel = shortx.getAppLabel(
            AppPkg.newBuilder()
                .setPkgName(BuildPropInternal.APP_PACKAGE_NAME)
                .setUserId(crackVersionAsUserId)
                .build()
        )
        if (appLabel != null) {
            return shortx
        }
    }
    return stubShortXManager
}
