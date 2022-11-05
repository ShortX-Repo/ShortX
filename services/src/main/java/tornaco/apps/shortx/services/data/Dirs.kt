package tornaco.apps.shortx.services.data

import android.os.UserHandle
import java.io.File

object Dirs {

    private fun baseServerDir() = DirInit.baseServerDirMayInit()

    fun baseServerDataDir(userId: Int = UserHandle.USER_SYSTEM) =
        File(baseServerDir(), "data/u/$userId")

    fun dataStoreDir() = File(baseServerDataDir(), "data_store")

    fun dataStoreFile(prefName: String) = File(dataStoreDir(), prefName.let {
        if (it.endsWith(".pb")) it else "$it.pb"
    })
}
