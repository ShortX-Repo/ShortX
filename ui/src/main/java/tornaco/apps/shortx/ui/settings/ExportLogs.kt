package tornaco.apps.shortx.ui.settings

import android.content.Context
import android.os.Environment
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.ZipUtils
import java.io.File
import java.io.IOException

val Context.logFolderPath get() = externalCacheDir.toString() + File.separator + "log"

suspend fun exportLogs(context: Context, shortXManager: ShortXManager): File {
    return withContext(Dispatchers.IO) {
        val backupTempDir = File(context.cacheDir, "ShortX-Log-${System.currentTimeMillis()}")
        val backupZipRootDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val name = autoGenLogFileName()

        kotlin.runCatching {
            // App log.
            File(context.logFolderPath).copyRecursively(backupTempDir)

            // Service log.
            val serviceLogFile = File(backupTempDir, "service.zip").apply { createNewFile() }
            val pfd =
                ParcelFileDescriptor.open(serviceLogFile, ParcelFileDescriptor.MODE_READ_WRITE)
            shortXManager.writeLogsTo(pfd)


            ZipUtils.zip(backupTempDir.absolutePath, backupZipRootDir.absolutePath, name)
        }.onFailure {
            throw IOException(it)
        }

        backupTempDir.deleteRecursively()
        File(backupZipRootDir, name)
    }
}

private fun autoGenLogFileName() =
    "ShortX-Log-${DateUtils.formatForFileName(System.currentTimeMillis())}.zip"