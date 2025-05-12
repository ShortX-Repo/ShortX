package tornaco.apps.shortx.services.data

import android.os.Build
import android.os.UserHandle
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import java.io.File

object Dirs {
    var baseServerDirInjection: File? = null
    val baseServerDir by lazy { baseServerDirInjection ?: DirInit.baseServerDirMayInit() }

    fun baseServerCacheDir() = File(baseServerDir, "cache")
    fun baseServerRecordingDir() = File(baseServerDir, "record")
    fun baseServerFlagsDir() = File(baseServerDir, "flags")
    fun picassoCacheDir() = File(baseServerCacheDir(), "picasso")
    fun jsCacheDir() = File(baseServerCacheDir(), "js")
    fun httpDir() = File(baseServerCacheDir(), "http")
    fun methodHookDir() = File(baseServerCacheDir(), "hook")
    fun backupCacheDir() = File(baseServerCacheDir(), "backup")
    fun pluginCacheDir() = File(baseServerCacheDir(), "plugin")
    fun screenshotCacheDir() = File(baseServerCacheDir(), "screenshot")
    fun dexCacheDir() = File(baseServerCacheDir(), "dex_${Build.VERSION.SDK_INT}")
    private fun ruleCacheDir() = File(baseServerCacheDir(), "rule")
    fun ruleCacheDir(ruleSrc: EvaluateContext) = File(ruleCacheDir(), ruleSrc.srcId)

    private fun baseServerDataDir(userId: Int = UserHandle.USER_SYSTEM) =
        File(baseServerDir, "data/u/$userId")

    fun baseServerLogDir() =
        File(baseServerDir, "log")

    fun baseServerTmpDir(): File {
        return File(baseServerDir, "tmp")
    }

    fun dataStoreDir() = File(baseServerDataDir(), "data_store")

    fun dataStoreFile(prefName: String) = File(dataStoreDir(), prefName.let {
        if (it.endsWith(".pb")) it else "$it.pb"
    })
}
