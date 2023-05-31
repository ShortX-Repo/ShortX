package tornaco.apps.shortx.ui

import android.content.Context
import android.util.Log
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import tornaco.apps.shortx.core.util.logAdapter
import java.io.File

val Context.logFolderPath get() = externalCacheDir.toString() + File.separator + "log"

fun initLogger(context: Context) {
    with(context) {
        val androidPrinter: com.elvishew.xlog.printer.Printer = AndroidPrinter()
        val filePrinter: com.elvishew.xlog.printer.Printer =
            com.elvishew.xlog.printer.file.FilePrinter.Builder(logFolderPath)
                .fileNameGenerator(DateFileNameGenerator())
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
            }
        }
    }
}