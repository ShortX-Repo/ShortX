package tornaco.apps.shortx

import android.content.Context
import android.util.Log
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.ui.settings.logFolderPath

fun initLogger(context: Context) {
    with(context) {
        val androidPrinter: com.elvishew.xlog.printer.Printer = AndroidPrinter()
        val filePrinter: com.elvishew.xlog.printer.Printer =
            com.elvishew.xlog.printer.file.FilePrinter.Builder(logFolderPath)
                .fileNameGenerator(DateFileNameGenerator())
                .flattener(ClassicFlattener())
                .cleanStrategy(FileLastModifiedCleanStrategy(60 * 1000 * 60 * 24 * 3 /* 3 Days */))
                .build()
        XLog.init(
            com.elvishew.xlog.LogConfiguration.Builder()
                .logLevel(if (BuildPropInternal.APP_IS_DEBUG) LogLevel.ALL else LogLevel.INFO)
                .tag("${BuildProp.LOG_PREFIX}App")
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