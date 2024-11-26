package tornaco.apps.shortx.ui.subscribe

import android.content.Context
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.logOnDebug
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

const val EXT_JS = "7E4BEEE5A92DCDDD48B5B7798CDEC79A"
const val EXT_DEX = "2C318A629B22F56EA6665AC81F0210CB"

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
        crackLogger.d("AntiCrack hookVipEnc - dec: ${EncryptUtils.decrypt(EXT_JS)}")
        crackLogger.d("AntiCrack hookVipEnc - dec: ${EncryptUtils.decrypt(EXT_DEX)}")
    }
    val crackDetected = crackModPaths.any {
        kotlin.runCatching { File(dataDir, EncryptUtils.decrypt(it)).exists() }.getOrElse {
            crackLogger.logOnDebug(it.stackTraceToString())
            false
        }
    } || hasJsAndDexFilesInAnyFolder(dataDir)
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

fun hasJsAndDexFilesInAnyFolder(directory: File): Boolean {
    // 遍历主目录及子目录
    directory.listFiles()?.forEach { file ->
        if (file.isDirectory) {
            val containsJs =
                file.listFiles()?.any { it.extension == EncryptUtils.decrypt(EXT_JS) } ?: false
            val containsDex =
                file.listFiles()?.any { it.extension == EncryptUtils.decrypt(EXT_DEX) } ?: false

            // 如果当前文件夹包含 .js 和 .dex 文件，则返回 true
            if (containsJs && containsDex) {
                return true
            }

            // 递归检查子目录
            if (hasJsAndDexFilesInAnyFolder(file)) {
                return true
            }
        }
    }
    return false
}