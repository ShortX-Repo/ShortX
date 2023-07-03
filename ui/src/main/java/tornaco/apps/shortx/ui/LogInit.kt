package tornaco.apps.shortx.ui

import android.content.Context
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.LOG_LEVEL_P
import tornaco.apps.shortx.core.util.ZipUtils
import tornaco.apps.shortx.core.util.logAdapter
import java.io.File
import java.io.IOException

val Context.logFolderPath get() = externalCacheDir.toString() + File.separator + "log"

fun initLogger(context: Context) {
    with(context) {
        val androidPrinter: com.elvishew.xlog.printer.Printer = AndroidPrinter()
        val filePrinter: com.elvishew.xlog.printer.Printer =
            com.elvishew.xlog.printer.file.FilePrinter.Builder(logFolderPath)
                .fileNameGenerator(DateFileNameGenerator())
                .flattener(ClassicFlattener())
                .build()
        XLog.init(
            com.elvishew.xlog.LogConfiguration.Builder()
                .logLevel(com.elvishew.xlog.LogLevel.ALL)
                .tag("ShortX-App")
                .build(),
            filePrinter,
            androidPrinter
        )

        logAdapter = { level: Int, tag: String, msg: String ->
            when (level) {
                Log.VERBOSE -> XLog.v("$tag $msg")
                Log.DEBUG -> XLog.d("$tag $msg")
                Log.INFO -> XLog.i("$tag $msg")
                Log.WARN -> XLog.w("$tag $msg")
                Log.ERROR -> XLog.e("$tag $msg")
                LOG_LEVEL_P -> if (BuildProp.APP_IS_DEBUG) XLog.d("$tag $msg")
            }
        }
    }
}

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