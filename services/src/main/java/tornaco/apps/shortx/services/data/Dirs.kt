package tornaco.apps.shortx.services.data

import android.os.UserHandle
import java.io.File

object Dirs {

    fun baseServerDir() = DirInit.baseServerDirMayInit()

    fun baseServerCacheDir() = File(baseServerDir(), "cache")
    fun baseServerRecordingDir() = File(baseServerDir(), "record")
    fun baseServerFlagsDir() = File(baseServerDir(), "flags")
    fun picassoCacheDir() = File(baseServerCacheDir(), "picasso")
    fun jsCacheDir() = File(baseServerCacheDir(), "js")
    fun httpDir() = File(baseServerCacheDir(), "http")
    fun methodHookDir() = File(baseServerCacheDir(), "hook")

    fun baseServerDataDir(userId: Int = UserHandle.USER_SYSTEM) =
        File(baseServerDir(), "data/u/$userId")

    fun baseServerLogDir() =
        File(baseServerDir(), "log")

    fun baseServerTmpDir(): File {
        return File(baseServerDir(), "tmp")
    }

    fun dataStoreDir() = File(baseServerDataDir(), "data_store")

    fun dataStoreFile(prefName: String) = File(dataStoreDir(), prefName.let {
        if (it.endsWith(".pb")) it else "$it.pb"
    })
}
